package com.example.apputil.redis.impl;

import com.example.apputil.redis.api.IRedisService;
import com.example.apputil.redis.service.LocalCacheID;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: wangwc
 * @date: 2020/11/27 16:01
 */
@Component
public class SpringRedisServiceImpl implements IRedisService, InitializingBean {
    private static Map<String, String> configMap = null;
    @Value("${dataCenter.url:http://localhost:8088}")
    String dataCenterUrl;
    @Autowired
    @Qualifier("cacheRedisTemplate")
    private RedisTemplate template;

    @Autowired
    LocalCacheID localCacheID;

    private ObjectMapper om = new ObjectMapper();

    @PostConstruct
    public void init() {
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public SpringRedisServiceImpl() {
    }

    @Override
    public void put(String key, Object value) {
        this.template.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, Object value, int TTL) {
        this.template.opsForValue().set(key, value, (long) TTL, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return this.template.opsForValue().get(key);
    }

    @Override
    public Object remove(String key) {
        Object obj = this.template.opsForValue().get(key);
        this.template.delete(key);
        return obj;
    }

    @Override
    public boolean containsKey(String key) {
        return this.template.hasKey(key);
    }

    @Override
    public void hput(String key, String field, Object value) {
        this.template.opsForHash().put(key, field, value);
    }

    @Override
    public void hmput(String key, Map value) {
        this.template.opsForHash().putAll(key, value);
    }

    @Override
    public List<Object> hmget(String key, Object[] fields) {
        return this.template.opsForHash().multiGet(key, Arrays.asList(fields));
    }

    @Override
    public long expire(String key, int TTL) {
        this.template.expire(key, (long) TTL, TimeUnit.SECONDS);
        return this.template.getExpire(key);
    }

    @Override
    public Object hget(String key, String field) {
        return this.template.boundHashOps(key).get(field);
    }

    @Override
    public Map hgetAll(String key) {
        return this.template.boundHashOps(key).entries();
    }

    @Override
    public long hdel(String key, String... fields) {
        return this.template.boundHashOps(key).delete(new Object[]{fields});
    }

    @Override
    public boolean hdelall(String key) {
        this.template.delete(key);
        return this.template.hasKey(key);
    }

    @Override
    public Boolean hexists(String key, String field) {
        return this.template.boundHashOps(key).hasKey(field);
    }

    @Override
    public long lpush(String key, Object obj) {
        return this.template.boundListOps(key).leftPush(obj);
    }

    @Override
    public Object rpop(String key) {
        return this.template.boundListOps(key).rightPop();
    }

    @Override
    public List getList(String key) {
        return this.template.opsForList().range(key, 0L, this.template.opsForList().size(key));
    }

    @Override
    public long sadd(String key, Object... members) {
        return this.template.opsForSet().add(key, new Object[]{members});
    }

    @Override
    public long srem(String key, Object... members) {
        return this.template.opsForSet().remove(key, new Object[]{members});
    }

    @Override
    public boolean smove(String srckey, String dstkey, Object member) {
        return this.template.opsForSet().move(srckey, member, dstkey);
    }

    @Override
    public long incr(String key) {
        return this.template.opsForValue().increment(key, 1L);
    }

    @Override
    public long incr(String key, long addBy) {
        return this.template.opsForValue().increment(key, addBy);
    }

    @Override
    public long hincr(String key, String field, long addBy) {
        return this.template.opsForHash().increment(key, field, addBy);
    }

    @Override
    public long batchHPut(Map<String, Map> objmaps) {
        objmaps.forEach((key, value) -> {
            this.template.opsForHash().putAll(key, value);
        });
        return (long) objmaps.size();
    }

    @Override
    public long batchDel(List<String> bigkeyList) {
        this.template.delete(bigkeyList);
        return (long) bigkeyList.size();
    }

    @Override
    public List<Map> batchGet(List<String> keyObjectList) {
        List<Map> result = new ArrayList();
        keyObjectList.forEach((key) -> {
            Map value = this.template.boundHashOps(key).entries();
            result.add(value);
        });
        return result;
    }

    @Override
    public Boolean zadd(String key, double score, Object member) {
        return this.template.opsForZSet().add(key, member, score);
    }

    @Override
    public Long zadd(String key, Map<Object, Double> members) {
        Set<ZSetOperations.TypedTuple<Object>> param = new HashSet();
        members.forEach((eKey, eValue) -> {
            DefaultTypedTuple<Object> tuple = new DefaultTypedTuple(eKey, eValue);
            param.add(tuple);
        });
        return this.template.opsForZSet().add(key, param);
    }

    @Override
    public Long zcard(String key) {
        return this.template.opsForZSet().zCard(key);
    }

    @Override
    public Long zcount(String key, Double min, Double max) {
        return this.template.opsForZSet().count(key, min, max);
    }

    @Override
    public Set<Object> zrange(String key, long start, long end) {
        return this.template.opsForZSet().range(key, start, end);
    }

    @Override
    public Set<Object> zrangeByScore(String key, double start, double end) {
        return this.template.opsForZSet().rangeByScore(key, start, end);
    }

    @Override
    public Set<Object> zrangeByScore(String key, double min, double max, int offset, int count) {
        return this.template.opsForZSet().rangeByScore(key, min, max, (long) offset, (long) count);
    }

    @Override
    public Long zrem(String key, Object[] members) {
        return this.template.opsForZSet().remove(key, members);
    }

    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        return this.template.opsForZSet().removeRange(key, start, end);
    }

    @Override
    public Long zremrangeByScore(String key, Double start, Double end) {
        return this.template.opsForZSet().removeRangeByScore(key, start, end);
    }

    @Override
    public Long getExpire(String key) {
        return this.template.getExpire(key);
    }

    @Override
    public Set<String> getKeysByPattern(String pattern) {
        return this.template.keys(pattern);
    }

    @Override
    public String getID() {
        return this.getIDInternal("incr_only_id_default");
    }

    @Override
    public String getID(String stNo, Map<String, String> param) {
        return localCacheID.getID(stNo, param);
    }

    private String getIDInternal(String incrId) {
        Date date = new Date();
        String yyMMddHH = (new SimpleDateFormat("yyMMddHHmmss")).format(date);
        Integer HH = Integer.parseInt(yyMMddHH.substring(6, 8));
        Integer mm = Integer.parseInt(yyMMddHH.substring(8, 10));
        Integer ss = Integer.parseInt(yyMMddHH.substring(10, 12));
        if (mm % 30 == 0 && ss % 10 == 0) {
            Integer lastHH = HH == 0 ? 23 : HH - 1;
            String redisKey = incrId + lastHH;
            if (this.get(redisKey) != null) {
                synchronized (this) {
                    if (this.get(redisKey) != null) {
                        this.remove(redisKey);
                    }
                }
            }
        }

        long incRs = this.incr(incrId + HH);
        DecimalFormat df = new DecimalFormat("000000000");
        String incStr = df.format(incRs);
        return yyMMddHH + incStr + (configMap == null ? "01" : (String) configMap.get("dataCenterCode"));
    }

    @Override
    public long decr(String key) {
        return this.template.opsForValue().decrement(key, 1L);
    }

    @Override
    public void removeByKeyLike(String keyPattern) {
        Set<String> keys = this.getKeys(keyPattern);
        Iterator var3 = keys.iterator();

        while (var3.hasNext()) {
            String key = (String) var3.next();
            this.remove(key);
        }

    }

    @Override
    public Set<String> getKeys(String pattern) {
        Set<String> result = new HashSet();
        Cursor cursor = this.scan(pattern, 2147483647);
        while (cursor.hasNext()) {
            result.add(cursor.next().toString());
        }
        try {
            cursor.close();
        } catch (IOException var5) {
        }
        return result;
    }

    @Override
    public Set<String> sacn(String pattern) {
        return null;
    }

    @Override
    public Object lpop(String key) {
        return this.template.boundListOps(key).leftPop();
    }

    @Override
    public List<Map<String, Object>> getListByKeyLike(String pattern) {
        Set<String> keys = this.getKeys(pattern);
        List<Map<String, Object>> mapList = new ArrayList();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            mapList.add(this.hgetAll(key));
        }

        return mapList;
    }

    @Override
    public Map<String, Object> getMapByKeyLike(String pattern) {
        Set<String> keys = this.getKeys(pattern);
        Map<String, Object> mapList = new HashMap();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            mapList.put(key, this.get(key));
        }

        return mapList;
    }

    private Cursor<String> scan(String match, int count) {
        ScanOptions options = ScanOptions.scanOptions().count(count).match(match).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) template.getKeySerializer();
        Cursor cursor = (Cursor) template.executeWithStickyConnection(redisConnection ->
                new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
        return cursor;
    }

    @Override
    public String getID(String type) {
        return this.getIDInternal("incr_only_id_" + type);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        if (configMap == null) {
//            String config = HttpClient.requestOtherProject(this.dataCenterUrl);
//            if (!StringUtils.isEmpty(config) && !config.contains("404 Not Found")) {
//                configMap = (Map) JsonUtil.gson.fromJson(config, HashMap.class);
//            }
//        }
    }

    private byte[] rawKey(String key) {
        try {
            return key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] rawValue(Object value) {
        try {
            return value instanceof byte[] ? (byte[]) value : om.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败！！！");
        }
    }

    @Override
    public void batchPutWithPipe(Map<String, Object> keyValues) {
        template.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                keyValues.forEach((k, v) -> {
                    connection.set(rawKey(k), rawValue(v));
                });
                return null;
            }
        });
    }

    @Override
    public void batchSAddWithPipe(Map<String, List<Object>> keyValues) {

    }

    @Override
    public void batchHMSetWithPipe(Map<String, Map> keyValues) {

    }

    public void batchAddWithPipe(Map<String, List<Object>> keyValues) {
        template.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                keyValues.forEach((k, v) -> {
                    List<byte[]> values = v.stream().map(t -> rawValue(t)).collect(Collectors.toList());
                    connection.sAdd(rawKey(k), values.toArray(new byte[v.size()][]));
                });
                return null;
            }
        });
    }

    @Override
    public List<Object> batchGetWithPipe(List<String> keys) {
        List result = template.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                keys.forEach(k -> {
                    connection.hGetAll(rawKey(k));
                });
                return null;
            }
        });
        return result;
    }


}
