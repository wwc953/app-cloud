package com.example.apporder.util;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTypeJudge {
    /**
     * Constructor
     */

    private FileTypeJudge() {
    }

    /**
     * 将文件头转换成16进制字符串 * * @param 原生byte * @return 16进制字符串
     */

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();

        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();

    }

    /**
     * 得到文件头 * * @param filePath * 文件路径 * @return 文件头 * @throws IOException
     */
    private static String getFileContent(String filePath) throws IOException {
        byte[] b = new byte[28];

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(filePath);

            inputStream.read(b, 0, 28);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }

        return bytesToHexString(b);
    }

    /**
     * 判断文件类型 * * @param filePath * 文件路径 * @return 文件类型
     */
    public static FileType getType(String filePath) throws IOException {
        String fileHead = getFileContent(filePath);

        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();

        FileType[] fileTypes = FileType.values();

        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }
        return null;
    }

    public static FileType getType(InputStream in) throws IOException {
        byte[] b = new byte[28];
        in.read(b, 0, 28);
        String fileHead = bytesToHexString(b);
        System.out.println("fileHead: " + fileHead);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();

        FileType[] fileTypes = FileType.values();

        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }
        return null;
    }

    public static void main(String args[]) throws Exception {
        String fileName = "/Users/wangwc/IdeaProjects/uploadfiles/营销2.0物理数据模型.zip";
        System.out.println(FileTypeJudge.getType(fileName).name());
        String[] splitName = fileName.split("\\.");
        System.out.println(StringUtils.equalsIgnoreCase(FileTypeJudge.getType(fileName).name(), splitName[splitName.length - 1]));

    }
}
