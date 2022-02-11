package com.example.appuser.controller;

import com.example.apputil.redis.api.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/id")
public class IDController {

    @Autowired
    IRedisService redisService;

    @GetMapping("/getID/{msg}")
    public String sendOnewayorder(@PathVariable String msg) {
        return redisService.getID(msg, null);
    }

}
