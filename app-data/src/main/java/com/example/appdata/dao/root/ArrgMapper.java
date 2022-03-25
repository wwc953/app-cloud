package com.example.appdata.dao.root;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArrgMapper {

    @SelectProvider(type = AggrProvider.class,method = "selectColumnType")
    List<Map<String,Object>> selectColumnType(Map<String, Object> params);
}
