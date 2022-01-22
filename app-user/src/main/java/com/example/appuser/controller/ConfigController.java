package com.example.appuser.controller;

import com.example.appuser.remote.IWebsocketFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description:
 * @author: wangwc
 * @date: 2021/1/19 21:04
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${user.config.url:http://xxx.xx.x.com}")
    private String config;

    @Resource
    IWebsocketFeign websocketFeign;

    @GetMapping("/get")
    public String getConfig(){
        return config;
    }

    @PostMapping("/testSendWebToHtml")
    public String testSendWebToHtml(@RequestBody Map map){
        return websocketFeign.sendWebMsg(map);
    }

}
