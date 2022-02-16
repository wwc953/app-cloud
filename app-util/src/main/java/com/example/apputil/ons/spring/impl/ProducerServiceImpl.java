package com.example.apputil.ons.spring.impl;

import com.example.apputil.ons.api.IProducerService;
import com.example.apputil.ons.constant.MqConstant;
import com.example.apputil.ons.factory.KafkaProducerFactory;
import com.example.apputil.ons.factory.OnsProducerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
public class ProducerServiceImpl implements IProducerService {
    private String binder;
    private OnsProducerFactory onsFactory;
    private KafkaProducerFactory kafkaFactory;

    @Override
    public void sendMsg(String topic, String message, String msgKey) {
        log.info("发送：topic:{},message:{},msgKey:{}", topic, message, msgKey);
        if (MqConstant.BINDER_ONS.equals(binder)) {
            MQProducer producer = onsFactory.getNormalProducer();
            Message msg = new Message(topic, MqConstant.DEFAULT_TAG, message.getBytes());
            msg.setKeys(msgKey);
            try {
                producer.sendOneway(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (MqConstant.BINDER_KAFKA.equals(binder)) {
            KafkaProducer<String, String> producer = kafkaFactory.getNormalProducer();
            producer.send(new ProducerRecord<>(topic, msgKey, message));
        } else {
            log.error("MQ binder异常，请检查配置文件中的binder参数，当前binder参数:{}", binder);
            throw new RuntimeException("配置属性mq.binder参数异常");
        }
    }

    @Override
    public void sendTimingMsg(String topic, String message, String msgKey, String formatTime) throws ParseException {
        if (MqConstant.BINDER_KAFKA.equals(binder)) {
            KafkaProducer<String, String> producer = kafkaFactory.getDelayProducer();
            long delayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(formatTime).getTime();
            producer.send(new ProducerRecord<>(topic, MqConstant.KAFKA_DEFAULT_PARTITION, delayTime, msgKey, message));
        }
    }

    @Override
    public void sendDelayMsg(String topic, String message, String msgKey, long millisecond) {
        if (MqConstant.BINDER_KAFKA.equals(binder)) {
            KafkaProducer<String, String> producer = kafkaFactory.getDelayProducer();
            long startDeliverTime = System.currentTimeMillis() + millisecond;
            producer.send(new ProducerRecord<>(topic, MqConstant.KAFKA_DEFAULT_PARTITION, startDeliverTime, msgKey, message));
        }
    }

    @Override
    public void sendShardingOrderMsg(String topic, String message, String msgKey, String shardingKey) {
        if (MqConstant.BINDER_KAFKA.equals(binder)) {
            KafkaProducer<String, String> producer = kafkaFactory.getOrderProducer();
            int partition = Integer.parseInt(shardingKey);
            producer.send(new ProducerRecord<>(topic, partition, msgKey, message));
        }
    }

    public String getBinder() {
        return binder;
    }

    public void setBinder(String binder) {
        this.binder = binder;
    }

    public OnsProducerFactory getOnsFactory() {
        return onsFactory;
    }

    public void setOnsFactory(OnsProducerFactory onsFactory) {
        this.onsFactory = onsFactory;
    }

    public KafkaProducerFactory getKafkaFactory() {
        return kafkaFactory;
    }

    public void setKafkaFactory(KafkaProducerFactory kafkaFactory) {
        this.kafkaFactory = kafkaFactory;
    }
}
