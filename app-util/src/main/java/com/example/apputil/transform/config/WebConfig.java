package com.example.apputil.transform.config;

import com.example.apputil.transform.core.TransformConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public TransformConverter converter() {
        return new TransformConverter();
    }

}
