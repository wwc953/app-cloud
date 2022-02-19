package com.example.appstaticutil.encry;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MD5Utils {
    public static String md5(String source) {
        try {

            byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
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
