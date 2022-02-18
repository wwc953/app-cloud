package com.example.apputil.sync.util;

import com.example.apputil.sync.ISyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CmcPublishUtil {
    private static ISyncService syncService;

    @Autowired
    public void setSyncService(ISyncService syncService) {
        CmcPublishUtil.syncService = syncService;
    }

    private static String UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void NostrategyPublish() {
        syncService.publish("CMC_ST_NO_DATA_ID", "CMC_PUBLISH", UUID());
    }

}
