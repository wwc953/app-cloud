package com.example.apputil.utils;

import java.security.MessageDigest;

public class MD5Utils {
    public static String md5(String source) {
        try {

            byte[] bytes = source.getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] result = messageDigest.digest(bytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(String.format("%02X", result[i] & 255));
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
