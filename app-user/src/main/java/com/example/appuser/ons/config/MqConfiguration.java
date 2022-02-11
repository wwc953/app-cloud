package com.example.appuser.ons.config;

import com.example.appuser.ons.api.IConsumerService;
import com.example.appuser.ons.api.IProducerService;
import com.example.appuser.ons.bean.MqProperties;
import com.example.appuser.ons.factory.OnsConsumerFactory;
import com.example.appuser.ons.factory.OnsProducerFactory;
import com.example.appuser.ons.spring.impl.ConsumerServiceImpl;
import com.example.appuser.ons.spring.impl.ProducerServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 初始化mq实例
 */
@Configuration
public class MqConfiguration {

    @Bean("defaultMqProps")
    @ConfigurationProperties(prefix = "mq")
    public MqProperties getMqProps() {
        return new MqProperties();
    }

    @Primary
    @Bean("defaultConsumerService")
    public IConsumerService consumerService() {
        ConsumerServiceImpl consumerService = new ConsumerServiceImpl();
        String binder = getMqProps().getBinder();
        if ("ons".equals(binder)) {
            consumerService.setBinder("ons");
            consumerService.setOnsFactory(initOnsConsumerFactory());
        }
        return consumerService;
    }

    @Primary
    @Bean("defaultProducerService")
    public IProducerService producerService() {
        ProducerServiceImpl producerService = new ProducerServiceImpl();
        String binder = getMqProps().getBinder();
        if ("ons".equals(binder)) {
            producerService.setBinder("ons");
            producerService.setOnsFactory(initOnsProducerFactory());
        }
        return producerService;
    }

    @Bean("defaultOnsConsumerFactory")
    public OnsConsumerFactory initOnsConsumerFactory() {
        return new OnsConsumerFactory(getMqProps());
    }

    @Bean("defaultOnsProducerFactory")
    public OnsProducerFactory initOnsProducerFactory() {
        return new OnsProducerFactory(getMqProps());
    }


}
