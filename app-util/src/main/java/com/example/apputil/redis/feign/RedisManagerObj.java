package com.example.apputil.redis.feign;

import java.io.Serializable;

public class RedisManagerObj implements Serializable {
    private Long expireTime;
    private String keyName;
    private String preKey;
    private String timeUnit;
    private String clsNumber;
    private String validType;
    private String setAuth;
    private String getAuth;

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getPreKey() {
        return preKey;
    }

    public void setPreKey(String preKey) {
        this.preKey = preKey;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getClsNumber() {
        return clsNumber;
    }

    public void setClsNumber(String clsNumber) {
        this.clsNumber = clsNumber;
    }

    public String getValidType() {
        return validType;
    }

    public void setValidType(String validType) {
        this.validType = validType;
    }

    public String getSetAuth() {
        return setAuth;
    }

    public void setSetAuth(String setAuth) {
        this.setAuth = setAuth;
    }

    public String getGetAuth() {
        return getAuth;
    }

    public void setGetAuth(String getAuth) {
        this.getAuth = getAuth;
    }
}
