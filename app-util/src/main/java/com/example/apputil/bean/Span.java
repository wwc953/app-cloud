package com.example.apputil.bean;

import java.util.HashMap;
import java.util.Map;

public class Span {
    private String appId = "";
    private boolean sampled = true;
    private String traceId = "";
    private String rpcId = "";
    private Map<String, String> baggage = new HashMap<>();
    private Map<String, String> tags = new HashMap();

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isSampled() {
        return sampled;
    }

    public void setSampled(boolean sampled) {
        this.sampled = sampled;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRpcId() {
        return rpcId;
    }

    public void setRpcId(String rpcId) {
        this.rpcId = rpcId;
    }

    public Map<String, String> getBaggage() {
        return baggage;
    }

    public void setBaggage(Map<String, String> baggage) {
        this.baggage = baggage;
    }
}
