package com.example.appdata.dao;

import com.alibaba.nacos.client.utils.JSONUtils;
import com.example.appdata.dao.root.ArrgMapper;
import com.example.appstaticutil.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.event.WindowFocusListener;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Transactional
public class AggrProviderService {

    @Autowired
    ArrgMapper arrgMapper;

    public List<Map<String, Object>> selectColumnTypeOracle(Map<String, Object> params) {
        List<Map<String, Object>> result = arrgMapper.selectColumnTypeOracle(params);
        return result;
    }

    public List<Map<String, Object>> selectColumnTypeMySQL(Map<String, Object> params) {
        log.info("selectColumnTypeMySQL ====> {}", JsonUtil.convertObjectToJson(params));
        List<Map<String, Object>> result = arrgMapper.selectColumnTypeMySQL(params);
        log.info("selectColumnTypeMySQL <===> {}", JsonUtil.convertObjectToJson(result));
        return result;
    }
}
