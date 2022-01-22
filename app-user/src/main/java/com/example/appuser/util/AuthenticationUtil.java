package com.example.appuser.util;

import com.alibaba.fastjson.JSON;
import com.example.appuser.model.People;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationUtil {

    private static SpringMVCUtil springMVCUtil;

    @Autowired
    public static void setSpringMVCUtil(SpringMVCUtil springMVCUtil) {
        AuthenticationUtil.springMVCUtil = springMVCUtil;
    }

    public static People getCurrentUserInfo() {
        HttpServletRequest request = springMVCUtil.getRequest();

        if (request == null) {
            throw new RuntimeException("未获取到当前请求，用户信息返回null");
        }
        String userInfo = request.getHeader("userInfo");
        if (StringUtils.isNotBlank(userInfo)) {
            People people = JSON.parseObject(userInfo, People.class);
            return people;
        }
        return null;
    }
}
