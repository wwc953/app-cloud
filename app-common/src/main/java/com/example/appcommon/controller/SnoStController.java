package com.example.appcommon.controller;

import com.example.appcommon.bean.SnoSt;
import com.example.appcommon.service.SignerService;
import com.example.apputil.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/member/snost")
public class SnoStController {

    @Autowired
    SignerService signerService;

    @PostMapping("/selectAll")
    public String selectAll() {
        log.info("server selectAll: begin");
        List<SnoSt> snoSts = signerService.selectAll();
        String res = JsonUtil.convertObjectToJson(snoSts);
        log.info("server selectAll: {}", res);
        return res;
    }

    @PostMapping("/synUpdate")
    public Integer synUpdate(Map map) {
        log.info("synUpdate --> {}", JsonUtil.convertMapToJson(map));
        Integer synUpdate = signerService.synUpdate(map);
        log.info("synUpdate <-- {}", synUpdate);
        return synUpdate;
    }
}
