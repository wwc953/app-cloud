package com.example.appdata.dao.dataopera;

import com.example.appdata.model.DataOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataOperationMapper {

    DataOperation getDataOperationByUri(String uri);

    List<DataOperation> getDataOperationByUris(@Param("uriList") List<String> urilist);
}
