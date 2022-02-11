package com.example.apputil.redis.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description:
 * @author: wangwc
 * @date: 2020/11/27 15:58
 */
@Configuration
public class SpringRedisConfig {

    @Bean({"cacheRedisTemplate"})
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) throws Throwable {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean({"cacheStringRedisTemplate"})
    public StringRedisTemplate StringRedisTemplate(RedisConnectionFactory factory) throws Throwable {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }
}
