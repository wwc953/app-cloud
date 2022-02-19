package com.example.apputil.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    public static void setRequestUtil(SpringMVCUtil requestUtil) {
        ResourceUtil.requestUtil = requestUtil;
    }

    public static String getCurrentResource() {
        try {
            HttpServletRequest request = requestUtil.getRequest();
            if (request == null) {
                log.error("获取不到当前请求，当前用户资源内容返回null");
                return null;
            } else {
                String resource = request.getHeader(RESOURCE_NAME);
                if (!StringUtils.isEmpty(request)) {
                    resource = URLDecoder.decode(resource, "utf-8");
                }
                return resource;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
