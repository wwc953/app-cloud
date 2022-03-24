package com.example.appcommon.groovy.customsql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomSqlMapper {

    @SelectProvider(type = CustomSqlProvider.class, method = "docustomSql")
    List<Map<String, Object>> docustomSql(Map<String, Object> paramMap);

}
