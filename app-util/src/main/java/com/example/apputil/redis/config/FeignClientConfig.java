package com.example.apputil.redis.config;

import com.example.apputil.redis.remote.SignerFeign;
import com.example.apputil.redis.remote.SignerFeignFallBack;
import feign.Client;
import feign.Contract;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.hystrix.HystrixFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({FeignClientsConfiguration.class})
public class FeignClientConfig {

    @Autowired
    Client feignClient;

    @Value("${frame.signer.application.name:xxx-xx-signer-subdomain}")
    private String signerName;

    @Autowired
    @Qualifier("baseInterceptor")
    private RequestInterceptor baseInterceptor;

    @Bean
    public SignerFeign signerFeign(Contract contract, Decoder decoder, Encoder encoder, ErrorDecoder errorDecoder) {
        return HystrixFeign.builder().contract(new SpringMvcContract())
                .encoder(encoder)
                .client(this.feignClient)
                .errorDecoder(errorDecoder)
                .retryer(Retryer.NEVER_RETRY)
                .requestInterceptor(baseInterceptor)
                .target(SignerFeign.class, "http://" + this.signerName + "/", new SignerFeignFallBack());
    }
}
