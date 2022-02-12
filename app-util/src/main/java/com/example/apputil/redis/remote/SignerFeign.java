package com.example.apputil.redis.remote;

import com.example.apputil.redis.bean.SnoSt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface SignerFeign {
    @PostMapping("/member/signer/batchGenerateId")
    String batchGenerateId(@RequestBody Map map);

    @PostMapping("/member/signer/generateId")
    Long generateId(@RequestBody Map map);

    @PostMapping("/member/snost/selectAll")
    String selectAll();

    @PostMapping("/member/snost/synUpdate")
    String synUpdate(Map param);
}
