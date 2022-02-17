package com.example.apputil.ons.api;


import com.example.apputil.ons.model.ConsumerInfoEntry;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public interface IConsumerService {

    void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle);

    void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle, MessageModel messageModel) throws Exception;
}
