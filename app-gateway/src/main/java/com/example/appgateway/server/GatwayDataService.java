package com.example.appgateway.server;

import com.example.appgateway.util.DynamicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

//import org.springframework.data.redis.core.RedisTemplate;

@Component
public class GatwayDataService implements ApplicationEventPublisherAware {

    public static final String GATEWAY_ROUTES = "gateway:routes";

    ApplicationEventPublisher publisher;

    @Autowired
    RouteDefinitionWriter routeDefinitionWriter;

//    @Autowired(required = false)
//    RedisTemplate redisTemplate;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 从redis获取路由信息
     *
     * @return
     */
//    public List<GatwayRouteDTO> initGatwayRouteList() {
//        List<GatwayRouteDTO> gatwayRouteList = new ArrayList<>();
//        Map routeMap = redisTemplate.boundHashOps(GATEWAY_ROUTES).entries();
//        routeMap.forEach((k, v) -> {
//            gatwayRouteList.add(JSON.parseObject(v.toString(), GatwayRouteDTO.class));
//        });
//        return gatwayRouteList;
//    }
    public List<GatwayRouteDTO> testRouteDTOList() {
        List<GatwayRouteDTO> gatwayRouteList = new ArrayList<>();

        GatwayRouteDTO d1 = new GatwayRouteDTO();
        d1.setRouteId("app-user");
        d1.setRoutePath("/app-user/**");
        d1.setStripprefix("true");
        gatwayRouteList.add(d1);

        GatwayRouteDTO d2 = new GatwayRouteDTO();
        d2.setRouteId("app-order");
        d2.setRoutePath("/app-order/**");
        d2.setStripprefix("true");
        gatwayRouteList.add(d2);

        GatwayRouteDTO d3 = new GatwayRouteDTO();
        d3.setRouteId("app-websocket");
        d3.setRoutePath("/app-websocket/**");
        d3.setStripprefix("true");
        gatwayRouteList.add(d3);

        GatwayRouteDTO d4 = new GatwayRouteDTO();
        d4.setRouteId("app-redis-id");
        d4.setRoutePath("/app-redis-id/**");
        d4.setStripprefix("true");
        gatwayRouteList.add(d4);

        return gatwayRouteList;
    }

    /**
     * 加载路由
     */
    public void loadRouteData() {
        System.out.println("动态路由加载 ===> 开始...");
//        List<GatwayRouteDTO> gatwayRouteList = initGatwayRouteList();
        List<GatwayRouteDTO> gatwayRouteList = testRouteDTOList();

        gatwayRouteList.forEach(gatwayRoute -> {
            RouteDefinition routeDefinition = new RouteDefinition();
            URI uri = DynamicUtil.getUri("lb://" + gatwayRoute.getRouteId());
            routeDefinition.setId(gatwayRoute.getRouteId());
            routeDefinition.setUri(uri);
            routeDefinition.setOrder(0);

            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName("Path");
            Map<String, String> predicateMap = new HashMap<>();
            predicateMap.put("pattern", gatwayRoute.getRoutePath());
            predicate.setArgs(predicateMap);
            routeDefinition.setPredicates(Arrays.asList(predicate));

            FilterDefinition filter = new FilterDefinition();
            filter.setName("StripPrefix");
            String stripPrefixValue = "true".equals(gatwayRoute.getStripprefix()) ? "1" : "0";
            Map<String, String> filterMap = new HashMap<>();
            filterMap.put("_genkey_0", stripPrefixValue);
            filter.setArgs(filterMap);

//            FilterDefinition hysstrixFilter = new FilterDefinition();
//            hysstrixFilter.setName("Hystrix");
//            Map<String, String> hysstrixFilterMap = new HashMap<>();
//            hysstrixFilterMap.put("fallbackUri", "forward:/fallback");
//            hysstrixFilterMap.put("name", "default");
//            hysstrixFilter.setArgs(hysstrixFilterMap);
//            routeDefinition.setFilters(Arrays.asList(filter, hysstrixFilter));

            routeDefinition.setFilters(Arrays.asList(filter));

            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        });

        refreshRoute();
        System.out.println("动态路由加载 ===> 结束...");
    }


    public String deleteRouteById(String routeId) {
        try {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
            refreshRoute();
            return routeId + "删除成功";
        } catch (Exception e) {
            e.printStackTrace();
            return routeId + "删除失败";
        }
    }

    public String add(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "update route  fail";
        }
    }

    //更新/新增路由
    public String update(RouteDefinition definition) {
        try {
            deleteRouteById(definition.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add(definition);
    }

    private void refreshRoute() {
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }


    /**
     建表语句
     CREATE TABLE `get_way_routes_config` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `routesId` varchar(255) DEFAULT NULL,
     `uri` varchar(255) DEFAULT NULL,
     `predicates` varchar(255) DEFAULT NULL,
     `filters` varchar(255) DEFAULT NULL COMMENT '过滤条件',
     `alias` varchar(255) DEFAULT NULL COMMENT '访问别名',
     `serverName` varchar(255) DEFAULT NULL COMMENT '服务汉语名称',
     `isAddSwagger` int(1) DEFAULT '1' COMMENT '是否添加到swagger，0否，1是',
     PRIMARY KEY (`id`)
     ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='getway动态路由配置';
     */

}
