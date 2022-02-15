package com.example.appuser.controller;

import com.example.appstaticutil.json.JsonUtil;
import com.example.apputil.redis.service.InitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/member/signer")
public class SignerController {

    @Autowired
    InitService initService;

    @PostMapping("/generateId")
    public Long generateId(@RequestBody Map map) {
        return initService.generateId(map);
    }

    @PostMapping("/batchGenerateId")
    public String batchGenerateId(@RequestBody Map map) {
        log.info("controller batchGenerateId入参：{}", JsonUtil.convertObjectToJson(map));
        String result = initService.batchGenerateId(map);
        log.info("result:{}", result);
        return result;
    }

}
