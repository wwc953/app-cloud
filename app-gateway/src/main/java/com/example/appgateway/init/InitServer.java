package com.example.appgateway.init;

import com.example.appgateway.server.GatwayDataService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitServer implements ApplicationListener<ApplicationStartedEvent> {
    @Resource
    GatwayDataService gatwayDataService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        gatwayDataService.loadRouteData();
    }
}
