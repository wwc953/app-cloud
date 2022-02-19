package com.example.apputil.cmccache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AutoCacheRunner implements ApplicationRunner {

    private List<String> types;

    @Value("${frame.cmccahe.use.type:centerdataId,numberstrategy,rediskeymanager,user,org}")
    public void setTypes(String type) {
        try {
            type = type.replace("_", "").toUpperCase();
            String[] typeArr = type.split(",");
            this.types = Arrays.asList(typeArr);
        } catch (Exception e) {
            this.types = new ArrayList<>();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("该应用使用的公共缓存类型:{}", types);
        CommonParamManager.doListenerInitialize(types);
    }
}
