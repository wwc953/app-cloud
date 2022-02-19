package com.example.apputil.cmccache.schedule;

import com.example.apputil.cmccache.CommonParamManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnExpression("${frame.cmccache.use:true}")
public class SyncCacheSchedule {

    private List<String> types;

    @Value("${frame.cmccahe.use.type:numberstrategy,rediskeymanager,user,org}")
    public void setTypes(String type) {
        try {
            type = type.replace("_", "").toUpperCase();
            String[] typeArr = type.split(",");
            this.types = Arrays.asList(typeArr);
        } catch (Exception e) {
            this.types = new ArrayList<>();
        }
    }


    @Scheduled(initialDelay = 2000L, fixedDelay = 900000L)
    private void task() {
        log.info("定时任务开始:");
        CommonParamManager.doAllTask(types);
    }

}
