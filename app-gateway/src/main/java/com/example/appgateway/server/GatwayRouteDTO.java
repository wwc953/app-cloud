package com.example.appgateway.server;

import lombok.Data;

@Data
public class GatwayRouteDTO {
    /**
     * 网关标识 主键
     */
    private String gatewayId;
    /**
     * 路由id
     */
    private String routeId;
    /**
     * 路由路径
     */
    private String routePath;
    /**
     * 服务Id
     */
    private String serverId;
    /**
     * 服务名称 中文
     */
    private String serverNameCn;
    /**
     * 服务名称 英文
     */
    private String serverNameEn;
    /**
     * 路由地址
     */
    private String routeUrl;

    /**
     * 去除前缀
     */
    private String stripprefix;

    /**
     * 是否开启重试
     */
    private String retryable;
    /**
     * 0正常 1删除
     */
    private String isDelete;
    /**
     * 是否置换token
     */
    private String isConvertToken;

}
