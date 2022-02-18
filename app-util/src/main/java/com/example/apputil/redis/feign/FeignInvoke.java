package com.example.apputil.redis.feign;

import com.example.appstaticutil.encry.MD5Utils;
import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.response.ResponseResult;
import com.example.apputil.cache.CaffeineCache;
import com.example.apputil.redis.model.SnoSt;
import com.example.apputil.redis.remote.SignerFeign;
import com.fasterxml.jackson.core.type.TypeReference;
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

    @Autowired
    SignerFeign signerFeign;

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
        List<RedisManagerObj> rediskeys = document.read("$.data." + centerName + "[*]", new TypeRef<List<RedisManagerObj>>() {
        });
        if (rediskeys != null) {
            JsonArray read = document.read("$.data." + centerName + "[*]", new Predicate[0]);
            String md5v = MD5Utils.md5(JsonUtil.convertObjectToJson(read));
            cache.put(REDIS_MGT_MD5, md5v);
        }
        return rediskeys;
    }

    public List<SnoSt> getNoStList(boolean needCheck) {
        String snoStList = signerFeign.getSnoStList();
        ResponseResult<List<SnoSt>> responseResult = JsonUtil.convertJsonToObject(snoStList, new TypeReference<ResponseResult<List<SnoSt>>>() {
        });
        return responseResult.getData();
    }

    public List<SnoSt> getNoStListByDoc(boolean needCheck) {
        String snoStList = signerFeign.getSnoStList();
        DocumentContext document = JsonPath.using(jsonPathConf).parse(snoStList);
        List<SnoSt> list = document.read("$.data.strategys[*]", new TypeRef<List<SnoSt>>() {
        });
        return list;
    }

}
