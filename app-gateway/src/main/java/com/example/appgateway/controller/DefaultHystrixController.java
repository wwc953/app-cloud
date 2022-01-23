package com.example.appgateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultHystrixController {

    @RequestMapping(value = "/fallback",method = RequestMethod.GET)
    public String fallback(){
        System.out.println("fallback****************Gateway");
//        return Result.builder().code(500).msg("您访问的接口超时。。。").build();
        return "您访问的接口超时...";
    }
}
