package com.example.appcommon.controller;


import com.example.appcommon.commondata.CustomSqlProviderServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/member/commondata")
public class CommonDataController {

    @Resource
    CustomSqlProviderServer customSqlProviderServer;

    @PostMapping("/test")
    public List<Map<String, Object>> test() {
        Map<String, Object> param = new HashMap<>();
        param.put("stNo", "USER");
        String beforesql = "select t.* from sno_st t where t.st_Name is nou null <if test=\"stNo!=null && stNo!=''\"> and t.st_No like #{stNo}||'%' </if> <if test=\"genType!=null && genType!=''\"> and t.gen_Type=#{genType} </if>";
        String apiParamJson = "{\"stNo\":\"\",\"genType\":\"\"}";

        customSqlProviderServer.getLabelSql(param, beforesql, apiParamJson);

        return customSqlProviderServer.docustomSql(param);
    }

}
