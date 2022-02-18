package com.example.appcommon.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member/centerDataApi")
public class CenterDataApiController {

    @PostMapping("/getDataCenterId")
    public String getDataCenterId() {
        return "32";
    }

}
