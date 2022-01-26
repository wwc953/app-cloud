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

    @Value("${file.uploaddir:/Users/wangwc/IdeaProjects/uploadfiles/}")
    String fileBasePath;

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

        File savefile = new File(fileBasePath);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        savefile = new File(fileBasePath + originalFilename);
        log.info(savefile.getPath());
        file.transferTo(savefile);
        return "OK";
    }


}
