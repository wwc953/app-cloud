package com.example.apputil.ons.factory;

import com.example.apputil.ons.model.MqProperties;
import com.example.apputil.ons.constant.PRODUCER_TYPE;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class KafkaProducerFactory {

    MqProperties props;

    ConcurrentHashMap<PRODUCER_TYPE, KafkaProducer<String, String>> cache = new ConcurrentHashMap();

    public KafkaProducerFactory(MqProperties props) {
        this.props = props;
    }

    public KafkaProducer<String, String> getNormalProducer() {
        KafkaProducer<String, String> producer = cache.get(PRODUCER_TYPE.NORMAL);
        return producer != null ? producer : createNormalProducer();
    }

    public KafkaProducer<String, String> getDelayProducer() {
        KafkaProducer<String, String> producer = cache.get(PRODUCER_TYPE.DELAY);
        return producer != null ? producer : createDelayProducer();
    }

    public KafkaProducer<String, String> getOrderProducer() {
        KafkaProducer<String, String> producer = cache.get(PRODUCER_TYPE.SHARDING_ORDER);
        return producer != null ? producer : createShardingProducer();
    }

    private KafkaProducer<String, String> createProducer(PRODUCER_TYPE type) {
        if (type != PRODUCER_TYPE.NORMAL && type != PRODUCER_TYPE.DELAY && type != PRODUCER_TYPE.SHARDING_ORDER) {
            log.error("PRODUCER_TYPE异常:{}", type);
            return null;
        } else {
            return new KafkaProducer(covertProperties(props));
        }
    }

    private KafkaProducer<String, String> createNormalProducer() {
        KafkaProducer<String, String> producer = createProducer(PRODUCER_TYPE.NORMAL);
        printStartLogs(props, PRODUCER_TYPE.NORMAL);
        cache.put(PRODUCER_TYPE.NORMAL, producer);
        return producer;
    }

    private KafkaProducer<String, String> createDelayProducer() {
        KafkaProducer<String, String> producer = createProducer(PRODUCER_TYPE.DELAY);
        printStartLogs(props, PRODUCER_TYPE.DELAY);
        cache.put(PRODUCER_TYPE.DELAY, producer);
        return producer;
    }

    private KafkaProducer<String, String> createShardingProducer() {
        KafkaProducer<String, String> producer = createProducer(PRODUCER_TYPE.SHARDING_ORDER);
        printStartLogs(props, PRODUCER_TYPE.SHARDING_ORDER);
        cache.put(PRODUCER_TYPE.SHARDING_ORDER, producer);
        return producer;
    }

    public MqProperties getProps() {
        return props;
    }

    public void setProps(MqProperties props) {
        this.props = props;
    }

    private Properties covertProperties(MqProperties props) {
        if (props.hasNullProperties()) {
            log.error("Kafka相关配置文件存在为空，请检查相关配置");
            return null;
        }
        Properties properties = new Properties();
        properties.put("bootstrap.servers", props.getConnect());
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        properties.put("max.block.ms", 30000);
        properties.put("retries", 5);
        properties.put("reconnect.backoff.ms", 3000);
        return properties;
    }

    private void printStartLogs(MqProperties props, PRODUCER_TYPE type) {
        log.info("Kafka-" + type.name() + "生产者实例装配成功");
        log.info("zookeeper连接地址是：" + props.getConnect());
    }

}
