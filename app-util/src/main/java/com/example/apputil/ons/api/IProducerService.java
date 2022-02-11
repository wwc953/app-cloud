package com.example.apputil.ons.api;

public interface IProducerService {
    void sendMsg(String topic, String message, String msgKey);
}
