package com.example.appcommon.controller;

import com.example.appcommon.model.SnoSt;
import com.example.appcommon.service.SignerService;
import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.response.ResponseResult;
import com.example.apputil.sync.util.CmcPublishUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/getSnoStList")
    public ResponseResult<List<SnoSt>> getSnoStList() {
        List<SnoSt> snoSts = signerService.getSnoStList();
        return ResponseResult.success(snoSts);
    }

    @PostMapping("/synUpdate")
    public Integer synUpdate(Map map) {
        log.info("synUpdate --> {}", JsonUtil.convertMapToJson(map));
        Integer synUpdate = signerService.synUpdate(map);
        log.info("synUpdate <-- {}", synUpdate);
        return synUpdate;
    }

    @PostMapping("/nostrategyPublish")
    public String nostrategyPublish() {
        CmcPublishUtil.NostrategyPublish();
        return "success";
    }
}
