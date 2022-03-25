package com.example.appcommon.commondata;

import com.alibaba.fastjson.JSON;
import com.example.appcommon.commondata.customsql.CustomSqlMapper;
import com.example.appstaticutil.groovy.GroovyLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * @param beforeSQL    select * from table t where t.bb is nou null <if test="mgtOrgCode!=null && mgtOrgCode!=''"> and t.mgtOrgCode = #{mgtOrgCode} </if> <if test="aa!=null && aa!=''"> and t.aa=#{aa} </if>
     * @param apiParamJson {"mgtOrgCode":"","aa":""}
     */
    public void getLabelSql(Map<String, Object> params, String beforeSQL, String apiParamJson) {
        try {
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
        Integer pageSize = (Integer) params.get("pageSize");
        Integer pageNo = (Integer) params.get("pageNo");
        if (pageNo < 1) {
            pageNo = 1;
        }
        params.put("beginIndex", (pageNo - 1) * pageSize);
        return customSqlMapper.docustomSqlByPageMySQL(params);
    }
}
