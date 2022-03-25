package com.example.appdata.controller;

import com.example.appdata.dao.CustomSqlProviderServer;
import com.example.appstaticutil.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/member/commondata")
public class CommonDataController {

    @Resource
    CustomSqlProviderServer customSqlProviderServer;

    @PostMapping("/test")
    public List<Map<String, Object>> test(@RequestBody Map<String, Object> param) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("stNo", "USER");
        String beforesql = "select t.* from sno_st t where t.st_Name is not null <if test=\"stNo!=null && stNo!=''\"> and t.st_No like concat(#{stNo},'%') </if> <if test=\"genType!=null && genType!=''\"> and t.gen_Type=#{genType} </if>";
        String apiParamJson = "{\"stNo\":\"\",\"genType\":\"\"}";

        customSqlProviderServer.getLabelSql(param, beforesql, apiParamJson);
        List<Map<String, Object>> result = null;
        if (param.containsKey("pageSize")) {
            result = customSqlProviderServer.docustomSqlByPageMySQL(param);
            Integer integer = customSqlProviderServer.docustomSqlGetTotal(param);
            log.info("总数: {}", integer);
        } else {
            result = customSqlProviderServer.docustomSqlMySQL(param);
        }
        log.info("controller res ==> {}", JsonUtil.convertObjectToJson(result));
        return result;
    }

}
