package com.example.apputil.ons.api;

import java.text.ParseException;

public interface IProducerService {
    void sendMsg(String topic, String message, String msgKey);

    void sendTimingMsg(String topic, String message, String msgKey, String formatTime) throws ParseException;

    void sendDelayMsg(String topic, String message, String msgKey, long millisecond);

    void sendShardingOrderMsg(String topic, String message, String msgKey, String shardingKey);
}
