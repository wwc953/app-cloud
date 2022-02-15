package com.example.appuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@EnableFeignClients
@EnableSwagger2
//@MapperScan(basePackages={"com.example.appuser.dao","com.example.apputil.redis.dao"})
@MapperScan(basePackages={"com.example.appuser.dao"})
@SpringBootApplication(scanBasePackages = {"com.example.appuser", "com.example.apputil.*"})
public class AppuserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppuserApplication.class, args);
    }

}
