package com.example.apputil.appcache;

import com.example.appstaticutil.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Slf4j
//@Aspect
//@Component
public class AppCacheAspect {

    private static final String packageDir = "exexution(* com.example.xxx..*.*(..))";

    static Pattern pattern = Pattern.compile("#\\{(.*?)\\}");

    @Around(value = packageDir)
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        AppCache appCache = methodSignature.getMethod().getAnnotation(AppCache.class);
        Object result = null;
        if (!ObjectUtils.isEmpty(appCache)) {
            //第一个入参
            Map<String, Object> params = JsonUtil.convertJsonToMap(JsonUtil.convertObjectToJson(point.getArgs()[0]));

            StringBuffer sb = new StringBuffer();
            Matcher matcher = pattern.matcher(appCache.key());
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(sb, String.valueOf(params.get(group)));
            }
            String redisKey = sb.toString();

            //存在 去缓存

            //不存在，执行接口，放缓存
            Object proceed = point.proceed();

        }
        return result;
    }

}
