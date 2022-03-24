package com.example.appcommon.groovy.customsql;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomSqlProvider {

    public String docustomSql(Map<String, Object> map) {
        String docustomSql = map.get("finalSQL").toString();
        String pageSql = new SQL() {
            {
                SELECT("t*, rownum as rn");
                FROM("(" + docustomSql + ") t");
                WHERE("rownum <=2000 ");
            }
        }.toString();
        return pageSql;
    }


}
