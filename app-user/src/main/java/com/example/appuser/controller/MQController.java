package com.example.appuser.controller;

import com.example.apputil.ons.api.IProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/mq")
public class MQController {

    @Value("${mq.userTopic}")
    private String userTopic;

    @Value("${mq.orderTopic}")
    private String orderTopic;

    @Autowired
    IProducerService producerService;

    @GetMapping("/user/sendOneway/{msg}")
    public void sendOneway(@PathVariable String msg) {
        producerService.sendMsg(userTopic, msg, System.currentTimeMillis() + "");
    }

    @GetMapping("/order/sendOneway/{msg}")
    public void sendOnewayorder(@PathVariable String msg) {
        producerService.sendMsg(orderTopic, msg, System.currentTimeMillis() + "");
    }

}
