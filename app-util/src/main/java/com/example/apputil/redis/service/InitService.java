package com.example.apputil.redis.service;

import com.alibaba.fastjson.JSON;
import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.response.ResponseContant;
import com.example.appstaticutil.response.ResponseResult;
import com.example.apputil.redis.api.IRedisService;
import com.example.apputil.redis.model.NumberStrategy;
import com.example.apputil.redis.model.SnoSt;
import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.redis.remote.SignerFeign;
import com.example.apputil.redis.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Thread.sleep;

@Order
@Service
@Slf4j
public class InitService implements CommandLineRunner {

    @Autowired
    CaffeineCache cache;

//    @Autowired
//    SnoStMapper dao;

    @Autowired
    SignerFeign signerFeign;

    @Autowired
    IRedisService redisService;

    @Resource
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

//    ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Value("{task.reload.flag:false}")
    String flag;


    public void init() {
//        log.info("reflash配置....");
//        String str = signerFeign.getSnoStList();
//        log.info("all SnoSt....{}", str);
//        ResponseResult<List<SnoSt>> responseResult = JsonUtil.convertJsonToObject(str, new TypeReference<ResponseResult<List<SnoSt>>>() {
//        });
//        if (ResponseContant.SUCCESS.equals(responseResult.getCode()) && CollectionUtils.isNotEmpty(responseResult.getData())) {
//            responseResult.getData().forEach(v -> {
//                NumberStrategy numberStrategy = new NumberStrategy();
//                BeanUtils.copyProperties(v, numberStrategy);
//                numberStrategy.setStep(v.getSnoStStep());
//                cache.hset(Constants.NO_STRATEGY_IN_REDIS, v.getStNo(), numberStrategy);
//            });
//        }
//        cache.put(Constants.DATA_CENTER_ID, "32");

        InitListener.doListenerInitialize(Arrays.asList("NUMBERSTRATEGY"));

    }

//    public String batchGenerateId(Map map) {
//        String snIdKey = findKeyAndInitValue(map);
//        Object currentId = redisService.get(snIdKey);
//        long redis_id = redisService.incr(snIdKey, 100L);
//        map.put("currentId", String.valueOf(currentId));
//        map.put("maxId", String.valueOf(redis_id));
//        return JSON.toJSONString(map);
//    }

    public String batchGenerateId(Map map) {
        String genType = (String) Optional.ofNullable(map.get("genType")).orElse(1);
        String stNo = (String) Optional.ofNullable(map.get("stNo")).orElse(null);
        Integer step = (Integer) Optional.ofNullable(map.get("step")).orElse(null);

        Object initValue = map.get("initValue");
        long realinitValue = 0L;
        if (initValue != null) {
            if (initValue instanceof Integer) {
                realinitValue = ((Integer) initValue).longValue();
            }
            if (initValue instanceof Long) {
                realinitValue = (Long) initValue;
            }
        }

        Integer noLength = (Integer) Optional.ofNullable(map.get("noLength")).orElse(0);
        Integer count = (Integer) Optional.ofNullable(map.get("count")).orElse(0);

        if (count == 0) {
            log.info("未传入次数参数，根据编号长度获取");
            if (noLength <= 5) {
                throw new RuntimeException("流水号长度小于5，调用单次获取接口");
            }
            if (noLength >= 12) {
                count = 100000;
            } else {
                count = (int) Math.pow(10.0D, (noLength >> 1) - 1);
            }
        }

        count = count * step;
        String key = transformType(genType, stNo);
        long maxId = redisService.incr(key, count);
        maxId = maxId + realinitValue;
        if (maxId > Math.pow(10.0D, noLength)) {
            throw new RuntimeException("序列超出最大值，无序列可用");
        } else {
            Map<String, Object> rs = new HashMap<>();
            rs.put("maxId", maxId + "");
            rs.put("currentId", maxId - count + 1L + "");
            rs.put("step", step + "");
            updateDatabase(stNo, maxId);
            return JSON.toJSONString(rs);
        }

    }

    private void updateDatabase(String stNo, long currentId) {
        threadPoolTaskScheduler.execute(() -> {
            Map<String, Object> param = new HashMap<>();
            param.put("stNo", stNo);
            param.put("curValue", currentId);
            param.put("currentTime", new Date());
            try {
//                String sql = "update sno_st set cur_Value=:cur_Value ,cur_Date=:currentTime where st_No=:st_no and cur_Date<:currentTime";
                log.info("更新数据：" + JSON.toJSONString(param));
                signerFeign.synUpdate(param);
                log.info("同步策略当前值成功,策略编号" + stNo + "，当前值：" + currentId);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("同步策略当前值失败！！！,策略编号" + stNo + "，当前值：" + currentId);
            }

        });
    }


//    public Long generateId(Map map) {
//        String snIdKey = findKeyAndInitValue(map);
//        String stno = (String) map.get(snIdKey);
//        // key: USER:ID:210609 --> 100
//        return redisService.incr(stno);
//    }

    public Long generateId(Map map) {
        String genType = (String) Optional.ofNullable(map.get("genType")).orElse(1);
        String stNo = (String) Optional.ofNullable(map.get("stNo")).orElse(null);
        Integer step = (Integer) Optional.ofNullable(map.get("step")).orElse(null);
        Integer noLength = (Integer) Optional.ofNullable(map.get("noLength")).orElse(0);
        Integer initValue = (Integer) Optional.ofNullable(map.get("initValue")).orElse(0);

        if (genType == null || stNo == null || noLength == 0) {
            throw new RuntimeException("策略参数不全,请检查");
        }

        String key = transformType(genType, stNo);
        long id = redisService.incr(key, step);
        if (id > Math.pow(10.0D, noLength)) {
            throw new RuntimeException("序列超出最大值，无序列可用");
        } else {
            updateDatabase(stNo, id);
            return initValue + id;
        }
    }


    public String findKeyAndInitValue(Map map) {
        String stno = (String) map.get("stNo");
        String genType = (String) map.get("genType");
        String key = stno + ":" + transformType(genType);
        if (redisService.get(key) == null) {
            redisService.put(key, map.get("initValue"));
        }
        return key;
    }

    public String transformType(String generateType) {
        switch (generateType) {
            case "0"://每天生成
                return LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            case "1"://每月生成
                return LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
            case "2"://每年生成
                return LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
            default:
                //递增
                return "auto_increment";
        }
    }


    public String transformType(String generateType, String key) {
        switch (generateType) {
            case "0"://每天生成
                return key + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            case "1"://每月生成
                return key + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
            case "2"://每年生成
                return key + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
            default:
                //递增
                return key;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        threadPoolTaskScheduler.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            init();
        });

    }

//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        init();
//        if ("true".equals(flag)) {
//            threadPoolTaskScheduler.schedule(() -> {
//                init();
//            }, new CronTrigger("0 */15 * * * ?"));//每15分钟同步一次
//        }
//    }

}
