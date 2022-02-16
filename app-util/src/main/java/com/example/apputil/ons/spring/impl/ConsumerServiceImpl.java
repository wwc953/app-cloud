package com.example.apputil.ons.spring.impl;

import com.example.apputil.ons.api.IConsumerService;
import com.example.apputil.ons.api.MessageHandle;
import com.example.apputil.ons.bean.ConsumerInfoEntry;
import com.example.apputil.ons.factory.KafkaConsumerFactory;
import com.example.apputil.ons.factory.OnsConsumerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

@Slf4j
public class ConsumerServiceImpl implements IConsumerService {
    private String binder;
    private OnsConsumerFactory onsFactory;
    private KafkaConsumerFactory kafkaFactory;

    @Override
    public void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle) {
        if ("ons".equals(binder)) {
            log.info("ons consumerMsg");
            DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) onsFactory.getNoOrderConsumer(consumerInfo.getGroupId(), MessageModel.CLUSTERING, messageHandle);
            try {
                consumer.subscribe(consumerInfo.getTopic(), "Default");
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle, MessageModel messageModel) throws Exception {

    }

    public String getBinder() {
        return binder;
    }

    public void setBinder(String binder) {
        this.binder = binder;
    }

    public OnsConsumerFactory getOnsFactory() {
        return onsFactory;
    }

    public void setOnsFactory(OnsConsumerFactory onsFactory) {
        this.onsFactory = onsFactory;
    }

    public KafkaConsumerFactory getKafkaFactory() {
        return kafkaFactory;
    }

    public void setKafkaFactory(KafkaConsumerFactory kafkaFactory) {
        this.kafkaFactory = kafkaFactory;
    }
}
