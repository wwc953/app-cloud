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
    public List<SnoSt> selectAll() {
        return signerService.selectAll();
    }

    @PostMapping("/synUpdate")
    public Integer synUpdate(Map map) {
        log.info("synUpdate --> {}", JsonUtil.convertMapToJson(map));
        Integer synUpdate = signerService.synUpdate(map);
        log.info("synUpdate <-- {}", synUpdate);
        return synUpdate;
    }
}
