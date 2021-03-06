package com.example.apputil.redis.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.threadpool.ThreadPoolManager;
import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.cmccache.fegin.api.SignerFeign;
import com.example.apputil.constants.CmcConstants;
import com.example.apputil.redis.model.NumberStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class GenerateIdUtil {

    private static final Pattern p = Pattern.compile("\\{(.*?)\\}");

    @Autowired
    CaffeineCache cache;

    @Value("${init.retry.time:300000}")
    int retryTime;

    @Autowired
    SignerFeign feign;

    ThreadPoolManager threadPool = ThreadPoolManager.getInstance();

    @Autowired
    NumberStrategyUtil numberStrategyUtil;

    public String getID(String stNo, Map<String, String> param) {
        NumberStrategy st = numberStrategyUtil.getStrategyByStNo(stNo);
        String stContent = st.getStContent();
        List<String> contentArr = resolveContent(stContent);
        StringBuilder sb = new StringBuilder();
        contentArr.forEach(t -> {
            String tmpRs = generateCode(t, st, param == null ? null : param.get(t), null, false, false);
            if (tmpRs == null && log.isInfoEnabled()) {
                log.info("策略编号stNo:{}中,{}项的值解析失败，将被忽略,", stNo, t);
            }
            sb.append(tmpRs == null ? "" : tmpRs);
        });
        return sb.toString();
    }

    public List<String> batchGetId(String stNo, Map<String, String> param, Integer count) {
        List<String> rs = new ArrayList<>();
        NumberStrategy st = numberStrategyUtil.getStrategyByStNo(stNo);
        String stContent = st.getStContent();
        List<String> contentArr = resolveContent(stContent);
        StringBuilder sb = new StringBuilder();
        contentArr.forEach(t -> {
            String tmpRs = generateCode(t, st, param == null ? null : param.get(t), null, false, false);
            if (tmpRs == null && log.isInfoEnabled()) {
                log.info("batchGetId 策略编号stNo:{}中,{}项的值解析失败，将被忽略,", stNo, t);
            }
            sb.append(tmpRs == null ? "" : tmpRs);
        });

        List<Long> ids = batchGenerateId(st, count, false);
        String rsId = sb.toString();
        Long noLength = st.getNoLength();
        if (contentArr.contains("NO")) {
            ids.forEach(id -> {
                rs.add(MessageFormat.format(rsId, processDigit(noLength.intValue(), String.valueOf(id))));
            });
        } else if (contentArr.contains("no")) {
            ids.forEach(id -> {
                rs.add(MessageFormat.format(rsId, String.valueOf(id)));
            });
        }
        return rs;
    }

    public List<Long> batchGenerateId(NumberStrategy strategy, int count, boolean isHex) {
        Map map = JsonUtil.convertJsonToMap(JsonUtil.convertObjectToJson(strategy));
        if (count > 0) {
            map.put("count", count);
        }
        if (isHex) {
            map.put("hex", "1");
        }
        String res = Optional.of(feign.batchGenerateId(map)).get();
        Map rs = JsonUtil.convertJsonToMap(res);
        return convertMapToQueue(rs);
    }

    private String generateCode(String format, NumberStrategy st, String valueFromParam, Integer count, boolean isBatch, boolean isHex) {
        switch (format) {
            case "d":
            case "dd":
            case "M":
            case "MM":
            case "yy":
            case "yyyy":
            case "hh":
            case "HH":
            case "mm":
            case "ss":
                return nowTime(format);
            case "dataCenterId":
                return getDataCenterId();
            case "no":
                return generateId(st, false, isBatch, isHex);
            case "NO":
                return generateId(st, true, isBatch, isHex);
            default:
                return valueFromParam;
        }
    }

    public String generateId(NumberStrategy st, boolean fullWithZero, boolean isBatch, boolean isHex) {
        if (isBatch) {
            return "{0}";
        } else {
            long id = this.generateId(st, false);
            String idStr = String.valueOf(id);
            if (isHex) {
                idStr = Long.toHexString(id);
            }
            return fullWithZero ? processDigit(st.getNoLength().intValue(), idStr) : idStr;
        }
    }

    /**
     * 补零
     *
     * @param digit
     * @param data
     * @return
     */
    private String processDigit(int digit, String data) {
        int len = data.length();
        while (len < digit) {
            StringBuilder sb = new StringBuilder();
            sb.append("0").append(data);
            data = sb.toString();
            ++len;
        }
        return data;
    }

    private String nowTime(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date());
    }

    private List<String> resolveContent(String stContent) {
        List<String> rs = new ArrayList<>();
        Matcher matcher = p.matcher(stContent);
        while (matcher.find()) {
            rs.add(matcher.group(1));
        }
        return rs;
    }

    public String getDataCenterId() {
        return cache.getString(CmcConstants.DATA_CENTER_ID);
    }

    public Long generateId(NumberStrategy st, boolean ishex) {
        String generateType = st.getGenType();
        Long noLength = st.getNoLength();
        String stNo = st.getStNo();
        String typeString = null;
        if (noLength < 5L) {
            Map map = JSON.parseObject(JSONObject.toJSONString(st), Map.class);
            if (ishex) map.put("hex", "1");
            Long id = Optional.of(feign.generateId(map)).get();
            return id;
        } else {
            typeString = transformType(generateType);
            String key = "NUMBER_ID_" + stNo;
            synchronized (st) {
                Object check = cache.hget(key, typeString);
                if (check == null) cache.del(key);

                Boolean initial = (Boolean) Optional.ofNullable(cache.hgetObj(key, typeString, CmcConstants.INITIAL_FIELD)).orElse(false);
                LinkedList<Long> queue = cache.hgetObj(key, typeString, CmcConstants.NOW_QUEUE_FIELD) == null ? new LinkedList<>() : (LinkedList) cache.hgetObj(key, typeString, CmcConstants.NOW_QUEUE_FIELD);
                ConcurrentLinkedQueue<Map> pendingQueue = cache.hgetObj(key, typeString, CmcConstants.PENDING_QUEUE_FIELD) == null ? new ConcurrentLinkedQueue<>() : (ConcurrentLinkedQueue) cache.hgetObj(key, typeString, CmcConstants.PENDING_QUEUE_FIELD);
                Long rs = null;
                Long asyncTime;
                if (queue.isEmpty()) {
                    if (pendingQueue.isEmpty()) {

                        if (initial) {
                            asyncTime = (Long) Optional.ofNullable(cache.hgetObj(key, typeString, CmcConstants.ASYNC_TIME_FIELD)).orElse(0L);
                            Map map = JSON.parseObject(JSONObject.toJSONString(st), Map.class);
                            if (ishex) {
                                map.put("hex", "1");
                            }
                            rs = Optional.of(feign.generateId(map)).get();
                            log.info("等待队列为空，已初始化，直接降级为从发号器获取，直到服务恢复,ids={}", rs);
                            long nowTime = System.currentTimeMillis();
                            if (asyncTime != 0L && nowTime - asyncTime > retryTime) {
                                String finalTypeString1 = typeString;
                                threadPool.execute(() -> {
                                    doRetry(key, finalTypeString1, pendingQueue, st, ishex);
                                });
                            }
                            return rs;
                        }

                        log.info("等待队列为空，未初始化，从缓存队列中获取。策略编号：{}", stNo);
                        try {
                            if (pendingQueue.isEmpty()) {
                                pendingQueue.offer(getPendingMapWithFeign(st, ishex));
                                cache.hset(key, typeString, CmcConstants.PENDING_QUEUE_FIELD, pendingQueue);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("从算号器服务获取ID失败", e);
                        }

                        initial = true;
                        cache.hset(key, typeString, CmcConstants.INITIAL_FIELD, initial);

                    }

                    Map dataMap = pendingQueue.poll();
                    log.info("取出第二缓存号段：{}，策略编号：{}", dataMap, stNo);
                    queue = convertMapToQueue(dataMap);
                    cache.hset(key, typeString, CmcConstants.NOW_QUEUE_FIELD, queue);
                    cache.hset(key, typeString, CmcConstants.THRESHOLD_FIELD, calcThresHold(queue));
                }

                rs = queue.poll();
                asyncTime = (Long) cache.hgetObj(key, typeString, CmcConstants.THRESHOLD_FIELD);
                if (rs.equals(asyncTime)) {
                    log.info("触发阈值，异步缓存第二段队列：策略编号：{}，阈值：{}", stNo, asyncTime);
                    String finalTypeString = typeString;
                    threadPool.execute(() -> {
                        try {
                            pendingQueue.offer(getPendingMapWithFeign(st, ishex));
                            log.info("异步缓存结果，pendingQueue：{},策略编号：{}", pendingQueue, stNo);
                        } catch (Exception e) {
                            log.info("异步缓存出错，纪录时间,策略编号：{}", stNo);
                            cache.hset(key, finalTypeString, CmcConstants.ASYNC_TIME_FIELD, System.currentTimeMillis());
                        }
                    });
                }

                asyncTime = (Long) Optional.ofNullable(cache.hgetObj(key, typeString, "asyncTime")).orElse(0L);
                long nowTime = System.currentTimeMillis();
                if (asyncTime != 0L && nowTime - asyncTime > retryTime) {
                    String finalTypeString1 = typeString;
                    threadPool.execute(() -> {
                        doRetry(key, finalTypeString1, pendingQueue, st, ishex);
                    });
                }
                return rs;
            }
        }

    }

    /**
     * 阈值计算
     *
     * @param queue
     * @return
     */
    private Long calcThresHold(LinkedList<Long> queue) {
        int thresHoldIndex = queue.size() / 5 - 1;
        return queue.get(thresHoldIndex);
    }

    /**
     * 取值范围，转化为List
     *
     * @param dataMap
     * @return
     */
    private LinkedList<Long> convertMapToQueue(Map dataMap) {
        Long currentId = Long.valueOf((String) dataMap.get("currentId"));
        Long maxId = Long.valueOf((String) dataMap.get("maxId"));
        Long stepInRs = Long.valueOf(String.valueOf(dataMap.get("step")));
        LinkedList<Long> rsList = new LinkedList<>();

        for (Long i = currentId; i <= maxId; i = i + stepInRs) {
            rsList.offer(i);
        }
        return rsList;
    }

    /**
     * 重试
     *
     * @param key
     * @param typeString
     * @param pendingQueue
     * @param st
     * @param ishex
     */
    private void doRetry(String key, String typeString, ConcurrentLinkedQueue<Map> pendingQueue, NumberStrategy st, boolean ishex) {
        log.info("达到重试时间，重新从服务拉取数据");
        try {
            pendingQueue.offer(getPendingMapWithFeign(st, ishex));
            log.info("异步缓存结果，pendingQueue：{}", pendingQueue);
            cache.hset(key, typeString, CmcConstants.ASYNC_TIME_FIELD, 0);
        } catch (Exception e) {
            log.info("异步缓存出错，更新时间,策略编号：{}", st.getStNo());
            cache.hset(key, typeString, CmcConstants.ASYNC_TIME_FIELD, System.currentTimeMillis());
        }
    }

    /**
     * 获取当前最新值
     *
     * @return
     */
    private Map getPendingMapWithFeign(NumberStrategy st, boolean isHex) {
        Map param = JSON.parseObject(JSONObject.toJSONString(st), Map.class);
        if (isHex) {
            param.put("hex", "1");
        }
        log.info("getPendingMapWithFeign -- st: {}", JSONObject.toJSONString(st));
        String rsdata = Optional.of(feign.batchGenerateId(param)).get();
        Map rsMap = JSON.parseObject(rsdata, Map.class);
        return rsMap;
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
                return "infinite";
        }
    }

}
