package com.example.appgateway.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户认证处理器: userInfo放请求头中，但需注意http请求头的限制
 */
@Slf4j
//@Component
//@RefreshScope
public class UserAuthFilter implements GlobalFilter, Ordered {

    @Value("${request.filter.member:(.*/member/.*)}")
    private String member;

    @Value("${open.token.filter:false}")
    private boolean openTokenFilter;

    @Autowired
    Jedis redisServer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("========= 用户认证处理器 START =========");
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().getRawPath();
        String method = request.getMethodValue().toUpperCase();
        String upgrade = request.getHeaders().getUpgrade();

        if (!isMember(requestUri)) {
            throw new RuntimeException("非法路径!!!");
        }
        String token = request.getHeaders().getFirst("token");

        log.info("========= 是否开启token校验: {} =========", openTokenFilter);
        if (StringUtils.isBlank(token)) {
            if (openTokenFilter) {
                throw new RuntimeException("是否校验失败!!!");
            } else {
                exchange = setUserInfoHeaderData(exchange, "");
                return chain.filter(exchange);
            }
        }

        String newPath = "";
        if ("websocket".equalsIgnoreCase(upgrade)) {
            int len = requestUri.lastIndexOf("/");
            newPath = requestUri.substring(0, len);
            token = requestUri.substring(len + 1, requestUri.length());
        }

        //TODO 是否校验token
        String userinfoStr = redisServer.get("coc:token:" + token);
        //将用户信息保存上下文中，方便其他过滤器获取使用(如：response数据脱敏)
        exchange.getAttributes().put("userObj", userinfoStr);

        if (StringUtils.isNotBlank(userinfoStr)) {
            exchange = setUserInfoHeaderData(exchange, userinfoStr);
        }

        if ("websocket".equalsIgnoreCase(upgrade)) {
            JSONObject userObj = JSONObject.parseObject(userinfoStr);
            String loginName = userObj.getString("loginName");
            newPath += "/" + loginName;
            log.info("====== 拼接后ws请求路径: {} ======", newPath);
            ServerHttpRequest httpRequest = request.mutate().path(newPath).build();
            return chain.filter(exchange.mutate().request(httpRequest).build());
        }
        log.info("========= 用户认证处理器 END =========");
        return chain.filter(exchange);
    }

    /**
     * 是否是授权访问路径
     *
     * @param uri
     * @return
     */
    private boolean isMember(String uri) {
        Pattern r = Pattern.compile(member);
        Matcher matcher = r.matcher(uri);
        return matcher.matches();
    }

    /**
     * @param exchange
     * @param content  用户信息字符串
     * @return
     */
    public ServerWebExchange setUserInfoHeaderData(ServerWebExchange exchange, String content) {
        ServerHttpRequest headers = exchange.getRequest().mutate().headers(httpHeaders -> {
            httpHeaders.set("userInfo", content);
        }).build();
        ServerWebExchange newExchange = exchange.mutate().request(headers).build();
        return newExchange;
    }


    @Override
    public int getOrder() {
        return -8;
    }
}
