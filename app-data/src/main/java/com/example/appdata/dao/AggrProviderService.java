package com.example.appdata.dao;

import com.example.appdata.dao.root.ArrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Transactional
public class AggrProviderService {

    @Autowired
    ArrgMapper arrgMapper;

    public List<Map<String, Object>> selectColumnType(Map<String, Object> params) {
        List<Map<String, Object>> result = arrgMapper.selectColumnType(params);
        return result;
    }
}
