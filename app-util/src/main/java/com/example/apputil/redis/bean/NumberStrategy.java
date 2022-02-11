package com.example.apputil.redis.bean;

public class NumberStrategy {
    private String stName;
    private String stNo;
    private String stContent;
    private String genType;
    private Long noLength;
    private Long initValue;
    private Long step;

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
    }

    public String getStNo() {
        return stNo;
    }

    public void setStNo(String stNo) {
        this.stNo = stNo;
    }

    public String getStContent() {
        return stContent;
    }

    public void setStContent(String stContent) {
        this.stContent = stContent;
    }

    public String getGenType() {
        return genType;
    }

    public void setGenType(String genType) {
        this.genType = genType;
    }

    public Long getNoLength() {
        return noLength;
    }

    public void setNoLength(Long noLength) {
        this.noLength = noLength;
    }

    public Long getInitValue() {
        return initValue;
    }

    public void setInitValue(Long initValue) {
        this.initValue = initValue;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }
}
