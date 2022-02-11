package com.example.apputil.ons.spring.impl;

import com.example.apputil.ons.api.IProducerService;
import com.example.apputil.ons.factory.OnsProducerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;

@Slf4j
public class ProducerServiceImpl implements IProducerService {
    private String binder;
    private OnsProducerFactory onsFactory;

    @Override
    public void sendMsg(String topic, String message, String msgKey) {
        log.info("发送：topic:{},message:{},msgKey:{}", topic, message, msgKey);
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
