package com.example.apputil.feign;

import com.example.appstaticutil.model.Span;
import com.example.appstaticutil.model.Tracer;
import com.example.appstaticutil.json.JsonUtil;
import com.example.apputil.cmccache.CommonParamManager;
import com.example.apputil.authentication.ResourceUtil;
import com.example.apputil.authentication.SpringMVCUtil;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

/**
 * request拦截器
 */
@Configuration
@Slf4j
public class BaseFeginConfiguration {

    public static final String APP_NAME = "APP_NAME";
    public static final String USER_INFO = "userInfo";
    public static final String TRACE_ID = "traceId";

    @Autowired
    private SpringMVCUtil requestUtil;


//    public HttpServletRequest getRequest() {
//        ServletRequestAttributes sra = (ServletRequestAttributes) Optional.ofNullable(RequestContextHolder.getRequestAttributes()).orElse(null);
//        return sra == null ? null : sra.getRequest();
//    }

    @Bean
    public RequestInterceptor baseInterceptor() {

        RequestInterceptor interceptor = (requestTemplate) -> {
            try {
                requestTemplate.header(APP_NAME, CommonParamManager.getAppName());
                // redis 获取用户信息
                requestTemplate.header(USER_INFO, JsonUtil.convertObjectToJson(null));
                log.info("Fegin中封装了用户信息，uri:{}", requestTemplate.url());
            } catch (Exception e) {
                log.error("获取请求失败，Fegin中不封装用户信息", e);
            }

            try {
                String resourceName = ResourceUtil.getCurrentResource();
                log.info("BaseFeginConfiguration resourceName ======> {}", resourceName);
                if (StringUtils.isNotBlank(resourceName)) {
                    log.info("BaseFeginConfiguration resourceName encode ======> {}", resourceName);
                    requestTemplate.header(ResourceUtil.RESOURCE_NAME, URLEncoder.encode(resourceName, "UTF-8"));
                }
            } catch (Exception e) {
                log.error("获取请求失败，Fegin中不封装资源信息", e);
            }

            try {
                String traceId = "";
                HttpServletRequest request = requestUtil.getRequest();
                if (request != null) {
                    traceId = (String) Optional.ofNullable(request.getAttribute(TRACE_ID)).orElse("");
                }
                if (StringUtils.isBlank(traceId)) {
                    traceId = UUID.randomUUID().toString().replaceAll("-", "");
                    Span span = getArmsSpan();
                    if (span != null && StringUtils.isNotBlank(span.getTraceId())) {
                        traceId = traceId + "," + span.getTraceId();
                    }
                }
                requestTemplate.header(TRACE_ID, traceId);
            } catch (Exception e) {
                log.error("Fegin塞入traceId失败", e);
            }

        };
        log.info("Fegin基础拦截器注册成功。");
        return interceptor;
    }

    private Span getArmsSpan() {
        Span span = Tracer.builder().getSpan();
        return span;
    }

}
