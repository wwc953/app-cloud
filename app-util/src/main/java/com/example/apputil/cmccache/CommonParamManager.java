package com.example.apputil.cmccache;

import com.example.appstaticutil.model.RedisManagerObj;
import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.cmccache.fegin.invoke.FeignInvoke;
import com.example.apputil.sync.ISyncService;
import com.example.apputil.sync.SyncListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Component
public class CommonParamManager {

    public static String appName;

    public static String centerNo;

    public static CaffeineCache cache;

    public static FeignInvoke feignInvoke;

    private static ISyncService syncService;

    public static String REDIS_KEYS = "redisKeys";

    @Autowired
    public void setCache(CaffeineCache cache) {
        CommonParamManager.cache = cache;
    }

    @Autowired
    public void setFeignInvoke(FeignInvoke feignInvoke) {
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

    @Autowired
    public void setSyncService(ISyncService syncService) {
        CommonParamManager.syncService = syncService;
    }

    public static void doListenerInitialize(List<String> types) {
        if (syncService == null) {
            log.error("ISyncService实例注册失败，将不注册公共组件监听");
            return;
        }

        if (types.contains("NUMBERSTRATEGY")) {
            syncService.addListener("CMC_ST_NO_DATA_ID", "CMC_PUBLISH", new SyncListener() {
                @Override
                public void recevice(String content) {
                    try {
                        feignInvoke.getNoStList(false);
                    } catch (Exception e) {
                        log.error("写入流水号策略失败");
                    }
                }
            });
        }
    }

    public static String getAppName() {
        return appName;
    }

    public static  String getDataCenterId(){
        return cache.getString("dataCenterId");
    }
}
