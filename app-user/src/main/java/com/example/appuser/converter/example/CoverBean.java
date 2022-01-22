package com.example.appuser.converter.example;

import com.example.appuser.converter.annotion.EnableUserInfoTransform;
import com.example.appuser.converter.annotion.TransfCode;
import com.example.appuser.converter.annotion.TransfUser;

@EnableUserInfoTransform
public class CoverBean {

    private String devCls;

    @TransfCode(valueFrom = "devCls", codeType = "mycodedevCls")
    private String devClsName;

    private String testPerson;
    @TransfUser(valueFrom = "testPerson")
    private String testPersonName;

    public String getTestPerson() {
        return testPerson;
    }

    public void setTestPerson(String testPerson) {
        this.testPerson = testPerson;
    }

    public String getTestPersonName() {
        return testPersonName;
    }

    public void setTestPersonName(String testPersonName) {
        this.testPersonName = testPersonName;
    }

    public String getDevCls() {
        return devCls;
    }

    public void setDevCls(String devCls) {
        this.devCls = devCls;
    }

    public String getDevClsName() {
        return devClsName;
    }

    public void setDevClsName(String devClsName) {
        this.devClsName = devClsName;
    }
}
