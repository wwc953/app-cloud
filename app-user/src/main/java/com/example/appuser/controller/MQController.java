package com.example.appuser.controller;

import com.example.appuser.ons.api.IProducerService;
import com.example.appuser.service.PeopleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/mq")
public class MQController {

    @Value("${mq.userTopic}")
    private String userTopic;

    @Autowired
    IProducerService producerService;

    @GetMapping("/sendOneway/{msg}")
    public void sendOneway(@PathVariable String msg) {
        producerService.sendMsg(userTopic, msg, System.currentTimeMillis() + "");
    }

}
