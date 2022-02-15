package com.example.appcommon.controller;

import com.example.appcommon.service.SignerService;
import com.example.appstaticutil.json.JsonUtil;
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
    SignerService signerService;

    @PostMapping("/generateId")
    public Long generateId(@RequestBody Map map) {
        return signerService.generateId(map);
    }

    @PostMapping("/batchGenerateId")
    public String batchGenerateId(@RequestBody Map map) {
        log.info("controller batchGenerateId入参：{}", JsonUtil.convertObjectToJson(map));
        String result = signerService.batchGenerateId(map);
        log.info("result:{}", result);
        return result;
    }

}
