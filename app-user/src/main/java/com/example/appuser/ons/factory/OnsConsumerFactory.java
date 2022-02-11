package com.example.appuser.ons.factory;

import com.example.appuser.ons.api.MessageHandle;
import com.example.appuser.ons.bean.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class OnsConsumerFactory {
    MqProperties props;

    ConcurrentHashMap<String, MQAdmin> cache = new ConcurrentHashMap();

    public OnsConsumerFactory(MqProperties props) {
        this.props = props;
    }


    public MQConsumer getNoOrderConsumer(String groupId, MessageModel model, MessageHandle messageHandle) {
        MQAdmin admin = cache.get(groupId);
        return admin != null ? (MQConsumer) admin : createNoOrderConsumer(groupId, model, messageHandle);
    }

    private MQConsumer createNoOrderConsumer(String groupId, MessageModel model, MessageHandle messageHandle) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setConsumerGroup(groupId);
        consumer.setNamesrvAddr(props.getNamesrvAddr());
        consumer.setMessageModel(model);
        consumer.setConsumeMessageBatchMaxSize(1);
        try {
            consumer.registerMessageListener(createMessageListener(messageHandle));
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        cache.put(groupId, consumer);
        log.info("rocketmq consumer开启成功-------groupId={}----------.", groupId);
        return consumer;
    }

    private MessageListenerConcurrently createMessageListener(MessageHandle messageHandle) {
        return new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("进入MessageListenerConcurrently");
                log.info("msgs长度：{}", msgs.size());
                MessageExt messageExt = msgs.get(0);
                log.info("messageExt[0]:{}", messageExt);
                boolean result = messageHandle.handle(messageExt.getKeys(), new String(messageExt.getBody()));
                log.info("result======{}", result);
                return result ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        };
    }


}
