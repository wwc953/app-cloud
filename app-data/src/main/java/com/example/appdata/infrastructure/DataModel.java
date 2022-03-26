package com.example.appdata.infrastructure;

import com.example.appdata.model.DataOperation;
import com.example.appdata.model.OperaDetail;
import com.example.appstaticutil.json.JsonUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModel {
    private List<OperaDetail> operaDetails;
    private Map<String, List<OperaDetail>> poConfigMapping;
    private Map<String, List<OperaDetail>> doConfigMapping;
    private Map<String, JSONObject> curent;
    private Map<String, Map<String, String>> colTypes;
    private JSONObject inParams;
    private String appName;
    private String url;
    private boolean gtxFlag;

    public DataModel(String paramStr, DataOperation operaConfig, boolean flag, String appName) {
        this.url = operaConfig.getApiPath();
        this.appName = appName;
        this.curent = new HashMap<>();
        this.inParams = JsonUtil.convertJsonToObject(paramStr, JSONObject.class);
        this.gtxFlag = flag;
        operaDetails = operaConfig.getcDataSrvRelaDOList();
        colTypes = operaConfig.getColTypes();
        poConfigMapping = new HashMap<>();
        doConfigMapping = new HashMap<>();

        for (OperaDetail detail : operaDetails) {
            String po = detail.getDataModelObhjName();
            if (poConfigMapping.containsKey(po)) {
                List<OperaDetail> list = poConfigMapping.get(po);
                list.add(detail);
            } else {
                List<OperaDetail> list = new ArrayList<>();
                list.add(detail);
                poConfigMapping.put(po, list);
            }

            String dataObj = detail.getFldTypeObhjName();
            if (doConfigMapping.containsKey(dataObj)) {
                List<OperaDetail> list = doConfigMapping.get(dataObj);
                list.add(detail);
            } else {
                List<OperaDetail> list = new ArrayList<>();
                list.add(detail);
                doConfigMapping.put(dataObj, list);
            }
        }

    }

    public List<OperaDetail> getOperaDetails() {
        return operaDetails;
    }

    public void setOperaDetails(List<OperaDetail> operaDetails) {
        this.operaDetails = operaDetails;
    }

    public Map<String, List<OperaDetail>> getPoConfigMapping() {
        return poConfigMapping;
    }

    public void setPoConfigMapping(Map<String, List<OperaDetail>> poConfigMapping) {
        this.poConfigMapping = poConfigMapping;
    }

    public Map<String, List<OperaDetail>> getDoConfigMapping() {
        return doConfigMapping;
    }

    public void setDoConfigMapping(Map<String, List<OperaDetail>> doConfigMapping) {
        this.doConfigMapping = doConfigMapping;
    }

    public Map<String, JSONObject> getCurent() {
        return curent;
    }

    public void setCurent(Map<String, JSONObject> curent) {
        this.curent = curent;
    }

    public Map<String, Map<String, String>> getColTypes() {
        return colTypes;
    }

    public void setColTypes(Map<String, Map<String, String>> colTypes) {
        this.colTypes = colTypes;
    }

    public JSONObject getInParams() {
        return inParams;
    }

    public void setInParams(JSONObject inParams) {
        this.inParams = inParams;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getGtxFlag() {
        return gtxFlag;
    }

    public void setGtxFlag(boolean gtxFlag) {
        this.gtxFlag = gtxFlag;
    }
}
