package com.example.apputil.redis.feign;

import com.example.apputil.redis.cache.CaffeineCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class CommonParamManager {

    public static String appName;

    public static String centerNo;

    public static CaffeineCache cache;

    public static FeignInvoke feignInvoke;

    public static String REDIS_KEYS = "redisKeys";

    @Autowired
    public void setCache(CaffeineCache cache) {
        CommonParamManager.cache = cache;
    }

    @Autowired
    public static void setFeignInvoke(FeignInvoke feignInvoke) {
        CommonParamManager.feignInvoke = feignInvoke;
    }

    @Value("${spring.application.name}")
    public void setCenterNo(String appName) {
        String[] split = appName.split("-");
        centerNo = split[1];
        CommonParamManager.appName = appName;
        Assert.notNull(centerNo, "获取中心编号失败");
    }

    public static void storeRedisKeys(boolean needCheck) {
        List<RedisManagerObj> redisKeys = feignInvoke.getRedisMgt(appName, needCheck);
        if (redisKeys != null && redisKeys.size() > 0) {
            cache.del(REDIS_KEYS);
            cache.put(REDIS_KEYS, redisKeys);
        }
    }

    public static String getAppName() {
        return appName;
    }

    public static  String getDataCenterId(){
        return cache.getString("dataCenterId");
    }
}
