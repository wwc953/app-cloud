package com.example.appuser.ons.api;

public interface IProducerService {
    void sendMsg(String topic, String message, String msgKey);
}
