package com.example.apputil.ons.factory;

import com.example.apputil.ons.model.MqProperties;
import com.example.apputil.ons.constant.PRODUCER_TYPE;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class OnsProducerFactory {
    MqProperties props;

    ConcurrentHashMap<PRODUCER_TYPE, MQAdmin> cache = new ConcurrentHashMap();

    public OnsProducerFactory(MqProperties props) {
        this.props = props;
    }

    public MQProducer getNormalProducer() {
        MQAdmin admin = cache.get(PRODUCER_TYPE.NORMAL);
        return admin != null ? (MQProducer) admin : createNormalProducer();
    }

    private MQProducer createNormalProducer() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(props.getNamesrvAddr());
        producer.setProducerGroup("default");
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("rocketmq producer 开启成功---------------------------------.");
        cache.put(PRODUCER_TYPE.NORMAL, producer);
        return producer;
    }

}
