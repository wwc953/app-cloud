package com.example.apputil.redis.service;

import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.constants.CmcConstants;
import com.example.apputil.redis.model.NumberStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NumberStrategyUtil {

    @Autowired
    CaffeineCache cache;

    public NumberStrategy getStrategyByStNo(String stNo) {
        return (NumberStrategy) Optional.ofNullable(cache.hget(CmcConstants.NO_STRATEGY_IN_REDIS, stNo)).orElse(null);
    }
}
