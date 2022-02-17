package com.example.appcommon.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SnoSt {
    private Long snoStId;

    private String stName;

    private String stNo;

    private String stContent;

    private String genType;

    private Long noLength;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date curDate;

    private Long initValue;

    private Long curValue;

    private Long snoStStep;

    private String tenantId;

    public Long getSnoStId() {
        return snoStId;
    }

    public void setSnoStId(Long snoStId) {
        this.snoStId = snoStId;
    }

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

    public Date getCurDate() {
        return curDate;
    }

    public void setCurDate(Date curDate) {
        this.curDate = curDate;
    }

    public Long getInitValue() {
        return initValue;
    }

    public void setInitValue(Long initValue) {
        this.initValue = initValue;
    }

    public Long getCurValue() {
        return curValue;
    }

    public void setCurValue(Long curValue) {
        this.curValue = curValue;
    }

    public Long getSnoStStep() {
        return snoStStep;
    }

    public void setSnoStStep(Long snoStStep) {
        this.snoStStep = snoStStep;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}