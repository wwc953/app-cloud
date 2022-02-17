package com.example.apputil.sync.impl.nacos;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.example.apputil.sync.ISyncService;
import com.example.apputil.sync.SyncListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@ConditionalOnClass({ConfigService.class, NacosConfigProperties.class})
public class NacosClientConfig {
    private ConcurrentHashMap<SyncListener, Listener> listenerCache = new ConcurrentHashMap<>();

    @Bean
    @ConditionalOnMissingBean
    public ISyncService nacosServiceImpl(@Autowired(required = false) NacosConfigProperties properties) {
        ConfigService configService = properties.configServiceInstance();
        ISyncService impl = new ISyncService() {
            @Override
            public void addListener(String dataId, String group, SyncListener listener) {
                try {
                    Listener temp = new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return null;
                        }

                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            log.info("nacos收到消息:{}", configInfo);
                            listener.recevice(configInfo);
                        }
                    };
                    configService.addListener(dataId, group, temp);
                    listenerCache.put(listener, temp);
                } catch (NacosException e) {
                    log.error("创建nacos监听器失败", e);
                }

            }

            @Override
            public boolean publish(String dataId, String group, String content) {
                try {
                    log.info("发布消息:{}, dataId:{}, groupId:{}", content, dataId, group);
                    return configService.publishConfig(dataId, group, content);
                } catch (NacosException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void removeListener(String dataId, String group, SyncListener listener) {
                Listener temp = listenerCache.get(listener);
                if (temp == null) {
                    return;
                }
                configService.removeListener(dataId, group, temp);
                listenerCache.remove(listener);
            }
        };
        return impl;
    }
}
