package com.example.apputil.redis.remote;

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
}
