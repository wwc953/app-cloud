package com.example.apputil.ons.config;


import com.example.apputil.ons.api.IConsumerService;
import com.example.apputil.ons.api.IProducerService;
import com.example.apputil.ons.bean.MqProperties;
import com.example.apputil.ons.constant.MqConstant;
import com.example.apputil.ons.factory.KafkaConsumerFactory;
import com.example.apputil.ons.factory.KafkaProducerFactory;
import com.example.apputil.ons.factory.OnsConsumerFactory;
import com.example.apputil.ons.factory.OnsProducerFactory;
import com.example.apputil.ons.spring.impl.ConsumerServiceImpl;
import com.example.apputil.ons.spring.impl.ProducerServiceImpl;
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
        if (MqConstant.BINDER_ONS.equals(binder)) {
            consumerService.setBinder(MqConstant.BINDER_ONS);
            consumerService.setOnsFactory(initOnsConsumerFactory());
        }
        if (MqConstant.BINDER_KAFKA.equals(binder)) {
            consumerService.setBinder(MqConstant.BINDER_KAFKA);
            consumerService.setKafkaFactory(initKafkaConsumerFactory());
        }
        return consumerService;
    }

    @Primary
    @Bean("defaultProducerService")
    public IProducerService producerService() {
        ProducerServiceImpl producerService = new ProducerServiceImpl();
        String binder = getMqProps().getBinder();
        if (MqConstant.BINDER_ONS.equals(binder)) {
            producerService.setBinder(MqConstant.BINDER_ONS);
            producerService.setOnsFactory(initOnsProducerFactory());
        }
        if (MqConstant.BINDER_KAFKA.equalsIgnoreCase(binder)) {
            producerService.setBinder(MqConstant.BINDER_KAFKA);
            producerService.setKafkaFactory(initKafkaProducerFactory());
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

    @Bean("defaultKafkaConsumerFactory")
    public KafkaConsumerFactory initKafkaConsumerFactory() {
        return new KafkaConsumerFactory(getMqProps());
    }

    @Bean("defaultKafkaProducerFactory")
    public KafkaProducerFactory initKafkaProducerFactory() {
        return new KafkaProducerFactory(getMqProps());
    }

}
