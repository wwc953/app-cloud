package com.example.appcommon.dao;

import com.example.appcommon.bean.SnoSt;

import java.util.List;
import java.util.Map;

public interface SnoStCommonMapper {

    int insert(SnoSt record);

    int insertSelective(SnoSt record);

    SnoSt selectByPrimaryKey(Long snoStId);
    
    SnoSt selectByStNo(String stNo);

    int updateByPrimaryKeySelective(SnoSt record);

    int updateByPrimaryKey(SnoSt record);

    int synUpdate(Map record);

    List<SnoSt> selectAll();
}