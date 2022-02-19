package com.example.apputil.cmccache;

import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.model.RedisManagerObj;
import com.example.appstaticutil.threadpool.ThreadPoolManager;
import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.cmccache.fegin.invoke.FeignInvoke;
import com.example.apputil.constants.CmcConstants;
import com.example.apputil.redis.model.NumberStrategy;
import com.example.apputil.sync.ISyncService;
import com.example.apputil.sync.SyncListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    public void setSyncService(ISyncService syncService) {
        CommonParamManager.syncService = syncService;
    }

    /**
     * 创建监听实例
     *
     * @param types
     */
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
                        storeNoStrategy(false);
                    } catch (Exception e) {
                        log.error("写入流水号策略失败", e);
                    }
                }
            });
        }
        if (types.contains("CENTERDATAID")) {
            syncService.addListener("CMC_ST_NO_DATA_ID", "CMC_PUBLISH", new SyncListener() {
                @Override
                public void recevice(String content) {
                    try {
                        storeDataCenterId();
                    } catch (Exception e) {
                        log.error("写入数据中心参数失败。", e);
                    }
                }
            });
        }
    }

    /**
     * 执行所有定时任务
     *
     * @param types
     */
    public static void doAllTask(List<String> types) {
        if (types.contains("NUMBERSTRATEGY")) {
            ThreadPoolManager threadPool = ThreadPoolManager.getInstance();
            threadPool.execute(() -> {
                try {
                    storeNoStrategy(false);
                } catch (Exception e) {
                    log.error("写入流水号策略失败。", e);
                }
            });
            threadPool.execute(() -> {
                try {
                    storeDataCenterId();
                } catch (Exception e) {
                    log.error("写入数据中心参数失败。", e);
                }
            });
        }
    }

    /**
     * 存储流水号策略数据
     *
     * @param needCheck
     */
    public static void storeNoStrategy(boolean needCheck) {
        List<NumberStrategy> list = feignInvoke.getNoStList(needCheck);
        //重试次数
        int i = 0;
        while (CollectionUtils.isEmpty(list) && i < 3) {
            i++;
            list = feignInvoke.getNoStList(needCheck);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("storeNoStrategy 重试次数：" + i);
        }

        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, NumberStrategy> map = list.stream().collect(Collectors.toMap(NumberStrategy::getStNo, Function.identity(), (x, y) -> {
                return y;
            }));
            List<String> allNames = list.stream().map(NumberStrategy::getStNo).collect(Collectors.toList());
            cache.put(CmcConstants.NO_STRATEGY_IN_REDIS, map);
            cache.put(CmcConstants.NO_STRATEGY_LIST, allNames);
            log.info("流水号更新成功...");
            log.info("cache --> no_strategy_in_redis :{}", JsonUtil.convertObjectToJson(map));
            log.info("cache --> NO_STRATEGY_LIST :{}", JsonUtil.convertObjectToJson(allNames));
        }
    }

    public static void storeRedisKeys(boolean needCheck) {
        List<RedisManagerObj> redisKeys = feignInvoke.getRedisMgt(appName, needCheck);
        if (redisKeys != null && redisKeys.size() > 0) {
            cache.del(REDIS_KEYS);
            cache.put(REDIS_KEYS, redisKeys);
        }
    }

    /**
     * 存储中心id
     */
    public static void storeDataCenterId() {
        String dataCenterId = feignInvoke.getDataCenterId();
        log.info("appName===={}", appName);
        //重试次数
        int i = 0;
        while (StringUtils.isBlank(dataCenterId) && i < 3) {
            i++;
            dataCenterId = feignInvoke.getDataCenterId();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("storeDataCenterId 重试次数：" + i);
        }
        cache.put(CmcConstants.DATA_CENTER_ID, dataCenterId);
        log.info("数据中心参数更新成功: {}", dataCenterId);
    }

    public static String getAppName() {
        return appName;
    }

    public static String getDataCenterId() {
        String dataCenterId = cache.getString(CmcConstants.DATA_CENTER_ID);
        return dataCenterId;
    }
}
