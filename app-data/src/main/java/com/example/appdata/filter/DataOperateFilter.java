package com.example.appdata.filter;

import com.example.appdata.dao.AggrProviderService;
import com.example.appdata.dao.CustomSqlProviderServer;
import com.example.appdata.dao.dataopera.DataOperationMapper;
import com.example.appdata.model.DataOperation;
import com.example.appdata.model.OperaDetail;
import com.example.appstaticutil.json.JsonUtil;
import com.example.appstaticutil.response.ResponseResult;
import com.example.appstaticutil.response.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@WebFilter(urlPatterns = {"/*"}, filterName = "myDataOperaFilter")
public class DataOperateFilter implements Filter {

    DataOperationMapper dataOperationMapper;
    CustomSqlProviderServer customSqlProviderServer;
    AggrProviderService providerService;
    String serverName;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        customSqlProviderServer = context.getBean(CustomSqlProviderServer.class);
        dataOperationMapper = context.getBean(DataOperationMapper.class);
        providerService = context.getBean(AggrProviderService.class);
        serverName = "/" + context.getEnvironment().getProperty("spring.application.name");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String method = req.getMethod();
        String appName = req.getHeader("APP_NAME");
        String uri = serverName + req.getRequestURI();
        String paramStr = null;
        if ("POST".equals(method)) {
            paramStr = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info("当前请求路径:{}", uri);
            log.info("当前接受参数:{}", paramStr);
            DataOperation operaConfig = getOperaConfig(uri);
            log.info("getOperaConfig:---->{}", JsonUtil.convertObjectToJson(operaConfig));
            ResponseResult<Object> responseResult = null;
            if (operaConfig == null) {
                responseResult = ResultUtils.warpResult("-1", "未找到对应路径");
                writeResult(res, responseResult, false);
            } else {
                Object executePost = null;
                if (!"0".equals(operaConfig.getOpType()) && !"01".equals(operaConfig.getOpType())) {
                    if ("1".equals(operaConfig.getOpType())) {
                        if (operaConfig.getColTypes() == null) {
                            writeResult(res, ResultUtils.warpResult("-1", "未找到配置信息中对应表结构，请检查"), false);
                            return;
                        }
                        executePost = providerService.executePost(paramStr, operaConfig, false, appName);
                        if (executePost == null) {
                            executePost = ResultUtils.warpResult("00000", "操作成功");
                        } else {
                            executePost = ResultUtils.warpResult(executePost);
                        }
                        writeResult(res, executePost, true);
                    } else {
                        writeResult(res, ResultUtils.warpResult("-1", "配置信息操作类型有误"), false);
                    }
                } else {
                    // 查询
                    executePost = customSqlProviderServer.executeQuery(paramStr, operaConfig);
                    writeResult(res, executePost, true);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void writeResult(HttpServletResponse response, Object resultObj, boolean flag) {
        if (!flag) {
            response.setStatus(488);
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter writer = null;
        try {
            String json = JsonUtil.convertObjectToJson(resultObj);
            writer = response.getWriter();
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private DataOperation getOperaConfig(String uri) {
        Map<String, String> columns = new HashMap<>();
        Map<String, String> conditions = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        DataOperation dataOperation = dataOperationMapper.getDataOperationByUri(uri);
        if (dataOperation != null) {
            getOperaDetails(dataOperation, uri);
            List<OperaDetail> operaDetails = dataOperation.getcDataSrvRelaDOList();
            if ("1".equals(dataOperation.getOpType()) && !CollectionUtils.isEmpty(operaDetails)) {
                StringBuffer sb = new StringBuffer();
                for (OperaDetail detail : operaDetails) {
                    sb.append("'").append(detail.getDataModelObhjName().toUpperCase()).append("'").append(",");
                }
                sb.deleteCharAt(sb.length() - 1);

//                getDataColumnTypeOracle(columns, conditions, params);
//                params.put("tabName", sb.toString());
//                List<Map<String, Object>> tabColumns = providerService.selectColumnTypeOracle(params);

                getDataColumnTypeMySQL(columns, conditions, params, "appcommon");
                params.put("tabName", sb.toString().toLowerCase());
                List<Map<String, Object>> tabColumns = providerService.selectColumnTypeMySQL(params);

                if (tabColumns != null && tabColumns.size() > 0) {
                    Map<String, Map<String, String>> tabColMap = new HashMap<>(32);
                    Iterator<Map<String, Object>> iterator = tabColumns.iterator();
                    while (iterator.hasNext()) {
                        Map<String, Object> map = iterator.next();
                        String table = map.get("tableName") == null ? "" : map.get("tableName").toString();
                        String columnName = map.get("columnName") == null ? "" : map.get("columnName").toString();
                        String columnType = map.get("columnType") == null ? "" : map.get("columnType").toString();
                        if (tabColMap.containsKey(table)) {
                            Map<String, String> map2 = tabColMap.get(table);
                            map2.put(columnName, columnType);
                        } else {
                            Map<String, String> map2 = new HashMap<>(32);
                            map2.put(columnName, columnType);
                            tabColMap.put(table, map2);
                        }
                    }
                    dataOperation.setColTypes(tabColMap);
                }
            }
        }
        return dataOperation;
    }

    private void getDataColumnTypeOracle(Map<String, String> columns, Map<String, String> conditions, Map<String, Object> params) {
        columns.clear();
        conditions.clear();
        columns.put("tableName", "table_Name");
        columns.put("columnName", "column_Name");
        columns.put("columnType", "data_type");
        conditions.put("tabName", "table_Name");
        params.put("columns", columns);
        params.put("tableName", "all_tab_cols");
//        SELECT data_type as "columnType", table_Name as "tableName", column_Name as "columnName" FROM all_tab_cols WHERE (table_Name in ('REDIS_AUTH_MGT'))
        params.put("conditions", conditions);
    }

    private void getDataColumnTypeMySQL(Map<String, String> columns, Map<String, String> conditions, Map<String, Object> params, String tableSchema) {
        columns.clear();
        conditions.clear();
        columns.put("tableName", "table_Name");
        columns.put("columnName", "column_Name");
        columns.put("columnType", "data_type");
        conditions.put("tabName", "table_Name");
//        params.put("table_schema", tableSchema);
        params.put("columns", columns);
//        params.put("tableName", "all_tab_cols");
        params.put("tableName", "information_schema.columns");
//        select COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT from information_schema.columns where TABLE_NAME='表名'
        params.put("conditions", conditions);
    }

    private void getOperaDetails(DataOperation dataOperation, String uri) {
        int lastIndexOf = uri.lastIndexOf("/");
        String beforeuri = uri.substring(0, lastIndexOf + 1);
        String end = uri.substring(lastIndexOf + 1, uri.length());

        List<String> uriList = new ArrayList<>();
        int indexof = end.indexOf("_AND_");
        if (indexof != -1) {
            String[] split = end.split("_AND_");
            for (int i = 0; i < split.length; ++i) {
                String neuri = beforeuri + split[i];
                uriList.add(neuri);
            }
        }

        int getAllIndex = end.indexOf("_getAll");
        int getOneIndex = end.indexOf("_getOne");
        int batchIndex = end.indexOf("_batch");
        if (getAllIndex != -1) {
            String getAllUri = beforeuri + end.split("_getAll")[0];
            uriList.add(getAllUri);
        } else if (getOneIndex != -1) {
            String getOneUri = beforeuri + end.split("_getOne")[0];
            uriList.add(getOneUri);
        } else if (batchIndex != -1) {
            String batchUri = beforeuri + end.split("_batch")[0];
            uriList.add(batchUri);
        }

        if (uriList.size() > 0) {
            List<DataOperation> subList = dataOperationMapper.getDataOperationByUris(uriList);
            if (subList.size() != uriList.size()) {
                throw new RuntimeException("数据服务配置信息异常：未找到对应的全部聚合数据");
            }
            List<OperaDetail> operaDetails = new ArrayList<>();
            for (DataOperation subDataOpe : subList) {
                operaDetails.addAll(subDataOpe.getcDataSrvRelaDOList());
            }
            dataOperation.setcDataSrvRelaDOList(operaDetails);
        }


    }


}
