package com.example.appuser.commandline;

import com.example.appuser.ons.api.IConsumerService;
import com.example.appuser.ons.api.MessageHandle;
import com.example.appuser.ons.bean.ConsumerInfoEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class UserConsumer implements CommandLineRunner {

    @Value("${mq.userGroupId}")
    private String userGroupId;
    @Value("${mq.userTopic}")
    private String userTopic;

    @Autowired
    IConsumerService iConsumerService;

    @Override
    public void run(String... args) throws Exception {
        ConsumerInfoEntry infoEntry = new ConsumerInfoEntry();
        infoEntry.setGroupId(userGroupId);
        infoEntry.setTopic(userTopic);

        iConsumerService.consumerMsg(infoEntry, new MessageHandle() {
            @Override
            public boolean handle(String msgKey, String msg) {
                System.out.println("msgKey:" + msgKey);
                System.out.println("msg:" + msg);
                return true;
            }
        });
    }
}
