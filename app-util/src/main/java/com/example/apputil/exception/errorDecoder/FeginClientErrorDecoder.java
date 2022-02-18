package com.example.apputil.exception.errorDecoder;

import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@Configuration
public class FeginClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 405 && response.status() <= 499) {
            try {
                String errorContent = Util.toString(response.body().asReader());
                return new RuntimeException(errorContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new RuntimeException("接口为找到，请检查URL是否正确。方法:" + methodKey);
        } else {
            log.error("非业务异常，调用Fallback.方法名: " + methodKey);
            return FeignException.errorStatus(methodKey, response);
        }
    }

}
