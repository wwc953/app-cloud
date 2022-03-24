package com.example.appcommon.commondata.customsql;

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

    public String docustomSqlByPage(Map<String, Object> map) {
        String docustomSql = map.get("finalSQL").toString();
        String pageSql = new SQL() {
            {
                SELECT("t*, rownum as rn");
                FROM("(" + docustomSql + ") t");
                WHERE("rownum <= (#{pageNo})*#{pageSize} ");
            }
        }.toString();
        String resultSql = new SQL() {
            {
                SELECT(" * ");
                FROM("(" + pageSql + ") temp");
                WHERE(" temp.rn > (#{pageNo}-1)*#{pageSize}");
            }
        }.toString();
        return resultSql;
    }

    public String docustomSqlGetTotal(Map<String, Object> map) {
        String docustomSql = map.get("finalSQL").toString();
        String pageSql = new SQL() {
            {
                SELECT("count(0)");
                FROM("(" + docustomSql + ") t");
            }
        }.toString();
        return pageSql;
    }

}
