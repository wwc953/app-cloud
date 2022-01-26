package com.example.apporder.controller;

import com.example.apporder.service.FileServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequestMapping("/file")
@RestController
@Api(tags = "文件上传")
public class UploadController {

    @Value("${file.uploaddir:/Users/wangwc/IdeaProjects/uploadfiles/}")
    String fileBasePath;

    @Resource
    FileServiceImpl fileService;

    @ApiOperation(value = "单文件上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam String custID) throws IOException {
        return fileService.upload(file, custID);
    }

    @ApiOperation(value = "单文件下载")
    @RequestMapping(value = "/download", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        InputStream in = null;
        //http
//        URL url = new URL(FileServiceImpl.FILE_BASE_PATH + fileName);
//        in = url.openConnection().getInputStream();

        in = new FileInputStream(new File(fileBasePath + fileName));

        byte[] b = new byte[1024];
        response.setHeader("content-disposition", "attachment;fileName=" + fileName);
        ServletOutputStream out = response.getOutputStream();

        int len = 0;
        while ((len = in.read(b)) > 0) {
            out.write(b, 0, len);
        }
        in.close();
    }

}
