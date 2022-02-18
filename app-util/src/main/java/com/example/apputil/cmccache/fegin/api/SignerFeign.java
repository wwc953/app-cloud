package com.example.apputil.cmccache.fegin.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface SignerFeign {
    @PostMapping("/member/signer/batchGenerateId")
    String batchGenerateId(@RequestBody Map map);

    @PostMapping("/member/signer/generateId")
    Long generateId(@RequestBody Map map);

    @PostMapping("/member/snost/getSnoStList")
    String getSnoStList();

    @PostMapping("/member/snost/synUpdate")
    String synUpdate(Map param);

    @PostMapping("/member/centerDataApi/getDataCenterId")
    String getDataCenterId();
}
