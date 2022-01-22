package com.example.appuser.remote;

import com.example.appuser.remote.impl.WebsocketFeignImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "app-websocket", fallback = WebsocketFeignImpl.class)
public interface IWebsocketFeign {

    @RequestMapping(value = "/redis/sendWebMsg", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String sendWebMsg(@RequestBody Map map);

}
