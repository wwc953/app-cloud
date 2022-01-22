package com.example.apporder.controller;

import com.example.apporder.model.Order;
import com.example.apporder.service.OrderServiceimpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class OrderController {

    @Autowired
    OrderServiceimpl orderServiceimpl;

    @GetMapping("/order/{param}")
    public String callOrder(@PathVariable String param) {
        log.info("------ user --> order ----");
        return "我来自：order" + param;
    }

    @GetMapping("/getOrder/{id}")
    public Order getOrder(@PathVariable Integer id){
        log.info("order id = {}", id);
        return orderServiceimpl.getOrderId(id);
    }

    @PostMapping("/insert")
    public Integer insertOrder(@RequestBody Order order){
        Integer insertOrder = orderServiceimpl.insertOrder(order);
        return insertOrder;
    }

}
