package com.example.apputil.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@Component
@Slf4j
public class ResourceUtil {
    public static final String RESOURCE_NAME = "resourceName";
    private static SpringMVCUtil requestUtil;

    @Autowired
    public void setRequestUtil(SpringMVCUtil requestUtil) {
        ResourceUtil.requestUtil = requestUtil;
    }

    public static String getCurrentResource() {
        try {
            HttpServletRequest request = requestUtil.getRequest();
            if (request == null) {
                log.error("获取不到当前请求，当前用户资源内容返回null");
                return null;
            } else {
                String resourceName = request.getHeader(RESOURCE_NAME);
                if (!StringUtils.isEmpty(resourceName)) {
                    resourceName = URLDecoder.decode(resourceName, "utf-8");
                }
                return resourceName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
