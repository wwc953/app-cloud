package com.example.appgateway.controller;

import com.example.appgateway.server.GatwayDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/gateway")
public class DynamicRouteController {

    @Resource
    GatwayDataService gatwayDataService;

    @RequestMapping("/deleteRouteById")
    public String deleteRouteById(@RequestParam String routeId) {
        return gatwayDataService.deleteRouteById(routeId);
    }

}
