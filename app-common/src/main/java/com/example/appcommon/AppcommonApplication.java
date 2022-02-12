package com.example.appcommon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@EnableSwagger2
@MapperScan(basePackages = {"com.example.appcommon.dao", "com.example.apputil.redis.dao"})
@SpringBootApplication(scanBasePackages = {"com.example.appcommon", "com.example.apputil.*"})
public class AppcommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppcommonApplication.class, args);
    }

}
