//package com.example.apputil.redis.dao;
//
//
//import com.example.apputil.redis.bean.SnoSt;
//import org.apache.ibatis.annotations.Mapper;
//
//import java.util.List;
//import java.util.Map;
//
//@Mapper
//public interface SnoStMapper {
//
//    int insert(SnoSt record);
//
//    int insertSelective(SnoSt record);
//
//    SnoSt selectByPrimaryKey(Long snoStId);
//
//    SnoSt selectByStNo(String stNo);
//
//    int updateByPrimaryKeySelective(SnoSt record);
//
//    int updateByPrimaryKey(SnoSt record);
//
//    int synUpdate(Map record);
//
//    List<SnoSt> selectAll();
//}