package com.example.apporder.service;

import com.example.apporder.dao.OrderMapper;
import com.example.apporder.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceimpl {

    @Autowired
    OrderMapper orderMapper;

    public Order getOrderId(Integer id){
        Order order = orderMapper.selectByPrimaryKey(id);
        return order != null ? order : new Order();
    }

    public Integer insertOrder(Order order){
        if (order == null){
            log.error("order 不可为空！！");
        }

        int result = orderMapper.insert(order);
        if (result < 0){
            log.info("添加order失败！！！");
        }
        return result;
    }

}
