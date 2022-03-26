package com.example.appdata.dao;

import com.example.appdata.dao.root.ArrgMapper;
import com.example.appdata.infrastructure.DataModel;
import com.example.appdata.model.DataOperation;
import com.example.appdata.model.OperaDetail;
import com.example.appstaticutil.entity.EntityUtils;
import com.example.appstaticutil.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@Transactional
public class AggrProviderService {

    @Autowired
    ArrgMapper arrgMapper;

    public List<Map<String, Object>> selectColumnTypeOracle(Map<String, Object> params) {
        log.info("selectColumnTypeOracle ===> {}", JsonUtil.convertObjectToJson(params));
        List<Map<String, Object>> result = arrgMapper.selectColumnTypeOracle(params);
        log.info("selectColumnTypeOracle <=== {}", JsonUtil.convertObjectToJson(result));
        return result;
    }

    public List<Map<String, Object>> selectColumnTypeMySQL(Map<String, Object> params) {
        log.info("selectColumnTypeMySQL ===> {}", JsonUtil.convertObjectToJson(params));
        List<Map<String, Object>> result = arrgMapper.selectColumnTypeMySQL(params);
        log.info("selectColumnTypeMySQL <=== {}", JsonUtil.convertObjectToJson(result));
        return result;
    }

    public Object executePost(String paramStr, DataOperation operaConfig, boolean flag, String appName) {
        DataModel dataModel = new DataModel(paramStr, operaConfig, flag, appName);
        JSONObject inParams = dataModel.getInParams();
        Iterator<String> keys = inParams.keys();
        boolean singleFlag = (inParams.size() <= 1);
        Map resultMap = new HashMap();

        while (keys.hasNext()) {
            String next = keys.next();
            Object object = inParams.get(next);
            if (object instanceof JSONObject) {
                Object analysisJson = analysisJson(next, object, dataModel);
                if (singleFlag) {
                    return analysisJson;
                }
                resultMap.put(next, analysisJson);
            } else if (object instanceof JSONArray) {
                JSONArray obAry = (JSONArray) object;
                List<Object> result = null;
                for (Object obj : obAry) {
                    Object analysisJson = analysisJson(next, obj, dataModel);
                    if (analysisJson != null) {
                        if (result == null) {
                            result = new ArrayList<>();
                        }
                        result.add(analysisJson);
                    }

                }
                if (singleFlag) {
                    return result;
                }
                if (result != null) {
                    resultMap.put(next, result);
                }
            }
        }
        return resultMap;
    }

    private Object analysisJson(String table, Object obj, DataModel dataModel) {
        if (!(obj instanceof JSONObject)) {
            log.error("{}预期为对象，但传入实际为:{}", table, obj.getClass().getName());
            return null;
        }

        JSONObject jsonObject = (JSONObject) obj;
        String operateType = jsonObject.get("operateType") == null ? "" : jsonObject.get("operateType").toString();
        log.info("{}对应操作类型为：{}", table, operateType);
        if (!"no".equals(operateType) && !"".equals(operateType)) {
            Map<String, List<OperaDetail>> doConfigMapping = dataModel.getDoConfigMapping();
            List<OperaDetail> list = doConfigMapping.get(table);
            if (list == null) {
                throw new RuntimeException("未找到" + table + "对应的属性映射配置");
            }
            if (list.size() > 1) {
                Map<String, Object> result = new HashMap<>();
                for (OperaDetail operaDetail : list) {
                    Object analysisResult = analysisJson1(table, jsonObject, operaDetail, operateType, dataModel);
                    if (analysisResult != null) {
                        Map<String, Object> analyMap = (Map<String, Object>) analysisResult;
                        result.putAll(analyMap);
                    }
                }
                return result;
            } else {
                OperaDetail operaDetail = list.get(0);
                return analysisJson1(table, jsonObject, operaDetail, operateType, dataModel);
            }
        } else {
            Iterator it = jsonObject.keys();
            Map<String, JSONObject> curent = dataModel.getCurent();
            curent.put(table, jsonObject);

            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                if (object instanceof JSONObject) {
                    analysisJson(table + "." + key, object, dataModel);
                } else if (object instanceof JSONArray) {
                    JSONArray obAry = (JSONArray) object;
                    for (Object suObj : obAry) {
                        analysisJson(table + "." + key, suObj, dataModel);
                    }
                }
            }
        }
        return null;
    }

    private Object analysisJson1(String table, JSONObject jsonObject, OperaDetail operaDetail, String operateType, DataModel dataModel) {
        if (operaDetail == null || operaDetail.getAttrMapping() == null) {
            throw new RuntimeException("未找到" + table + "对应的属性映射配置");
        }
        String attrMapping = operaDetail.getAttrMapping();
        JSONObject attrMappJson = null;
        try {
            attrMappJson = JSONObject.fromObject(attrMapping);
        } catch (Exception e) {
            throw new RuntimeException("解析" + table + "字段映射出现异常!", e);
        }

        if ("insert".equals(operateType)) {
            analysisInsert(table, jsonObject, attrMappJson, operaDetail, dataModel);
        } else {
            String dataId = operaDetail.getDataObjID();
            Boolean pkflag = false;
            Map<String, Object> params = null;
            Map<String, Object> conditionMap = null;

            Iterator keys = jsonObject.keys();
            String key;
            String po;
            HashMap gtsResult;
            while (keys.hasNext()) {
                key = keys.next().toString();
                if (attrMappJson.containsKey(key)) {
                    po = attrMappJson.getString(key);
                    if (dataId.equalsIgnoreCase(po)) {
                        if ("query".equals(operateType) || "delete".equals(operateType)) {
                            params = new HashMap<>();
                            conditionMap = new HashMap<>();

                            params.put("tableName", operaDetail.getDataModelObhjName());
                            params.put(dataId, jsonObject.get(key));
                            conditionMap.put(attrMappJson.getString(key), "#{" + dataId + "}");
                            gtsResult = new HashMap();
                            gtsResult.put(key, jsonObject.get(key));
                            jsonObject = JSONObject.fromObject(gtsResult);
                        }
                        pkflag = true;
                        break;
                    }
                }
            }
            if (!pkflag) {
                throw new RuntimeException("参数传入有误，未找到" + table + "唯一标识:" + dataId);
            }

            if ("query".equals(operateType)) {
                key = operaDetail.getDataModelObhjName();
                List<Map<String, Object>> analysisQuery = analysisQuery(jsonObject, key, operaDetail, dataModel);
                if (analysisQuery != null && analysisQuery.size() > 0) {
                    Map<String, Object> map = analysisQuery.get(0);
                    EntityUtils.clobToString(map);
                    return map;
                }
            }

            if ("update".equals(operateType)) {
                Map<String, JSONObject> curent = dataModel.getCurent();
                curent.put(table, jsonObject);
                dataModel.setCurent(curent);
                analysisUpdate(table, jsonObject, attrMappJson, operaDetail, dataModel);
            } else {
                if (!"delete".equals(operateType)) {
                    throw new RuntimeException("参数传入有误，未识别的操作类型：" + operateType);
                }
                boolean gtxFlag = dataModel.getGtxFlag();
                if (gtxFlag) {
                    po = operaDetail.getDataModelObhjName();
                    gtsResult = new HashMap();
                    analysisQueryForGtx(jsonObject, po, operaDetail, gtsResult, dataModel);
                    if (gtsResult == null || gtsResult.size() < 1) {
                        throw new RuntimeException("删除的数据不存在");
                    }
                    deleteGTXdata(gtsResult,dataModel);
                } else {
                    analysisDelete(params, operaDetail, conditionMap, dataModel.getPoConfigMapping());
                }

            }

        }
        return null;
    }

    private void deleteGTXdata(HashMap gtsResult, DataModel dataModel) {
    }

    private void analysisQueryForGtx(JSONObject jsonObject, String po, OperaDetail operaDetail, HashMap gtsResult, DataModel dataModel) {
    }

    private void analysisDelete(Map<String, Object> params, OperaDetail operaDetail, Map<String, Object> conditionMap, Map<String, List<OperaDetail>> poConfigMapping) {
    }

    private void analysisUpdate(String table, JSONObject jsonObject, JSONObject attrMappJson, OperaDetail operaDetail, DataModel dataModel) {
    }

    private List<Map<String, Object>> analysisQuery(JSONObject jsonObject, String key, OperaDetail operaDetail, DataModel dataModel) {
        return null;
    }

    private void analysisInsert(String table, JSONObject jsonObject, JSONObject attrMappJson, OperaDetail operaDetail, DataModel dataModel) {
    }
}
