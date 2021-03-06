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
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
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
        log.error("dataModel==>{}", JsonUtil.convertObjectToJson(dataModel));
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
        log.error("executePost<==={}", JsonUtil.convertObjectToJson(resultMap));
        return resultMap;
    }

    private Object analysisJson(String table, Object obj, DataModel dataModel) {
        if (!(obj instanceof JSONObject)) {
            log.error("{}????????????????????????????????????:{}", table, obj.getClass().getName());
            return null;
        }

        JSONObject jsonObject = (JSONObject) obj;
        String operateType = jsonObject.get("operateType") == null ? "" : jsonObject.get("operateType").toString();
        log.info("analysisJson::{}????????????????????????{}", table, operateType);
        if (!"no".equals(operateType) && !"".equals(operateType)) {
            Map<String, List<OperaDetail>> doConfigMapping = dataModel.getDoConfigMapping();
            List<OperaDetail> list = doConfigMapping.get(table);
            if (list == null) {
                throw new RuntimeException("?????????" + table + "???????????????????????????");
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
            throw new RuntimeException("?????????" + table + "???????????????????????????");
        }
        String attrMapping = operaDetail.getAttrMapping();
        JSONObject attrMappJson = null;
        try {
            attrMappJson = JSONObject.fromObject(attrMapping);
        } catch (Exception e) {
            throw new RuntimeException("??????" + table + "????????????????????????!", e);
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
                throw new RuntimeException("??????????????????????????????" + table + "????????????:" + dataId);
            }

            if ("query".equals(operateType)) {
                key = operaDetail.getDataModelObhjName();
                log.info("??????query??????...");
                log.info("jsonObject:{}", JsonUtil.convertObjectToJson(jsonObject));
                log.info("key:{} ", key);
                log.info("operaDetail:{}", JsonUtil.convertObjectToJson(operaDetail));
                log.info("dataModel:{}", JsonUtil.convertObjectToJson(dataModel));
                List<Map<String, Object>> analysisQuery = analysisQuery(jsonObject, key, operaDetail, dataModel);
                if (analysisQuery != null && analysisQuery.size() > 0) {
                    Map<String, Object> map = analysisQuery.get(0);
                    EntityUtils.clobToString(map);
                    return map;
                } else {
                    return null;
                }
            }

            if ("update".equals(operateType)) {
                Map<String, JSONObject> curent = dataModel.getCurent();
                curent.put(table, jsonObject);
                dataModel.setCurent(curent);
                log.info("??????update??????...");
                log.info("jsonObject:{}", JsonUtil.convertObjectToJson(jsonObject));
                log.info("attrMappJson:{} ", JsonUtil.convertObjectToJson(attrMappJson));
                log.info("operaDetail:{}", JsonUtil.convertObjectToJson(operaDetail));
                log.info("dataModel:{}", JsonUtil.convertObjectToJson(dataModel));
                analysisUpdate(table, jsonObject, attrMappJson, operaDetail, dataModel);
            } else {
                if (!"delete".equals(operateType)) {
                    throw new RuntimeException("????????????????????????????????????????????????" + operateType);
                }
                boolean gtxFlag = dataModel.getGtxFlag();
                if (gtxFlag) {
                    po = operaDetail.getDataModelObhjName();
                    gtsResult = new HashMap();
                    analysisQueryForGtx(jsonObject, po, operaDetail, gtsResult, dataModel);
                    if (gtsResult == null || gtsResult.size() < 1) {
                        throw new RuntimeException("????????????????????????");
                    }
                    deleteGTXdata(gtsResult, dataModel);
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
        String table = operaDetail.getDataModelObhjName();
        String fk = operaDetail.getRelaDataObjID();
        if (StringUtils.isNotBlank(fk)) {
            JSONObject objJson = JSONObject.fromObject(fk);
            Iterator keys = objJson.keys();

            String next = null;
            Object object = null;
            String condititon = null;
            SQL from = null;
            do {
                if (!keys.hasNext()) {
                    break;
                }
                next = keys.next().toString();
                object = objJson.get(next);
                if (object instanceof JSONArray) {
                    JSONArray ary = (JSONArray) object;
                    from = (new SQL()).SELECT(next).FROM(table);
                    Set<Map.Entry<String, Object>> entrySet = conditionMap.entrySet();
                    Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> entry = iterator.next();
                        condititon = " in (" + entry.getValue() + ")";
                        from.WHERE(entry.getKey() + condititon);
                    }

                    for (int i = 0; i < ary.size(); i++) {
                        String string = ary.get(i).toString();
                        String[] split = string.split("\\.");
                        String condition = "(" + from.toString() + ")";
                        List<OperaDetail> subOperDeatil = poConfigMapping.get(split[0]);
                        Map<String, Object> subCondition = new HashMap<>();
                        subCondition.put(split[1], condition);
                        analysisDelete(params, subOperDeatil.get(0), conditionMap, poConfigMapping);
                    }
                }
            } while (!(object instanceof JSONObject));

            JSONObject hsonOb = (JSONObject) object;
            from = (new SQL()).SELECT(next).FROM(table);
            Set<Map.Entry<String, Object>> entrySet = conditionMap.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                condititon = " in (" + entry.getValue() + ") ";
                from.WHERE(entry.getKey() + condititon);
            }

            String string = hsonOb.toString();
            String[] split = string.split("\\.");
            String condition = "(" + from.toString() + ")";
            List<OperaDetail> subOperDeatil = poConfigMapping.get(split[0]);
            Map<String, Object> subCondition = new HashMap<>();
            subCondition.put(split[1], condition);
            analysisDelete(params, subOperDeatil.get(0), conditionMap, poConfigMapping);
        }
        params.put("tableName", table);
        params.put("conditions", conditionMap);
        doDelete(params);
    }

    private void analysisUpdate(String table, JSONObject jsonObject, JSONObject attrMappJson, OperaDetail operaDetail, DataModel dataModel) {
        Map<String, List<OperaDetail>> doConfigMapping = dataModel.getDoConfigMapping();
        Map<String, Object> conditionMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> hashMap = new HashMap<>();
        String po = operaDetail.getDataModelObhjName();
        params.put("tableName", po);
        String pk = operaDetail.getDataObjID();
        boolean idFlag = true;

        Iterator keys = jsonObject.keys();

        String subTab;
        JSONArray obAry = null;
        labe17:
        do {
            while (keys.hasNext()) {
                String key = keys.next().toString();
                Object object = jsonObject.get(key);
                if (object instanceof JSONArray) {
                    obAry = (JSONArray) object;
                    subTab = table + "." + key;
                    if (doConfigMapping.containsKey(subTab)) {
                        continue labe17;
                    }
                    log.error("??????????????????{}??????????????????????????????????????????????????????");
                } else if (object instanceof JSONObject) {
                    analysisJson(table + "." + key, object, dataModel);
                } else if (!"operateType".equals(key) && attrMappJson.containsKey(key)) {
                    if (pk.equals(attrMappJson.getString(key))) {
                        conditionMap.put(attrMappJson.getString(key), key);
                        idFlag = false;
                    } else {
                        Object val = jsonObject.get(key);
                        if (val == null || "null".equals(String.valueOf(val))) {
                            continue;
                        }
                        hashMap.put(attrMappJson.getString(key), key);
                    }
                    params.put(key, jsonObject.get(key));
                }
            }

            if (idFlag) {
                throw new RuntimeException("?????????" + po + "????????????");
            }
            if (hashMap.size() > 0) {
                Map<String, Map<String, String>> colTypes = dataModel.getColTypes();
                params.put("conditions", conditionMap);
                params.put("columns", hashMap);
                params.put("tabColumns", colTypes.get(po));
                doUpdate(params);
            }
            return;
        } while (obAry.size() < 1);

        Iterator iterator = obAry.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            analysisJson(subTab, obj, dataModel);
        }
    }


    private List<Map<String, Object>> analysisQuery(JSONObject jsonObject, String po, OperaDetail operaDetail, DataModel dataModel) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> conditionMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> columnsMap = new HashMap<>();
        params.put("tableName", po);
        String attrMapping = operaDetail.getAttrMapping();
        JSONObject attrMappJson = JSONObject.fromObject(attrMapping);
        String fk = operaDetail.getRelaDataObjID();
        Iterator its = attrMappJson.keys();
        while (its.hasNext()) {
            String key = its.next().toString();
            if (jsonObject.containsKey(key)) {
                conditionMap.put(key, attrMappJson.getString(key));
                params.put(key, jsonObject.get(key));
            }

            if (jsonObject.containsKey(attrMappJson.get(key))) {
                conditionMap.put(attrMappJson.getString(key), attrMappJson.getString(key));
                params.put(attrMappJson.get(key).toString(), jsonObject.get(attrMappJson.get(key)));
            }

            if (key.contains(".")) {
                int lastIndexOf = key.lastIndexOf(".");
                String attr = key.substring(lastIndexOf + 1, key.length());
                columnsMap.put(attr, attrMappJson.getString(key));
            } else {
                columnsMap.put(key, attrMappJson.getString(key));
            }
        }

        Map<String, Map<String, String>> colTypes = dataModel.getColTypes();
        params.put("tabColumns", colTypes.get(po));
        params.put("conditions", conditionMap);
        params.put("columns", columnsMap);
        if (conditionMap.size() < 1) {
            throw new RuntimeException(po + "???????????????!");
        }

        List<Map<String, Object>> selectPublic = selectPublic(params);
        if (selectPublic != null && selectPublic.size() >= 1) {
            for (Map map : selectPublic) {
                EntityUtils.clobToString(map);
                map.put("operateType", "no");
            }
            if (StringUtils.isNotEmpty(fk)) {
                querySub(fk, attrMappJson, selectPublic, dataModel);
            }
            return selectPublic;
        }
        return result;
    }

    private void querySub(String fk, JSONObject attrMappJson, List<Map<String, Object>> selectPublic, DataModel dataModel) {
        Iterator its = attrMappJson.keys();
        JSONObject objJson = JSONObject.fromObject(fk);
        Iterator keys = objJson.keys();
        while (keys.hasNext()) {
            String next = keys.next().toString();
            String fkMain = null;

            while (its.hasNext()) {
                String key = its.next().toString();
                if (next.equals(attrMappJson.getString(key))) {
                    fkMain = key;
                    break;
                }
            }

            Object object = objJson.get(next);
            if (object instanceof JSONArray) {
                JSONArray ary = (JSONArray) object;
                for (int i = 0; i < ary.size(); i++) {
                    String string = ary.get(i).toString();
                    String[] split = string.split("\\.");
                    for (int j = 0; j < selectPublic.size(); j++) {
                        querySubEntry(selectPublic, fkMain, split, dataModel);
                    }
                }
            } else {
                String string = object.toString();
                String[] split = string.split("\\.");
                querySubEntry(selectPublic, fkMain, split, dataModel);
            }

        }
    }

    private void querySubEntry(List<Map<String, Object>> selectPublic, String fkMain, String[] split, DataModel dataModel) {
        Map<String, List<OperaDetail>> poConfigMapping = dataModel.getPoConfigMapping();
        for (int i = 0; i < selectPublic.size(); i++) {
            Map<String, Object> map = selectPublic.get(i);
            Object object2 = map.get(fkMain);
            Map<String, Object> param = new HashMap<>();
            if (object2 != null) {
                String table = split[0];
                param.put(split[1], object2);
                List<OperaDetail> operaDetails = poConfigMapping.get(table);
                if (operaDetails == null) {
                    throw new RuntimeException("??????????????????????????????????????????" + table);
                }
                if (operaDetails.size() > 1) {
                    for (OperaDetail suboperaDetail : operaDetails) {
                        getSubEntityList(suboperaDetail, split[0], map, param, dataModel);
                    }
                } else {
                    OperaDetail suboperaDetail = operaDetails.get(0);
                    getSubEntityList(suboperaDetail, split[0], map, param, dataModel);
                }
            }
        }
    }

    private void getSubEntityList(OperaDetail suboperaDetail, String table, Map<String, Object> map, Map<String, Object> params, DataModel dataModel) {
        List<Map<String, Object>> analysisQuery = analysisQuery(JSONObject.fromObject(params), table, suboperaDetail, dataModel);
        String dol = suboperaDetail.getFldTypeObhjName();
        String[] split2 = dol.split("\\.");
        if ("one".equals(suboperaDetail.getRelaMapping())) {
            if (analysisQuery != null && analysisQuery.size() > 0) {
                Map<String, Object> map2 = analysisQuery.get(0);
                map.put(split2[split2.length - 1], map2);
            } else {
                map.put(split2[split2.length - 1], null);
            }
        } else {
            if (!"many".equals(suboperaDetail.getRelaMapping()) && suboperaDetail.getRelaMapping() != null) {
                throw new RuntimeException("??????????????????????????????????????????" + table);
            }
            map.put(split2[split2.length - 1], analysisQuery);
        }
    }

    private List<Map<String, Object>> selectPublic(Map<String, Object> params) {
        log.info("selectPublic ===> {}", JsonUtil.convertObjectToJson(params));
        List<Map<String, Object>> result = arrgMapper.selectPublic(params);
        log.info("selectPublic <=== {}", JsonUtil.convertObjectToJson(result));
        return result;
    }

    private void analysisInsert(String table, JSONObject jsonObject, JSONObject attrMappJson, OperaDetail operaDetail, DataModel dataModel) {
        Map<String, Object> hashMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        String po = operaDetail.getDataModelObhjName();
        if (po == null) {
            throw new RuntimeException("??????????????????," + table + "?????????????????????????????????");
        }
        params.put("tableName", po);
        Iterator its = attrMappJson.keys();
        while (its.hasNext()) {
            String key = its.next().toString();
            if (key.contains(".")) {
                String[] split = key.split("\\.");
                String relaCol = split[split.length - 1];
                String relaColumnValue = getRelaColumnValue(key, key, relaCol, dataModel);
                if (relaColumnValue == null) {
                    log.error("??????????????????????????????{}????????????", key);
                } else {
                    hashMap.put(attrMappJson.getString(key), attrMappJson.getString(key));
                    params.put(attrMappJson.getString(key), relaColumnValue);
                }
            } else {
                Object val = jsonObject.get(key);
                if (val != null && !"null".equals(String.valueOf(val))) {
                    hashMap.put(attrMappJson.getString(key), key);
                    params.put(key, jsonObject.getString(key));
                }
            }
        }

        Map<String, Map<String, String>> colTypes = dataModel.getColTypes();
        log.error("colTypes<==>{}", JsonUtil.convertObjectToJson(colTypes));
        params.put("columns", hashMap);
        log.error("po.toUpperCase()<==>{}", po.toUpperCase());
//        params.put("tabColumns", colTypes.get(po.toUpperCase()));
        params.put("tabColumns", colTypes.get(po));
        doInsert(params);
        Iterator it = jsonObject.keys();
        Map<String, JSONObject> curent = dataModel.getCurent();
        if (curent.containsKey(table)) {
            JSONObject jsonObject2 = curent.get(table);
            if (jsonObject.equals(jsonObject2)) {
                return;
            }
        }

        curent.put(table, jsonObject);
        Map<String, List<OperaDetail>> doConfigMapping = dataModel.getDoConfigMapping();
        String key;
        Object object;
        do {
            if (!it.hasNext()) {
                return;
            }
            key = it.next().toString();
            object = jsonObject.get(key);
        } while (attrMappJson.containsKey(key));

        if (object instanceof JSONObject) {
            analysisJson(table + "." + key, object, dataModel);
        } else if (object instanceof JSONArray) {
            String subTab = table + "." + key;
            if (doConfigMapping.containsKey(subTab)) {
                JSONArray obAry = (JSONArray) object;
                for (Object obj : obAry) {
                    analysisJson(subTab, obj, dataModel);
                }
            } else {
                log.error("??????????????????{}??????????????????????????????????????????????????????", subTab);
            }
        }


    }

    private void doInsert(Map<String, Object> params) {
        log.info("doInsert ===> {}", JsonUtil.convertObjectToJson(params));
        arrgMapper.insertMysql(params);
    }

    private void doUpdate(Map<String, Object> params) {
        log.info("updateMysql ===> {}", JsonUtil.convertObjectToJson(params));
        arrgMapper.updateMysql(params);
    }

    private void doDelete(Map<String, Object> params) {
        log.info("doDeleteMysql ===> {}", JsonUtil.convertObjectToJson(params));
        arrgMapper.deleteMysql(params);
    }

    private String getRelaColumnValue(String key, String subKey, String relaCol, DataModel dataModel) {
        Map<String, JSONObject> curent = dataModel.getCurent();
        if (subKey.contains(".")) {
            int lastIndexOf = subKey.lastIndexOf(".");
            String relaTab = subKey.substring(0, lastIndexOf);
            JSONObject relaObj = curent.get(relaTab);
            return relaObj == null ? getRelaColumnValue(key, relaTab, relaCol, dataModel) : aysnoGetValue(key, relaObj, relaTab, relaCol);
        } else {
            JSONObject relaObj = curent.get(subKey);
            if (relaObj == null) {
                throw new RuntimeException("?????????" + key + "????????????");
            }
            return aysnoGetValue(key, relaObj, subKey, relaCol);
        }

    }

    private String aysnoGetValue(String key, JSONObject relaObj, String subKey, String relaCol) {
        if (relaObj.containsKey(relaCol)) {
            return relaObj.getString(relaCol);
        }

        String substring = key.substring(0, subKey.length() + 1);
        Object obj = relaObj;
        String[] split = substring.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            String attr = split[i];
            if (obj instanceof JSONArray) {
                throw new RuntimeException("??????????????????????????????????????????????????????????????????????????????");
            }
            if (obj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) obj;
                if (!jsonObj.containsKey(attr)) {
                    throw new RuntimeException("?????????" + key + "????????????");
                }
                obj = jsonObj.get(attr);
            }
        }

        JSONObject jsonObj = (JSONObject) obj;
        if (jsonObj.containsKey(relaCol)) {
            return jsonObj.getString(relaCol);
        } else {
            throw new RuntimeException("?????????" + key + "????????????");
        }
    }

    public Object executeQuery(String paramStr, DataOperation operaConfig, boolean oneFlag, String appName) {
        Map<String, Object> resultMap = new HashMap<>();
        DataModel dataModel = new DataModel(paramStr, operaConfig, false, appName);
        JSONObject inParams = dataModel.getInParams();
        Iterator keys = inParams.keys();
        String next = (String) keys.next();
        Object object = inParams.get(next);

        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            String operateType = jsonObject.get("operateType") == null ? "" : jsonObject.get("operateType").toString();
            log.info("{}???????????????????????????{}", next, operateType);
            if (!"query".equals(operateType)) {
                throw new RuntimeException("?????????????????????, ??????????????????");
            }
            Map<String, List<OperaDetail>> doConfigMapping = dataModel.getDoConfigMapping();
            List<OperaDetail> list = doConfigMapping.get(next);
            if (list == null) {
                throw new RuntimeException("?????????" + next + "???????????????????????????");
            }

            if (list.size() > 1) {
                Iterator<OperaDetail> iterator = list.iterator();
                List<Map<String, Object>> analysisQuery = null;
                do {
                    if (!iterator.hasNext()) {
                        return resultMap;
                    }
                    OperaDetail operaDetail = iterator.next();
                    String po = operaDetail.getDataModelObhjName();
                    analysisQuery = analysisQuery(jsonObject, po, operaDetail, dataModel);
                    if (analysisQuery.size() == 1) {
                        Map<String, Object> analuMap = analysisQuery.get(0);
                        resultMap.putAll(analuMap);
                    }
                } while (analysisQuery.size() <= 1);
                throw new RuntimeException("????????????????????????????????????????????????????????????????????????????????????????????????");
            }

            OperaDetail operaDetail = list.get(0);
            String po = operaDetail.getDataModelObhjName();
            List<Map<String, Object>> analysisQuery = analysisQuery(jsonObject, po, operaDetail, dataModel);
            if (!oneFlag) {
                return analysisQuery;
            }

            if (analysisQuery.size() > 0) {
                resultMap = analysisQuery.get(0);
            }

        }
        return resultMap;
    }
}
