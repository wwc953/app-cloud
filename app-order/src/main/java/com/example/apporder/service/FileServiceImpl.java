package com.example.apporder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class FileServiceImpl {
    @Value("${server.port:1010}")
    String serverport;

    public static final String FILE_BASE_PATH = "/Users/wangwc/IdeaProjects/uploadfiles" + File.separator;

    public String upload(MultipartFile file, String custID) throws IOException {
        InputStream in = file.getInputStream();
//        System.out.println(DigestUtils.md5Hex(in));
//        System.out.println(org.springframework.util.DigestUtils.md5DigestAsHex(in));

        String originalFilename = file.getOriginalFilename();
        // 校验文件类型
//        String[] splitName = originalFilename.split("\\.");
//        String fName = splitName[splitName.length - 1];
//        FileType fileType = FileTypeJudge.getType(in);
//        if (fileType == null) {
//            return "未知文件类型!!!";
//        }
//        String fileTypeJudgeName = fileType.name();
//        log.info("文件名称类型：{},文件流类型:{}", fName, fileTypeJudgeName);
//        if (!StringUtils.equalsIgnoreCase(fileTypeJudgeName, fName)) {
//            return "文件类型错误";
//        }

        File savefile = new File(FILE_BASE_PATH + originalFilename);
        log.info(savefile.getPath());
        file.transferTo(savefile);
        return "OK";
    }


}
