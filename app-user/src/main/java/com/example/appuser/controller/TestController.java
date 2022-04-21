package com.example.appuser.controller;

import com.example.appuser.model.User;
import com.example.appuser.remote.IOrderServiceFeign;
import com.example.appuser.service.UserServiceImpl;
import com.netflix.client.ClientException;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
public class TestController {

    @Autowired
    IOrderServiceFeign orderServiceFeign;

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/local")
    public String callLocal() {
        log.info("=============== user ===============");
        return "local user";
    }

    @ApiOperation(value = "feign远程服务调用order", notes = "remote call test")
    @GetMapping("/user/{param}")
    public String callOrder(@PathVariable String param) {
        return orderServiceFeign.callOrder(param);
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/cOrder/{id}")
    public String getOrder(@PathVariable(value = "id") Integer id) {
        return orderServiceFeign.getOrderFromOrder(id);
    }

}
