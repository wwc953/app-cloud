package com.example.appdata.model;

import java.util.List;
import java.util.Map;

public class DataOperation {
    private String apiID;
    private String centerName;
    private String apiName;
    private String apiPath;
    private String opType;
    private String callSource;
    private String apiParam;
    private String afterSQL;
    private String beforeSQL;
    private String sqlParam;
    private Map<String,Map<String,String>> colTypes;
    private List<OperaDetail> cDataSrvRelaDOList;

    public String getApiID() {
        return apiID;
    }

    public void setApiID(String apiID) {
        this.apiID = apiID;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getCallSource() {
        return callSource;
    }

    public void setCallSource(String callSource) {
        this.callSource = callSource;
    }

    public String getApiParam() {
        return apiParam;
    }

    public void setApiParam(String apiParam) {
        this.apiParam = apiParam;
    }

    public String getAfterSQL() {
        return afterSQL;
    }

    public void setAfterSQL(String afterSQL) {
        this.afterSQL = afterSQL;
    }

    public String getBeforeSQL() {
        return beforeSQL;
    }

    public void setBeforeSQL(String beforeSQL) {
        this.beforeSQL = beforeSQL;
    }

    public String getSqlParam() {
        return sqlParam;
    }

    public void setSqlParam(String sqlParam) {
        this.sqlParam = sqlParam;
    }

    public Map<String, Map<String, String>> getColTypes() {
        return colTypes;
    }

    public void setColTypes(Map<String, Map<String, String>> colTypes) {
        this.colTypes = colTypes;
    }

    public List<OperaDetail> getcDataSrvRelaDOList() {
        return cDataSrvRelaDOList;
    }

    public void setcDataSrvRelaDOList(List<OperaDetail> cDataSrvRelaDOList) {
        this.cDataSrvRelaDOList = cDataSrvRelaDOList;
    }
}
