package com.example.apporder.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * 长传文件大小限制
 * @return
 */
@Configuration
public class MultipartFileConfig {
    
    @Bean
    public MultipartConfigElement multipartConfigFactory(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(100L));
        factory.setMaxRequestSize(DataSize.ofMegabytes(200L));
        return factory.createMultipartConfig();
    }
}
