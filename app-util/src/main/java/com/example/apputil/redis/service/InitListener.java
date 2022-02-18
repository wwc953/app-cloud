package com.example.apputil.redis.service;

import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.redis.feign.FeignInvoke;
import com.example.apputil.sync.ISyncService;
import com.example.apputil.sync.SyncListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@ConditionalOnExpression("${frame.cmccache.use:true}")
public class InitListener {

    private static ISyncService syncService;
    private static CaffeineCache cache;
    private static FeignInvoke feignInvoke;

    private List<String> types;

    @Autowired
    public static void setCache(CaffeineCache cache) {
        InitListener.cache = cache;
    }

    @Autowired
    public static void setFeignInvoke(FeignInvoke feignInvoke) {
        InitListener.feignInvoke = feignInvoke;
    }

    @Autowired(required = false)
    public static void setSyncService(ISyncService syncService) {
        InitListener.syncService = syncService;
    }

    @Value("${frame.cmccahe.use.type:numberstrategy,rediskeymanager,user,org}")
    public void setTypes(String type) {
        try {
            type = type.replace("_", "").toUpperCase();
            String[] typeArr = type.split(",");
            this.types = Arrays.asList(typeArr);
        } catch (Exception e) {
            this.types = new ArrayList<>();
        }
    }

    public static void doListenerInitialize(List<String> types) {
        log.info("该应用使用的公共缓存类型:{}", String.valueOf(types));
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

}
