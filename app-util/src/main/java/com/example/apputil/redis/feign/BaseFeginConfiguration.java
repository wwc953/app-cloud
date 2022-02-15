package com.example.apputil.redis.feign;

import com.example.appstaticutil.json.JsonUtil;
import com.example.apputil.bean.Span;
import com.example.apputil.bean.Tracer;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * request拦截器
 */
@Configuration
@Slf4j
public class BaseFeginConfiguration {

    public static final String APP_NAME = "APP_NAME";
    public static final String USER_INFO = "userInfo";
    public static final String RESOURCE_NAME = "resourceName";
    public static final String TRACE_ID = "traceId";

    public HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) Optional.ofNullable(RequestContextHolder.getRequestAttributes()).orElse(null);
        return sra == null ? null : sra.getRequest();
    }

    @Bean
    public RequestInterceptor baseInterceptor() {

        RequestInterceptor interceptor = (requestTemplate) -> {
            requestTemplate.header(APP_NAME, CommonParamManager.getAppName());
            // redis 获取用户信息
            requestTemplate.header(USER_INFO, JsonUtil.convertObjectToJson(null));
            log.info("Fegin中封装了用户信息，uri:{}", requestTemplate.url());

            //TODO 待定
//            String resourceName = getRequest().getHeader(RESOURCE_NAME);
//            if (StringUtils.isNotBlank(resourceName)) {
//                try {
//                    requestTemplate.header(RESOURCE_NAME, URLEncoder.encode(resourceName, "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }

//            String traceId = (String) Optional.ofNullable(getRequest().getAttribute(TRACE_ID)).orElse("");
//            if (StringUtils.isBlank(traceId)) {
//                traceId = UUID.randomUUID().toString().replaceAll("-", "");
//                Span span = getArmsSpan();
//                if (span != null && StringUtils.isNotBlank(span.getTraceId())) {
//                    traceId = traceId + "," + span.getTraceId();
//                }
//            }
//            requestTemplate.header(TRACE_ID, traceId);
        };

        log.info("Fegin基础拦截器注册成功。");
        return interceptor;
    }

    private Span getArmsSpan() {
        Span span = Tracer.builder().getSpan();
        return span;
    }

}
