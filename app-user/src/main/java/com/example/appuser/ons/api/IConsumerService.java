package com.example.appuser.ons.api;

import com.example.appuser.ons.bean.ConsumerInfoEntry;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public interface IConsumerService {

    void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle);

    void consumerMsg(ConsumerInfoEntry consumerInfo, MessageHandle messageHandle, MessageModel messageModel) throws Exception;
}
