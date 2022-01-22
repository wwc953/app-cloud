package com.example.appuser.controller;

import com.example.appuser.model.User;
import com.example.appuser.remote.IOrderServiceFeign;
import com.example.appuser.service.UserServiceImpl;
import com.netflix.client.ClientException;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
public class UserController {

    @Autowired
    IOrderServiceFeign orderServiceFeign;

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/local")
    public String callLocal() {
        log.info("=============== user ===============");
        return "local user";
    }

    @ApiOperation(value = "feign远程服务调用order", notes = "remote call test")
    @GetMapping("/user/{param}")
    public String callOrder(@PathVariable String param) {
        return orderServiceFeign.callOrder(param);
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/cOrder/{id}")
    public String getOrder(@PathVariable(value = "id") Integer id) {
        return orderServiceFeign.getOrderFromOrder(id);
    }

    @ApiOperation(value = "远程调用文件上传测试")
    @RequestMapping(value = "/orderupload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        return orderServiceFeign.uploadFile(file, "user-app");
    }

    @ApiOperation(value = "远程调用文件下载测试")
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> download(@RequestParam String fileName) {
        log.info("使用feign调用服务 文件下载");
        ResponseEntity<byte[]> result = null;
        InputStream inputStream = null;
        try {
            // feign文件下载
            Response response = orderServiceFeign.download(fileName);
            Response.Body body = response.body();
            inputStream = body.asInputStream();

            //TODO inputStream.available()获取的长度与文件实际大小不一致，导致文件下载异常
            // https://blog.csdn.net/zyxwvuuvwxyz/article/details/78549923
//            byte[] bytes = new byte[inputStream.available()];
//            inputStream.read(bytes);

            HttpHeaders heads = new HttpHeaders();
            heads.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            heads.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            byte[] bytes = IOUtils.toByteArray(inputStream);
            result = new ResponseEntity<byte[]>(bytes, heads, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @ApiOperation(value = "远程调用文件下载测试22")
    @RequestMapping(value = "/download2", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download2(@RequestParam String fileName, HttpServletResponse response) throws ClientException {
        log.info("使用feign调用服务 文件下载");
        Response feignResponse = orderServiceFeign.download(fileName);
        try {
            Response.Body body = feignResponse.body();
            InputStream inputStream = body.asInputStream();
            ServletOutputStream out = response.getOutputStream();
            response.setHeader("content-disposition", "attachment;fileName=" + fileName);
            out.write(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            throw new ClientException("get credit-order graphics fail :{} ", e);
        }

    }
}
