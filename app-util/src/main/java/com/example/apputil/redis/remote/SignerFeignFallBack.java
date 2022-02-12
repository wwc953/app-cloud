package com.example.apputil.redis.remote;

import com.example.apputil.redis.bean.SnoSt;

import java.util.List;
import java.util.Map;

public class SignerFeignFallBack implements SignerFeign {
    @Override
    public String batchGenerateId(Map map) {
        return null;
    }

    @Override
    public Long generateId(Map map) {
        return null;
    }

    @Override
    public String selectAll() {
        return null;
    }

    @Override
    public String synUpdate(Map param) {
        return null;
    }
}
