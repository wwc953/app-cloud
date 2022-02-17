package com.example.apputil.ons.spring.impl;

import com.example.apputil.ons.api.IConsumerService;
import com.example.apputil.ons.api.MessageHandle;
import com.example.apputil.ons.model.ConsumerInfoEntry;
import com.example.apputil.ons.constant.MqConstant;
import com.example.apputil.ons.factory.KafkaConsumerFactory;
import com.example.apputil.ons.factory.OnsConsumerFactory;
import com.example.apputil.ons.kafka.KafkaThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

@Slf4j
public class ConsumerServiceImpl implements IConsumerService {
    private String binder;
    private OnsConsumerFactory onsFactory;
    private KafkaConsumerFactory kafkaFactory;

    @Override
    public void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle) {
        if (MqConstant.BINDER_ONS.equals(binder)) {
            log.info("ons consumerMsg");
            DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) onsFactory.getNoOrderConsumer(consumerInfo.getGroupId(), MessageModel.CLUSTERING, messageHandle);
            try {
                consumer.subscribe(consumerInfo.getTopic(), "Default");
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        } else if (MqConstant.BINDER_KAFKA.equals(binder)) {
            KafkaConsumer<String, String> kafkaConsumer = kafkaFactory.getNormalConsumer(consumerInfo.getGroupId());
            kafkaConsumer.subscribe(Arrays.asList(consumerInfo.getTopic()));
            executeConsumeThread(kafkaConsumer, messageHandle);
        } else {
            log.error("MQ binder异常，请检查配置文件中的binder参数，当前binder参数:{}", binder);
            throw new RuntimeException("配置属性mq.binder参数异常");
        }
    }


    @Override
    public void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle, MessageModel messageModel) throws Exception {
        if (MqConstant.BINDER_ONS.equals(binder)) {

            DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) onsFactory.getNoOrderConsumer(consumerInfo.getGroupId(), messageModel, messageHandle);
            try {
                consumer.subscribe(consumerInfo.getTopic(), "Default");
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        } else if (MqConstant.BINDER_KAFKA.equals(binder)) {
            KafkaConsumer<String, String> kafkaConsumer = null;
            if (MessageModel.BROADCASTING.name().equals(messageModel.name())) {
                kafkaConsumer = kafkaFactory.getNormalConsumer(groupRandomHandle(consumerInfo.getGroupId()));
            } else {
                kafkaConsumer = kafkaFactory.getNormalConsumer(consumerInfo.getGroupId());
            }
            kafkaConsumer.subscribe(Arrays.asList(consumerInfo.getTopic()));
            executeConsumeThread(kafkaConsumer, messageHandle);
        } else {
            log.error("MQ binder异常，请检查配置文件中的binder参数，当前binder参数:{}", binder);
            throw new RuntimeException("配置属性mq.binder参数异常");
        }
    }

    private void executeConsumeThread(KafkaConsumer<String, String> kafkaConsumer, MessageHandle messageHandle) {
        new Thread(() -> {
            while (true) {
                try {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000L));
                    Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
                    while (iterator.hasNext()) {
                        ConsumerRecord<String, String> record = iterator.next();
                        try {
                            KafkaThreadPool.getInstance().execute(() -> {
                                messageHandle.handle(record.key(), record.value());
                            });
                        } catch (Exception e) {
                            log.error("KafkaThreadPool执行execute异常", e);
                        }
                    }
                } catch (Exception e) {
                    log.error("kafkaConsumer在poll消息出现异常", e);
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String groupRandomHandle(String groupId) {
        StringBuffer sb = new StringBuffer();
        sb.append(groupId).append(UUID.randomUUID().toString());
        return sb.toString();
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
