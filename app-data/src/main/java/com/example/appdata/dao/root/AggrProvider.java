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
        log.error("selectColumnTypeOracle==={}", sql);
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
        log.error("selectColumnTypeMySQL==={}", sql);
        return sql;
    }

    public String insertOracle(Map<String, Object> map) {
        Map<String, String> columns = (Map<String, String>) map.get("columns");
        Map<String, String> tabColumns = (Map<String, String>) map.get("tabColumns");
        Set<Map.Entry<String, String>> entrySet = columns.entrySet();
        String sql = new SQL() {{
            INSERT_INTO(map.get("tableName").toString());
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String upVal = key.toUpperCase();
                String columnType = tabColumns.get(key);
                if (entry.getKey().toUpperCase().equals(upVal) && "DATE".equals(columnType)) {
                    VALUES(entry.getKey(), "to_date(#{" + entry.getValue() + "},'yyyy-MM-dd hh24:mi:ss')");
                } else if (entry.getKey().toUpperCase().equals(upVal) && columnType.startsWith("TIMESTAMP")) {
                    VALUES(entry.getKey(), "to_timestamp(#{" + entry.getValue() + "},'yyyy-MM-dd hh24:mi:ss')");
                } else {
                    VALUES(entry.getKey(), "#{" + entry.getValue() + "}");
                }
            }
        }}.toString();
        return sql;
    }

    public String insertMysql(Map<String, Object> map) {
        Map<String, String> columns = (Map<String, String>) map.get("columns");
        Map<String, String> tabColumns = (Map<String, String>) map.get("tabColumns");
        Set<Map.Entry<String, String>> entrySet = columns.entrySet();
        String sql = new SQL() {{
            INSERT_INTO(map.get("tableName").toString());
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
//                String upVal = key.toUpperCase();
                String columnType = tabColumns.get(key);
                if (entry.getKey().toUpperCase().equals(key) && "DATE".equals(columnType)) {
                    VALUES(entry.getKey(), "to_date(#{" + entry.getValue() + "},'yyyy-MM-dd hh24:mi:ss')");
                } else if (entry.getKey().toUpperCase().equals(key) && columnType.startsWith("TIMESTAMP")) {
                    VALUES(entry.getKey(), "to_timestamp(#{" + entry.getValue() + "},'yyyy-MM-dd hh24:mi:ss')");
                } else {
                    VALUES(entry.getKey(), "#{" + entry.getValue() + "}");
                }
            }
        }}.toString();
        log.error("insertMysql sql==={}", sql);
        return sql;
    }

    public String updateMysql(Map<String, Object> map) {
        Map<String, String> columns = (Map<String, String>) map.get("columns");
        Set<Map.Entry<String, String>> entrySet = columns.entrySet();
        Map<String, String> conditions = (Map<String, String>) map.get("conditions");
        Set<Map.Entry<String, String>> entrySet2 = conditions.entrySet();
        Map<String, String> tabColumns = (Map<String, String>) map.get("tabColumns");

        String sql = new SQL() {{
            UPDATE(map.get("tableName").toString());
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String columnType = tabColumns.get(key);
                if ("DATE".equals(columnType) && entry.getValue() != null) {
                    SET(entry.getKey() + "to_date(#{" + entry.getValue() + "},'yyyy-MM-dd hh24:mi:ss')");
                } else if (columnType.startsWith("TIMESTAMP") && entry.getValue() != null) {
                    SET(entry.getKey() + "to_timestamp(#{" + entry.getValue() + "},'yyyy-MM-dd hh24:mi:ss')");
                } else {
                    SET(entry.getKey() + "=#{" + entry.getValue() + "}");
                }
            }

            Iterator<Map.Entry<String, String>> entryIterator = entrySet2.iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, String> entry = entryIterator.next();
                WHERE(entry.getKey() + "=#{" + entry.getValue() + "}");
            }

        }}.toString();
        log.error("updateMysql sql==={}", sql);
        return sql;
    }

    public String deleteMysql(Map<String, Object> map) {
        Map<String, String> conditions = (Map<String, String>) map.get("conditions");
        Set<Map.Entry<String, String>> entrySet = conditions.entrySet();
        String sql = new SQL() {{
            DELETE_FROM(map.get("tableName").toString());
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                WHERE(entry.getKey() + " in (" + entry.getValue() + ")");
            }
        }}.toString();
        log.error("deleteMysql sql==={}", sql);
        return sql;
    }

    public String selectAll(Map<String, Object> map) {
        Map<String, String> columns = (Map<String, String>) map.get("columns");
        Set<Map.Entry<String, String>> entrySet = columns.entrySet();
        Map<String, String> conditions = (Map<String, String>) map.get("conditions");
        Set<Map.Entry<String, String>> entrySet2 = conditions.entrySet();
        Map<String, String> tabColumns = (Map<String, String>) map.get("tabColumns");
        String sql = new SQL() {{
            Iterator<Map.Entry<String, String>> it = entrySet.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String value = entry.getValue();
                String columnType = tabColumns.get(value);
                if (columnType != null && columnType.startsWith("TIMESTAMP")) {
                    SELECT("cast (" + entry.getValue() + " as date)" + " as \"" + entry.getKey() + "\"");
                } else {
                    SELECT(entry.getValue() + " as \"" + entry.getKey() + "\"");
                }
            }

            FROM(map.get("tableName").toString());
            Iterator<Map.Entry<String, String>> iterator = entrySet2.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                WHERE(entry.getValue() + "=#{" + entry.getKey() + "}");
            }

        }}.toString();
        log.error("selectAll sql==={}", sql);
        return sql;
    }

    public String doBatchResult(Map<String, Object> map) {
        return map.get("SQL").toString();
    }
}
