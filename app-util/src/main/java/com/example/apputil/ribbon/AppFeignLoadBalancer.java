package com.example.apputil.ribbon;


import com.netflix.client.ClientException;
import com.netflix.client.RequestSpecificRetryHandler;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.ribbon.RibbonProperties;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;

import java.net.SocketTimeoutException;

@Slf4j
public class AppFeignLoadBalancer extends FeignLoadBalancer {

    RibbonProperties ribbon;

    public AppFeignLoadBalancer(ILoadBalancer lb, IClientConfig clientConfig, ServerIntrospector serverIntrospector) {
        super(lb, clientConfig, serverIntrospector);
        ribbon = RibbonProperties.from(clientConfig);
    }

    @Override
    public RequestSpecificRetryHandler getRequestSpecificRetryHandler(RibbonRequest request, IClientConfig requestConfig) {
        return new RequestSpecificRetryHandler(true, ribbon.isOkToRetryOnAllOperations(), getRetryHandler(), requestConfig) {
            @Override
            public boolean isRetriableException(Throwable e, boolean sameServer) {

                if ((!(e instanceof ClientException) || ((ClientException) e).getErrorType() != ClientException.ErrorType.READ_TIMEOUT_EXCEPTION) &&
                        (!((!(e instanceof SocketTimeoutException)) || "connect timed out".equals(e.getMessage())))) {
                    return super.isRetriableException(e, sameServer);
                } else {
                    log.info("read time out，不执行重试");
                    return false;
                }

            }
        };
    }

}
