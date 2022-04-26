package com.example.appdata.dao;

import com.alibaba.fastjson.JSON;
import com.example.appdata.dao.customsql.CustomSqlMapper;
import com.example.appdata.model.CustomSqlOpt;
import com.example.appdata.model.DataOperation;
import com.example.appdata.model.OperaDetail;
import com.example.appstaticutil.entity.EntityUtils;
import com.example.appstaticutil.groovy.GroovyLoader;
import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.response.ResponseResult;
import com.example.appstaticutil.response.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CustomSqlProviderServer {

    @Autowired
    CustomSqlMapper customSqlMapper;

    private static Pattern PATTERN = Pattern.compile("\\#\\{(.*?)\\}");

    public ResponseResult executeQuery(String inputParamStr, DataOperation operaConfig) {
        ResponseResult<Object> result = ResultUtils.warpResult(new HashMap<>(), 0);

        String beforeSQL = operaConfig.getBeforeSQL();
        beforeSQL = StringUtils.trim(beforeSQL);
        boolean selectFlag = StringUtils.startsWithIgnoreCase(beforeSQL, "select");
        if (!selectFlag) {
            throw new RuntimeException("自定义sql不允许执行非查询语句");
        }

        Map params = new HashMap();
        if (StringUtils.isNotBlank(inputParamStr)) {
            params = JsonUtil.convertJsonToMap(inputParamStr);
        }
        String opType = operaConfig.getOpType();
        if ("01".equals(opType)) {
            //自定义标签 if
            getLabelSql(params, operaConfig);
        } else {
            getCommonSql(params, operaConfig);
        }

        String oneFlag = params.get("queryType") == null ? "many" : params.get("queryType").toString();
        // 分页数据
        log.error("执行参数:{}", JsonUtil.convertObjectToJson(params));
        if (params.containsKey("pageSize") && params.get("pageSize") != null && !"null".equals(params.get("pageSize"))) {
            if (params.get("pageNo") != null || "null".equals(params.get("pageNo"))) {
                params.put("pageNo", 1);
            }
            List<Map> docustomSqlByPageMySQL = docustomSqlByPageMySQL(params);
            Integer total = docustomSqlGetTotal(params);
            if ("one".equals(oneFlag)) {
                if (docustomSqlByPageMySQL != null && docustomSqlByPageMySQL.size() > 0) {
                    Map res = docustomSqlByPageMySQL.get(0);
                    EntityUtils.clobToString(res);
                    return ResultUtils.warpResult(res, total);
                }
            } else {
                for (Map res : docustomSqlByPageMySQL) {
                    EntityUtils.clobToString(res);
                }
                return ResultUtils.warpResult(docustomSqlByPageMySQL, total);
            }
        } else {
            //不分页
            List<Map> docustomSqlMySQL = docustomSqlMySQL(params);
            if ("one".equals(oneFlag)) {
                if (docustomSqlMySQL != null && docustomSqlMySQL.size() > 0) {
                    Map res = docustomSqlMySQL.get(0);
                    EntityUtils.clobToString(res);
                    return ResultUtils.warpResult(res);
                }
            } else {
                for (Map res : docustomSqlMySQL) {
                    EntityUtils.clobToString(res);
                }
                return ResultUtils.warpResult(docustomSqlMySQL);
            }
        }

        return result;
    }

    private void getCommonSql(Map<String, Object> params, DataOperation operaConfig) {
        List<CustomSqlOpt> conditions = new ArrayList<>();
        Map<String, Object> sqlParams = new HashMap<>();
        List<OperaDetail> operaDetails = operaConfig.getcDataSrvRelaDOList();
        String afterSQL = operaConfig.getAfterSQL();
        String beforeSQL = operaConfig.getBeforeSQL();
        beforeSQL = StringUtils.trim(beforeSQL);
        beforeSQL = beforeSQL.substring(6, beforeSQL.length());
        beforeSQL = dealSqlStr(beforeSQL, params);
        if (afterSQL != null) {
            afterSQL = dealSqlStr(afterSQL, params);
        }
        sqlParams.put("beforeSql", beforeSQL);
        sqlParams.put("afterSql", afterSQL);

        if (operaDetails != null && operaDetails.size() > 0) {
            Iterator<OperaDetail> iterator = operaDetails.iterator();
            while (iterator.hasNext()) {
                OperaDetail operaDetail = iterator.next();
                CustomSqlOpt opt = new CustomSqlOpt();
                String sqlCondition = operaDetail.getSqlCondition();
                Matcher m = PATTERN.matcher(sqlCondition);

                ArrayList<String> paramNames = new ArrayList<>();
                while (m.find()) {
                    paramNames.add(m.group().substring(2, m.group().length() - 1));
                }

                boolean addFlag = true;
                if (paramNames.size() > 0) {
                    for (String pp : paramNames) {
                        if (params.get(pp) == null) {
                            addFlag = false;
                            break;
                        }
                    }
//                    Iterator<String> iterator1 = paramNames.iterator();
//                    while (iterator1.hasNext()) {
//                        String param = iterator1.next();
//                        if (params.get(param) == null) {
//                            addFlag = false;
//                            break;
//                        }
//                    }
                }

                if (addFlag) {
                    sqlCondition = dealInOrLikeCondition(params, paramNames, sqlCondition);
                    opt.setOptLogic(operaDetail.getSqlLogic().toUpperCase());
                    opt.setCondition(sqlCondition);
                    conditions.add(opt);
                }
            }
        }
        sqlParams.put("conditions", conditions);
        String assembleCustomSql = assembleCustomSql(sqlParams);
        params.put("finalSQL", assembleCustomSql);
    }

    public String assembleCustomSql(Map<String, Object> map) {
        String beforeSql = (String) map.get("beforeSql");
        String afterSql = (String) map.get("afterSql");
        List<CustomSqlOpt> conditions = (List<CustomSqlOpt>) map.get("conditions");
        String resultSQL = new SQL() {
            {
                SELECT(beforeSql);
                for (CustomSqlOpt opt : conditions) {
                    if ("OR".equals(opt.getOptLogic())) {
                        OR().WHERE(opt.getCondition());
                    } else if ("AND".equals(opt.getOptLogic())) {
                        AND().WHERE(opt.getCondition());
                    } else {
                        WHERE(opt.getCondition());
                    }
                }
            }
        }.toString();

        if (afterSql != null) {
            resultSQL = resultSQL + " " + afterSql;
        }

        return resultSQL;
    }


    private String dealSqlStr(String valSql, Map<String, Object> apiParams) {
        Matcher m = PATTERN.matcher(valSql);
        List paramNames = new ArrayList<>();
        while (m.find()) {
            paramNames.add(m.group().substring(2, m.group().length() - 1));
        }
        String res = dealInOrLikeCondition(apiParams, paramNames, valSql);
        log.debug("dealSqlStr: {}", res);
        return res;
    }

    /**
     * 判断是否有in和like
     *
     * @param apiParams
     * @param paramNamesList
     * @param valSql
     * @return
     */
    private String dealInOrLikeCondition(Map<String, Object> apiParams, List<String> paramNamesList, String valSql) {
        for (String paramName : paramNamesList) {
            String parastr = "#{" + paramName + "}";
            String deleteWhitespace = StringUtils.deleteWhitespace(valSql);
            int indexOf = deleteWhitespace.indexOf(parastr);
            if (indexOf < 0) continue;
            if (indexOf >= 3) {
                String subSequence = deleteWhitespace.substring(indexOf - 3, indexOf);
                if ("in(".equals(subSequence.toLowerCase())) {
                    valSql = valSql.replace(parastr, "${" + paramName + "}");
                    Object object = apiParams.get(paramName);
                    if (object instanceof List) {
                        List list = (List) object;
                        if (list.size() < 1) {
                            throw new RuntimeException(paramName + "参数至少存在一个！");
                        }
                        StringBuffer sb = new StringBuffer();
                        for (Iterator localit2 = list.iterator(); localit2.hasNext(); ) {
                            Object obj = localit2.next();
                            sb.append("'").append(obj.toString()).append("'").append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        apiParams.put(paramName, sb.toString());
                    }
                }
            }
            if (indexOf >= 3) {
                String likeStr = deleteWhitespace.substring(indexOf - 3, indexOf + 3);
                if (likeStr.contains("%")) {
                    String substring = deleteWhitespace.substring(indexOf - 9, indexOf + 3).toUpperCase();
                    if (substring.contains("LIKE")) {
                        valSql = valSql.replace(parastr, "${" + paramName + "}");
                    }
                }
            }
        }
        return valSql;
    }

    /**
     * @param params
     * @operaConfig operaConfig.beforeSQL    select * from table t where t.bb is nou null <if test="mgtOrgCode!=null && mgtOrgCode!=''"> and t.mgtOrgCode = #{mgtOrgCode} </if> <if test="aa!=null && aa!=''"> and t.aa=#{aa} </if>
     * @operaConfig apiParamJson {"mgtOrgCode":"","aa":""}
     */
    public void getLabelSql(Map<String, Object> params, DataOperation operaConfig) {
        try {
            String beforeSQL = operaConfig.getBeforeSQL();
            String apiParamJson = operaConfig.getApiParam();
            beforeSQL = beforeSQL.trim();
            String replaceAll = beforeSQL.replaceAll("&&", "&amp;&amp;");
            String sql = "<sql>" + replaceAll + "</sql>";
            Document doo = DocumentHelper.parseText(sql);
            Element rootElement = doo.getRootElement();

            Map<String, Object> apiParams = new HashMap<>();
            if (apiParamJson != null) {
                apiParams = JSON.parseObject(apiParamJson, Map.class);
                for (String key : apiParams.keySet()) {
                    apiParams.put(key, null);
                }
                apiParams.putAll(params);
                dealLabels(rootElement, apiParams);
            }

            String valSQL = rootElement.getStringValue();
            log.debug("valSQL ===> {}", valSQL);
            String finalSQL = dealSqlStr(valSQL, apiParams);
            log.info("finalSQL ==> {}", finalSQL);
            params.put("finalSQL", finalSQL);

        } catch (Exception e) {
            throw new RuntimeException("getLabelSql解析配置失败", e);
        }
    }

    /**
     * 解析if标签，去除无效if
     *
     * @param rootElement
     * @param params
     */
    private void dealLabels(Element rootElement, Map<String, Object> params) {
        Iterator<Element> it = rootElement.elementIterator("if");
        while (it.hasNext()) {
            Element next = it.next();
            String attributeValue = next.attributeValue("test");
            Object parseWithBinding = null;
            if (attributeValue != null) {
                try {
                    parseWithBinding = GroovyLoader.parseWithBinding(attributeValue, params);
                } catch (Exception e) {
                    throw new RuntimeException("dealLabels解析配置失败", e);
                }
                if (parseWithBinding.equals(true)) {
                    dealLabels(next, params);
                } else {
                    rootElement.remove(next);
                }
            }
        }
    }

    /**
     * 真正 执行查询语句
     *
     * @param params
     * @return
     */
    public List<Map<String, Object>> docustomSql(Map<String, Object> params) {
        return customSqlMapper.docustomSql(params);
    }

    /**
     * 真正 执行查询语句 带分页
     *
     * @param params
     * @return
     */
    public List<Map<String, Object>> docustomSqlByPage(Map<String, Object> params) {
        return customSqlMapper.docustomSqlByPage(params);
    }

    /**
     * 获取个数
     *
     * @param params
     * @return
     */
    public Integer docustomSqlGetTotal(Map<String, Object> params) {
        return customSqlMapper.docustomSqlGetTotal(params);
    }

    /**
     * MySQL
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> docustomSqlMySQL(Map<String, Object> param) {
        return customSqlMapper.docustomSqlMySQL(param);
    }

    /**
     * MySQL 分页
     *
     * @param params
     * @return
     */
    public List<Map<String, Object>> docustomSqlByPageMySQL(Map<String, Object> params) {
        Integer pageSize = Integer.valueOf(String.valueOf(params.get("pageSize")));
        Integer pageNo = Integer.valueOf(String.valueOf(params.get("pageNo")));
        if (pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        params.put("beginIndex", (pageNo - 1) * pageSize);
        return customSqlMapper.docustomSqlByPageMySQL(params);
    }
}
