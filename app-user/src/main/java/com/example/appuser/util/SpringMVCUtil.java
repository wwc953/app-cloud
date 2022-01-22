package com.example.appuser.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class SpringMVCUtil {

    public HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) Optional.ofNullable(RequestContextHolder.getRequestAttributes()).orElse(null);
        return sra == null ? null : sra.getRequest();
    }

}
