package com.example.appdata.model;

import java.math.BigDecimal;

public class OperaDetail {
    private BigDecimal relaID;
    private BigDecimal apiID;
    private String fldTypeObhjName;
    private String dataObjID;
    private String relaDataObjID;
    private String attrMapping;
    private String sqlLogic;
    private String sqlCondition;
    private String sqlSort;
    private String relaMapping;
    private String operateType;

    public BigDecimal getRelaID() {
        return relaID;
    }

    public void setRelaID(BigDecimal relaID) {
        this.relaID = relaID;
    }

    public BigDecimal getApiID() {
        return apiID;
    }

    public void setApiID(BigDecimal apiID) {
        this.apiID = apiID;
    }

    public String getFldTypeObhjName() {
        return fldTypeObhjName;
    }

    public void setFldTypeObhjName(String fldTypeObhjName) {
        this.fldTypeObhjName = fldTypeObhjName;
    }

    public String getDataObjID() {
        return dataObjID;
    }

    public void setDataObjID(String dataObjID) {
        this.dataObjID = dataObjID;
    }

    public String getRelaDataObjID() {
        return relaDataObjID;
    }

    public void setRelaDataObjID(String relaDataObjID) {
        this.relaDataObjID = relaDataObjID;
    }

    public String getAttrMapping() {
        return attrMapping;
    }

    public void setAttrMapping(String attrMapping) {
        this.attrMapping = attrMapping;
    }

    public String getSqlLogic() {
        return sqlLogic;
    }

    public void setSqlLogic(String sqlLogic) {
        this.sqlLogic = sqlLogic;
    }

    public String getSqlCondition() {
        return sqlCondition;
    }

    public void setSqlCondition(String sqlCondition) {
        this.sqlCondition = sqlCondition;
    }

    public String getSqlSort() {
        return sqlSort;
    }

    public void setSqlSort(String sqlSort) {
        this.sqlSort = sqlSort;
    }

    public String getRelaMapping() {
        return relaMapping;
    }

    public void setRelaMapping(String relaMapping) {
        this.relaMapping = relaMapping;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
