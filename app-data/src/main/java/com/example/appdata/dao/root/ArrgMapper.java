package com.example.appdata.dao.root;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArrgMapper {

    @SelectProvider(type = AggrProvider.class, method = "selectColumnTypeOracle")
    List<Map<String, Object>> selectColumnTypeOracle(Map<String, Object> params);

    @SelectProvider(type = AggrProvider.class, method = "selectColumnTypeMySQL")
    List<Map<String, Object>> selectColumnTypeMySQL(Map<String, Object> params);

    @SelectProvider(type = AggrProvider.class, method = "insertMysql")
    void insertMysql(Map<String, Object> map);

    @SelectProvider(type = AggrProvider.class, method = "insertOracle")
    long insertOracle(Map<String, Object> map);

    @SelectProvider(type = AggrProvider.class, method = "selectAll")
    List<Map<String, Object>> selectPublic(Map<String, Object> params);

    @SelectProvider(type = AggrProvider.class, method = "updateMysql")
    void updateMysql(Map<String, Object> params);
}
