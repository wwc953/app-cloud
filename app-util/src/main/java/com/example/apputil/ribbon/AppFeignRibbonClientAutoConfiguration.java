package com.example.apputil.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import feign.Feign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;
import org.springframework.cloud.openfeign.ribbon.RetryableFeignLoadBalancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnClass({ILoadBalancer.class, Feign.class})
//AppFeignRibbonClientAutoConfiguration 会在FeignAutoConfiguration之前加载
@AutoConfigureBefore({FeignAutoConfiguration.class})
public class AppFeignRibbonClientAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingClass({"org.springframework.retry.support.RetryTemplate"})
    public CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory(SpringClientFactory factory) {
        return new CachingSpringLoadBalancerFactory(factory) {
            private volatile Map<String, FeignLoadBalancer> cache = new ConcurrentReferenceHashMap();

            @Override
            public FeignLoadBalancer create(String clientName) {
                FeignLoadBalancer clientx = cache.get(clientName);
                if (clientx != null) {
                    return clientx;
                } else {
                    log.info("生成自定义的FeignLoadBalancer");
                    IClientConfig config = factory.getClientConfig(clientName);
                    ILoadBalancer lb = factory.getLoadBalancer(clientName);
                    ServerIntrospector serverIntrospector = factory.getInstance(clientName, ServerIntrospector.class);
                    FeignLoadBalancer client = loadBalancedRetryFactory != null ?
                            new RetryableFeignLoadBalancer(lb, config, serverIntrospector, loadBalancedRetryFactory) :
                            new AppFeignLoadBalancer(lb, config, serverIntrospector);
                    cache.put(clientName, client);
                    return client;
                }

            }
        };
    }

}
