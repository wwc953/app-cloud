package com.example.appgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * @Description: 防重请求
 * @author: wangwc
 * @date: 2021/1/12 15:30
 */
//@RefreshScope
//@Component
@Slf4j
public class HeavyRequestFilter implements GlobalFilter, Ordered {

    @Autowired
    Jedis redisServer;

    /**
     * 重复请求时间间隔
     */
    @Value("${app.heavyRequestFilter.time:0}")
    String timeOut;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("========== 防重请求过滤start =========");
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().getRawPath();
        String token = request.getHeaders().getFirst("auth_token");
        String host = request.getRemoteAddress().getHostName();
        String path = request.getPath().value();

        String key = String.format("%s:%s:%s", token, host, path);

        int intTimeOut = Integer.parseInt(timeOut);
        if (intTimeOut == 0 || StringUtils.isEmpty(token)) {
            log.info("========== 不用防重请求判断 =========");
            return chain.filter(exchange);
        }

        if (intTimeOut > 0 || redisServer.get(key) != null) {
            throw new RuntimeException("用户重复请求,uri=" + requestUri);
        } else {
            redisServer.set(key, UUID.randomUUID().toString());
            redisServer.expire(key, intTimeOut);
            log.info("=================>key的值：{}", key);
        }
        log.info("========== 防重请求过滤end =========");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -10;
    }

}
