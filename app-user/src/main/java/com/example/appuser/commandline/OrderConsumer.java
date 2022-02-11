package com.example.appuser.commandline;

import com.example.appuser.ons.api.IConsumerService;
import com.example.appuser.ons.api.MessageHandle;
import com.example.appuser.ons.bean.ConsumerInfoEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class OrderConsumer implements CommandLineRunner {

    @Value("${mq.orderGroupId}")
    private String orderGroupId;
    @Value("${mq.orderTopic}")
    private String orderTopic;

    @Autowired
    IConsumerService iConsumerService;

    @Override
    public void run(String... args) throws Exception {
        ConsumerInfoEntry infoEntry = new ConsumerInfoEntry();
        infoEntry.setGroupId(orderGroupId);
        infoEntry.setTopic(orderTopic);

        iConsumerService.consumerMsg(infoEntry, new MessageHandle() {
            @Override
            public boolean handle(String msgKey, String msg) {
                log.info("order----->msgKey:{}", msgKey);
                log.info("order---->msg:{}", msg);
                return true;
            }
        });
    }
}
