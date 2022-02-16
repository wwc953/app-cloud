package com.example.apputil.ons.factory;

import com.example.apputil.ons.bean.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class KafkaConsumerFactory {
    MqProperties props;
    ConcurrentHashMap<String, KafkaConsumer<String, String>> cache = new ConcurrentHashMap();

    public KafkaConsumerFactory(MqProperties props) {
        this.props = props;
    }

    public KafkaConsumer<String, String> getNormalConsumer(String groupId) {
        KafkaConsumer<String, String> consumer = cache.get(groupId);
        return consumer != null ? consumer : createNormalConsumer(groupId);
    }

    private KafkaConsumer<String, String> createNormalConsumer(String groupId) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(covertProperties(props, groupId));
        printStartLogs(props, groupId);
        cache.put(groupId, consumer);
        return consumer;
    }

    private Properties covertProperties(MqProperties props, String groupId) {
        if (props.hasNullProperties()) {
            log.error("Kafka相关配置文件存在为空，请检查相关配置");
            return null;
        }
        Properties properties = new Properties();
        properties.put("bootstrap.servers", props.getConnect());
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        properties.put("session.timeout.ms", 30000);
        properties.put("max.poll.records", 10);
        properties.put("group.id", groupId);
        return properties;
    }

    private void printStartLogs(MqProperties props, String groupId) {
        log.info("Kafka-消费者实例装配成功");
        log.info("GroupId是:" + groupId);
        log.info("zookeeper连接地址是：" + props.getConnect());
    }
}
