package com.example.apputil.redis.remote;

import java.util.Map;

public class SignerFeignFallBack implements SignerFeign {
    public static String ERROR_RES = null;

    @Override
    public String batchGenerateId(Map map) {
        return ERROR_RES;
    }

    @Override
    public Long generateId(Map map) {
        return null;
    }

    @Override
    public String selectAll() {
        return ERROR_RES;
    }

    @Override
    public String synUpdate(Map param) {
        return ERROR_RES;
    }
}
