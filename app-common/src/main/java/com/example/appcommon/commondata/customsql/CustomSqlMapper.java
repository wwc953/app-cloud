package com.example.appcommon.commondata.customsql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomSqlMapper {

    @SelectProvider(type = CustomSqlProvider.class, method = "docustomSql")
    List<Map<String, Object>> docustomSql(Map<String, Object> paramMap);

    @SelectProvider(type = CustomSqlProvider.class, method = "docustomSqlByPage")
    List<Map<String, Object>> docustomSqlByPage(Map<String, Object> paramMap);

    @SelectProvider(type = CustomSqlProvider.class, method = "docustomSqlGetTotal")
    Integer docustomSqlGetTotal(Map<String, Object> paramMap);

    @SelectProvider(type = CustomSqlProvider.class, method = "docustomSqlMySQL")
    List<Map<String, Object>> docustomSqlMySQL(Map<String, Object> paramMap);

    @SelectProvider(type = CustomSqlProvider.class, method = "docustomSqlByPageMySQL")
    List<Map<String, Object>> docustomSqlByPageMySQL(Map<String, Object> paramMap);

}
