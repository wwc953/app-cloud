//package com.example.apporder.commandline;
//
//import com.example.apputil.ons.api.IConsumerService;
//import com.example.apputil.ons.api.MessageHandle;
//import com.example.apputil.ons.bean.ConsumerInfoEntry;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//@Component
//@Order(1)
//@Slf4j
//public class UserConsumer implements CommandLineRunner {
//
//    @Value("${mq.userGroupId}")
//    private String userGroupId;
//    @Value("${mq.userTopic}")
//    private String userTopic;
//
//    @Autowired
//    IConsumerService iConsumerService;
//
//    @Override
//    public void run(String... args) throws Exception {
//        ConsumerInfoEntry infoEntry = new ConsumerInfoEntry();
//        infoEntry.setGroupId(userGroupId);
//        infoEntry.setTopic(userTopic);
//
//        iConsumerService.consumerMsg(infoEntry, new MessageHandle() {
//            @Override
//            public boolean handle(String msgKey, String msg) {
//                log.info("msgKey:{}", msgKey);
//                log.info("msg:{}", msg);
//                return true;
//            }
//        });
//    }
//}
