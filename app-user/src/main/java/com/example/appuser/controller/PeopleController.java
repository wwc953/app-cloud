package com.example.appuser.controller;

import com.example.appuser.service.PeopleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/people")
public class PeopleController {

    @Resource
    PeopleServiceImpl peopleService;


    @GetMapping("/batchCreate")
    public void batchCreate() {
        peopleService.batchCreate();
    }

}
