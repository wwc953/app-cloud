package com.example.appuser.remote.impl;

import com.example.appuser.remote.IOrderServiceFeign;
import feign.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class OrderServiceFeignImpl implements IOrderServiceFeign {
    @Override
    public String callOrder(String param) {
        return "远程调用失败！！！";
    }

    @Override
    public String getOrderFromOrder(Integer id) {
        return "远程调用失败！！！";
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String custId) {
        return "远程调用失败!";
    }

    @Override
    public feign.Response download(String fileName) {
        return Response.builder()
                .status(HttpStatus.OK.value())
                .headers(new HashMap<String, Collection<String>>() {{
                    put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
                }})
                .body("远程调用失败", Charset.forName("UTF-8"))
                .build();
    }
}
