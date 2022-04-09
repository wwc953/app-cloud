package com.example.appdata.dao;

import com.example.appdata.dao.root.ArrgMapper;
import com.example.appdata.model.DataOperation;
import com.example.appdata.model.OperaDetail;
import com.example.appstaticutil.json.JsonUtil;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Component
@Transactional
public class AggrBatchProviderService {

    @Value("${batch.size:300}")
    private Integer batchSize;

    @Autowired
    ArrgMapper arrgMapper;

    public Object excuteBetch(String paramStr, DataOperation operaConfig) {
        String operaType = getOperateType(paramStr);
        List<OperaDetail> operaDetails = operaConfig.getcDataSrvRelaDOList();
        if (operaDetails == null || operaDetails.isEmpty()) {
            throw new RuntimeException("未找到对应的配置详情信息");
        }
        Map<String, List<Map<String, Object>>> sqlParams = new HashMap<>(300);
        Map<String, Object> paramMap = JsonUtil.getMap4JsonObject(paramStr);
        for (int i = 0; i < operaDetails.size(); i++) {
            OperaDetail operaDetail = operaDetails.get(i);
            String getpo = operaDetail.getDataModelObhjName();
            String fldTypeObhjName = operaDetail.getFldTypeObhjName();
            List<Map<String, Object>> poList = null;
            if (paramMap.containsKey(getpo)) {
                poList = (List<Map<String, Object>>) paramMap.get(getpo);
            } else {
                poList = new ArrayList<>(500);
            }
            getParamsList(poList, fldTypeObhjName, paramMap);
            sqlParams.put(getpo, poList);
        }

        if (!"insert".equals(operaType) && !"update".equals(operaType)) {
            throw new RuntimeException("不支持的操作类型:" + operaType);
        }
        updateBatch(operaConfig, sqlParams, operaType);
        return null;

    }

    private void updateBatch(DataOperation operaConfig, Map<String, List<Map<String, Object>>> sqlParams, String operaType) {
        List<OperaDetail> operaDetails = operaConfig.getcDataSrvRelaDOList();
        Map<String, Map<String, String>> colTypes = operaConfig.getColTypes();
        for (int i = 0; i < operaDetails.size(); i++) {
            OperaDetail operaDetail = operaDetails.get(i);
            if (operaDetail == null || operaDetail.getDataModelObhjName() == null) {
                throw new RuntimeException("配置信息丢失");
            }
            String getPo = operaDetail.getDataModelObhjName();
            Map<String, Object> params = new HashMap<>(300);
            Map<String, Object> sqlMap = new HashMap<>(300);
            List<Map<String, Object>> list = sqlParams.get(getPo);
            if (list != null) {
                int maxLength = batchSize;
                sqlMap.put("tableName", getPo);
                sqlMap.put("attrMapping", operaDetail.getAttrMapping());
                sqlMap.put("colunmType", colTypes.get(getPo));

                String beatchSql = null;
                if ("insert".equals(operaType)) {
                    beatchSql = getBeachInsertSql(sqlMap, batchSize);
                }
                if ("update".equals(operaType)) {
                    beatchSql = getBeachUpdateSql(sqlMap, batchSize, operaDetail.getDataObjID());
                }

                int size = list.size();
                for (int j = 0; j < size; j += batchSize) {
                    if (j + batchSize > size) {
                        maxLength = size - j;
                        if ("update".equals(operaType)) {
                            beatchSql = getBeachUpdateSql(sqlMap, maxLength, operaDetail.getDataObjID());
                        } else {
                            beatchSql = getBeachInsertSql(sqlMap, maxLength);
                        }
                    }
                    List<Map<String, Object>> subList = list.subList(j, j + maxLength);
                    params.put("SQL", beatchSql);
                    params.put("list", subList);
                    arrgMapper.insertBatch(params);
                }

            }
        }
    }

    private String getBeachUpdateSql(Map<String, Object> sqlMap, Integer size, String dataObjID) {
        try {
            String tableName = sqlMap.get("tableName").toString();
            String attrMapping = sqlMap.get("attrMapping").toString();
            Map<String, Object> colunmType = (Map<String, Object>) sqlMap.get("colunmType");
            StringBuffer sb = new StringBuffer();
            StringBuffer sb1 = new StringBuffer();
            sb.append("UPDATE ").append(tableName).append(" SET ");
            JSONObject jsonObject = JSONObject.fromObject(attrMapping);
            Iterator iterator = jsonObject.keys();
            String fk = null;
            String sql = null;
            String replaceAll = null;

            while (iterator.hasNext()) {
                String next = iterator.next().toString();
                sql = jsonObject.get(next).toString();
                if (sql.equalsIgnoreCase(dataObjID)) {
                    fk = next;
                } else {
                    replaceAll = sql.toUpperCase();
                    Object object2 = colunmType.get(replaceAll);
                    String object = object2.toString();
                    sb.append(sql).append(" = ");
                    if ("DATE".equals(object)) {
                        sb.append(" to_date(#'{'list[{0}].").append(next).append("},'yyyy-mm-dd hh24:mi:ss'),");
                    } else if (object != null && object.startsWith("")) {
                        sb.append(" to_timestamp(#'{'list[{0}].").append(next).append("},'yyyy-mm-dd hh24:mi:ss'),");
                    } else {
                        sb.append(" #'{'list[{0}].").append(next).append("},");
                    }
                }
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append(" WHERE ").append(dataObjID).append(" = ").append(" #'{'list[{0}].").append(fk).append("};");
            MessageFormat msf = new MessageFormat(sb.toString());
            sb1.append(" BEGIN ");

            for (int i = 0; i < size; i++) {
                sb1.append(msf.format(i));
            }

            sb1.append(" END ");
            sql = sb1.toString();
            replaceAll = sql.replaceAll("yyyy-mm-dd hh24:mi:ss", "'yyyy-mm-dd hh24:mi:ss'");
            return replaceAll;
        } catch (Exception e) {
            throw new RuntimeException("sql生成异常", e);
        }
    }

    private String getBeachInsertSql(Map<String, Object> map, Integer size) {
        log.info("getBeachInsertSql map:{},size:{}", JsonUtil.convertMapToJson(map), size);
        try {
            String tableName = map.get("tableName").toString();
            String attrMapping = map.get("attrMapping").toString();
            Map<String, Object> colunmType = (Map<String, Object>) map.get("colunmType");
            StringBuffer sb = new StringBuffer();
            StringBuffer sb2 = new StringBuffer();

            sb.append("INSERT INTO ").append(tableName).append(" ( ");
            JSONObject jsonObject = JSONObject.fromObject(attrMapping);
            sb2.append(" SELECT ");
            Iterator iterator = jsonObject.keys();
            String sql = null;
            String replaceAll = null;
            while (iterator.hasNext()) {
                String next = iterator.next().toString();
                sql = jsonObject.get(next).toString();
//                replaceAll = sql.toUpperCase();
                replaceAll = sql.toLowerCase();
                log.error("replaceAll==={}", replaceAll);
                Object object2 = colunmType.get(replaceAll);
                String object = object2.toString();
                sb.append(sql).append(",");

                if ("DATE".equals(object)) {
                    sb2.append(" to_date(#'{'list[{0}].").append(next).append("},'yyyy-mm-dd hh24:mi:ss'),");
                } else if (object != null && object.startsWith("")) {
                    sb2.append(" to_timestamp(#'{'list[{0}].").append(next).append("},'yyyy-mm-dd hh24:mi:ss'),");
                } else {
                    sb2.append(" #'{'list[{0}].").append(next).append("},");
                }
            }

            sb.deleteCharAt(sb.length() - 1).append(")");
            sb2.deleteCharAt(sb2.length() - 1).append(" from dual");

            MessageFormat msf = new MessageFormat(sb2.toString());
            for (int i = 0; i < size; i++) {
                sb.append(msf.format(i));
                if (i < size - 1) {
                    sb.append(" union all ");
                }
            }
            sql = sb.toString();
            replaceAll = sql.replaceAll("yyyy-mm-dd hh24:mi:ss", "'yyyy-mm-dd hh24:mi:ss'");
            return replaceAll;
        } catch (Exception e) {
            throw new RuntimeException("sql生成异常", e);
        }
    }

    private void getParamsList(List<Map<String, Object>> poList, String fldTypeObhjName, Map<String, Object> paramMap) {
        int index = fldTypeObhjName.indexOf(".");
        if (index > -1) {
            String parentDo = fldTypeObhjName.substring(0, index);
            String subDo = fldTypeObhjName.substring(index + 1, fldTypeObhjName.length());
            Object object = paramMap.get(parentDo);
            if (object instanceof Map) {
                Map<String, Object> param = (Map<String, Object>) object;
                getParamsList(poList, subDo, param);
            } else {
                if (!(object instanceof List)) {
                    return;
                }

                List<Map<String, Object>> paramList = (List<Map<String, Object>>) object;
                Iterator<Map<String, Object>> iterator = paramList.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> map = iterator.next();
                    getParamsList(poList, subDo, map);
                }
            }
        } else {
            Object object = paramMap.get(fldTypeObhjName);
            if (object instanceof Map) {
                Map<String, Object> param = (Map<String, Object>) object;
                poList.add(param);
            } else if (object instanceof List) {
                List<Map<String, Object>> paramList = (List<Map<String, Object>>) object;
                poList.addAll(paramList);
            }

        }
    }

    private String getOperateType(String paramStr) {
        Configuration defaultConfiguration = Configuration.defaultConfiguration();
        List<String> list = JsonPath.using(defaultConfiguration).parse(paramStr).read("$..operateType", new Predicate[0]);
        return list.get(0);
    }


}
