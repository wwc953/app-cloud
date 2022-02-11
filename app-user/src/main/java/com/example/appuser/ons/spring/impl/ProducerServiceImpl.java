package com.example.appuser.ons.spring.impl;

import com.example.appuser.ons.api.IProducerService;
import com.example.appuser.ons.factory.OnsProducerFactory;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;

public class ProducerServiceImpl implements IProducerService {
    private String binder;
    private OnsProducerFactory onsFactory;

    @Override
    public void sendMsg(String topic, String message, String msgKey) {
        MQProducer producer = onsFactory.getNormalProducer();
        Message msg = new Message(topic, "Default", message.getBytes());
        msg.setKeys(msgKey);
        try {
            producer.sendOneway(msg);
        } catch (Exception e) {
            e.printStackTrace();
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
}
