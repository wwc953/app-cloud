package com.example.appuser.controller;

import com.example.appuser.remote.IOrderServiceFeign;
import com.netflix.client.ClientException;
import feign.Response;
import feign.Util;
import io.swagger.annotations.Api;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Api(tags = "远程上传、下载文件")
@RequestMapping("/file")
@RestController
public class FileController {

    @Autowired
    IOrderServiceFeign orderServiceFeign;


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
            IOUtils.closeQuietly(inputStream);
        }
        return result;
    }

    @ApiOperation(value = "远程调用文件下载测试22")
    @RequestMapping(value = "/download2", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download2(@RequestParam String fileName, HttpServletResponse response) throws ClientException {
        log.info("使用feign调用服务 文件下载");
        Response feignResponse = orderServiceFeign.download(fileName);
        ServletOutputStream out = null;
        try {
            Response.Body body = feignResponse.body();
            out = response.getOutputStream();

            response.setHeader("content-disposition", "attachment;fileName=" + fileName);

            //补全响应头信息
//            Map<String, Collection<String>> headers = feignResponse.headers();
//            Iterator<String> iterator = headers.keySet().iterator();
//            while (iterator.hasNext()) {
//                String field = iterator.next();
//                Iterator<String> it = Util.valuesOrEmpty(headers, field).iterator();
//                while (it.hasNext()) {
//                    String value = it.next();
//                    response.setHeader(field, value);
//                }
//            }

            out.write(IOUtils.toByteArray(body.asInputStream()));
        } catch (Exception e) {
            throw new ClientException("get credit-order graphics fail :{} ", e);
        } finally {
            IOUtils.closeQuietly(out);
        }

    }
}
