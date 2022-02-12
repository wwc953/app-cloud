package com.example.apputil.redis.feign;

import com.example.apputil.redis.cache.CaffeineCache;
import com.example.apputil.utils.JsonUtil;
import com.example.apputil.utils.MD5Utils;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class FeignInvoke {
    @Autowired
    private CaffeineCache cache;

    public static final String REDIS_MGT_MD5 = "redisMgtMd5";

    private static final Configuration jsonPathConf;

    static {
        jsonPathConf = Configuration.builder()
                .jsonProvider(new GsonJsonProvider())
                .mappingProvider(new GsonMappingProvider())
                .build()
                .addOptions(new Option[]{Option.DEFAULT_PATH_LEAF_TO_NULL});
    }

    public List<RedisManagerObj> getRedisMgt(String centerName, boolean needCheck) {
        String md5 = cache.getString(REDIS_MGT_MD5);
        String value = "";
        if (!StringUtils.isEmpty(md5) && needCheck) {
            // db 入参 centerName md5
            value = null;
        } else {
            // db 入参 centerName null
            value = null;
        }

        DocumentContext document = JsonPath.using(jsonPathConf).parse(value);
        List<RedisManagerObj> rediskeys = (List) document.read("$.data." + centerName + "[*]", new TypeRef<List<RedisManagerObj>>() {
        });
        if (rediskeys != null) {
            JsonArray read = document.read("$.data." + centerName + "[*]", new Predicate[0]);
            String md5v = MD5Utils.md5(JsonUtil.convertObjectToJson(read));
            cache.put(REDIS_MGT_MD5, md5v);
        }
        return rediskeys;
    }

}