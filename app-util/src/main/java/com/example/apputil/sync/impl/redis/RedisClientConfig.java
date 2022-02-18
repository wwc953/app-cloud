package com.example.apputil.sync.impl.redis;

import com.example.apputil.sync.ISyncService;
import com.example.apputil.sync.SyncListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis 监听
 */
@Slf4j
@Configuration
@ConditionalOnMissingClass({"com.alibaba.nacos.api.config.ConfigService", "com.alibaba.cloud.nacos.NacosConfigProperties"})
@EnableConfigurationProperties({GlobalRedisProperties.class})
public class RedisClientConfig {

    @Autowired
    GlobalRedisProperties properties;

    private ConcurrentHashMap<SyncListener, MessageListener> cache = new ConcurrentHashMap<>();

    @Bean("globalRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        return createJedisConnectionFactory();
    }

    @Bean("globalStringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("globalRedisConnectionFactory") RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    @Bean("globalRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        return container;
    }

    @Bean
    @ConditionalOnMissingBean
    public ISyncService redisServiceImpl(@Qualifier("globalStringRedisTemplate") StringRedisTemplate template,
                                         @Qualifier("globalRedisMessageListenerContainer") RedisMessageListenerContainer redisContainer) {
        return new ISyncService() {
            @Override
            public void addListener(String dataId, String group, SyncListener listener) {
                String topic = getkey(dataId, group);
                MessageListener messageListener = new MessageListener() {
                    @Override
                    public void onMessage(Message message, byte[] pattern) {
                        listener.recevice(new String(message.getBody()));
                    }
                };
                redisContainer().addMessageListener(messageListener, new ChannelTopic(topic));
                cache.put(listener, messageListener);

            }

            @Override
            public boolean publish(String dataId, String group, String content) {
                template.convertAndSend(getkey(dataId, group), content);
                return true;
            }

            @Override
            public void removeListener(String dataId, String group, SyncListener listener) {
                MessageListener messageListener = cache.get(listener);
                if (messageListener != null) {
                    redisContainer.removeMessageListener(messageListener, new ChannelTopic(getkey(dataId, group)));
                    cache.remove(messageListener);
                }
            }

            private String getkey(String dataId, String group) {
                return group + ":" + dataId;
            }
        };
    }


    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration();
        if (getSentinelConfig() != null) {
            return new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
        } else {
            return getClusterConfiguration() != null ? new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration)
                    : new JedisConnectionFactory(getStandaloneConfiguration(), clientConfiguration);
        }
    }


    private JedisClientConfiguration getJedisClientConfiguration() {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        RedisProperties.Pool pool = properties.getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        return builder.build();
    }


    private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }


    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            boolean useSsl = url.startsWith("redis://");
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(58);
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }
            return new ConnectionInfo(uri, useSsl, password);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Malformed url '" + url + "' ", e);
        }
    }


    private void applyPooling(RedisProperties.Pool pool, JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }


    private GenericObjectPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }

    protected RedisSentinelConfiguration getSentinelConfig() {
        GlobalRedisProperties.Sentinel propertiesSentinel = properties.getSentinel();
        if (propertiesSentinel == null) {
            return null;
        }
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.setMaster(propertiesSentinel.getMaster());
        config.setSentinels(createSentinels(propertiesSentinel));
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    private Iterable<RedisNode> createSentinels(GlobalRedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        Iterator<String> iterator = sentinel.getNodes().iterator();
        while (iterator.hasNext()) {
            String node = iterator.next();
            String[] parts = StringUtils.split(node, ":");
            Assert.state(parts.length == 2, "Must be defined as 'host:prot'");
            nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
        }
        return nodes;
    }

    protected RedisClusterConfiguration getClusterConfiguration() {
        GlobalRedisProperties.Cluster propertiesCluster = properties.getCluster();
        if (propertiesCluster == null) {
            return null;
        }
        RedisClusterConfiguration config = new RedisClusterConfiguration(propertiesCluster.getNodes());
        if (propertiesCluster.getMaxRedirects() != null) {
            config.setMaxRedirects(propertiesCluster.getMaxRedirects());
        }
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }


    protected RedisStandaloneConfiguration getStandaloneConfiguration() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(properties.getUrl());
//            config.setHostName(connectionInfo.getHost());
//            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }


    private JedisClientConfiguration.JedisClientConfigurationBuilder applyProperties(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        if (properties.isSsl()) {
            builder.useSsl();
        }
        if (properties.getTimeout() != null) {
            Duration timeout = properties.getTimeout();
            builder.readTimeout(timeout).connectTimeout(timeout);
        }
        return builder;
    }


    protected static class ConnectionInfo {
        private URI uri;
        private boolean useSsl;
        private String password;

        public ConnectionInfo(URI uri, boolean useSsl, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.password = password;
        }

        public URI getUri() {
            return uri;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }

        public boolean isUseSsl() {
            return useSsl;
        }

        public void setUseSsl(boolean useSsl) {
            this.useSsl = useSsl;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
