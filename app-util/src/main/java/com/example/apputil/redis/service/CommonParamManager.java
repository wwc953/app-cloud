package com.example.apputil.redis.service;

import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.redis.feign.FeignInvoke;
import com.example.apputil.sync.ISyncService;
import com.example.apputil.sync.SyncListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CommonParamManager {

    private static ISyncService syncService;
    private static CaffeineCache cache;
    private static FeignInvoke feignInvoke;

    @Autowired
    public void setCache(CaffeineCache cache) {
        CommonParamManager.cache = cache;
    }

    @Autowired
    public void setFeignInvoke(FeignInvoke feignInvoke) {
        CommonParamManager.feignInvoke = feignInvoke;
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

}
