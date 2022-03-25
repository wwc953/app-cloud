package com.example.appdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AppdataApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppdataApplication.class, args);
    }

}
