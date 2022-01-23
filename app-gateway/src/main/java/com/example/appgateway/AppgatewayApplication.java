package com.example.appgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AppgatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppgatewayApplication.class, args);
    }

}
