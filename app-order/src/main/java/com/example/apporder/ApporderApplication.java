package com.example.apporder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableFeignClients
@EnableDiscoveryClient
@EnableSwagger2
@MapperScan("com.example.apporder.dao")
@SpringBootApplication(scanBasePackages = {"com.example.apporder"})
public class ApporderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApporderApplication.class, args);
    }

}
