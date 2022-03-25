package com.example.appdata.dao.root;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class AggrProvider {

    /**
     * 获取oracle 表字段类型
     *
     * @param map
     * @return
     */
    public String selectColumnTypeOracle(Map<String, Object> map) {
        Map<String, String> columns = (Map<String, String>) map.get("columns");
        Set<Map.Entry<String, String>> entrySet = columns.entrySet();
        Map<String, String> conditions = (Map<String, String>) map.get("conditions");
        Set<Map.Entry<String, String>> entrySet2 = conditions.entrySet();

        String sql = new SQL() {{
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                SELECT(entry.getValue() + " as \"" + entry.getKey() + "\"");
            }
            FROM(map.get("tableName").toString());

            iterator = entrySet2.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                WHERE(entry.getValue() + " in (${" + entry.getKey() + "})");
            }

        }}.toString();

        return sql;
    }

    /**
     * 获取MySQL 表字段类型
     *
     * @param map
     * @return
     */
    public String selectColumnTypeMySQL(Map<String, Object> map) {
        Map<String, String> columns = (Map<String, String>) map.get("columns");
        Set<Map.Entry<String, String>> entrySet = columns.entrySet();
        Map<String, String> conditions = (Map<String, String>) map.get("conditions");
        Set<Map.Entry<String, String>> entrySet2 = conditions.entrySet();

        String sql = new SQL() {{
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                SELECT(entry.getValue() + " as \"" + entry.getKey() + "\"");
            }
            FROM(map.get("tableName").toString());

            iterator = entrySet2.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                WHERE(entry.getValue() + " in (${" + entry.getKey() + "})");
            }

        }}.toString();

        return sql;
    }
}
