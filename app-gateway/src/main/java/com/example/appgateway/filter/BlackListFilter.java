package com.example.appgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 黑名单过滤
 * @author: wangwc
 * @date: 2021/1/12 20:21
 */
@RefreshScope
@Component
@Slf4j
public class BlackListFilter implements GlobalFilter, Ordered {

    @Value("${appgetway.load.cache.level:1}")
    public int cacheLeave;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //request 获取 ip
        String ip = "127.0.0.1";

        // 判断ip是否在黑名单中
        List<String> blackList = new ArrayList<>();
        if(blackList.contains(ip)){
            return null;
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -9;
    }
}
