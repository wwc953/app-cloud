package com.example.apputil.authentication;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@Slf4j
@Component
public class AuthenticationUtil {

    private static SpringMVCUtil springMVCUtil;

    @Autowired
    public void setSpringMVCUtil(SpringMVCUtil springMVCUtil) {
        AuthenticationUtil.springMVCUtil = springMVCUtil;
    }

    public static SystemUser getCurrentUserInfo() {
        try {
            HttpServletRequest request = springMVCUtil.getRequest();
            if (request == null) {
                log.error("获取不到当前请求，当前用户资源内容返回null");
            }

            String userInfoStr = request.getHeader("userInfo");
            if (StringUtils.isNotBlank(userInfoStr)) {
                userInfoStr = URLDecoder.decode(userInfoStr, "utf-8");
                SystemUser systemUser = JSON.parseObject(userInfoStr, SystemUser.class);
                return systemUser;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
