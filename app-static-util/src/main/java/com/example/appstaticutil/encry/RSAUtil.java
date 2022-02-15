package com.example.appstaticutil.encry;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：SHA256withRSA 工具类
 * 说明：
 *
 * @author wangweichun
 * @date 2021-9-28 11:25
 */
public class RSAUtil {

    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);

    // MAX_DECRYPT_BLOCK应等于密钥长度/8（1byte=8bit），所以当密钥位数为2048时，最大解密长度应为256.
    // 128 对应 1024，256对应2048
    private static final int KEYSIZE = 2048;
    // RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    // 不仅可以使用DSA算法，同样也可以使用RSA算法做数字签名
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    // 默认种子
    public static final String DEFAULT_SEED = "$%^*%^()(ED47d784sde78";
    // 编码格式
    private static final String CODE_FORMATE_UTF8 = "UTF-8";

    // - - - - - - - - - - - - - - - - - - - - RSA 生成秘钥对 - - - - - - - - - - - - - - - - - - - - //
    public static String DEFAUT_PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkieMb0EntYotTUNt9ZyusU4mh3GW9k4lL4q42aNDS6aVN//55EnM45CrUC5qjW2zfEqrFLNPGwONueYGmDoVySXmS4Uy0enhnJSWqLRlLhiIr3oVHzPUZQ1v/cGultMSepCVsQmJ5z+LhcYt7UfVAh8dEpMg81Rthtf1V2RgtyAXe3JJjl8b24VG2lwkD8oJCp2fJpwOnhMbKPe/t5XEgUb++XAw5CZxN3fMRyuMz3+3GMeUrQOfatXpnX4RxUPY0tt3X7Y249ICbOG4QdVyG89wdATilECh/Y3DTyg61k8DDfs2zEUwPPURx0XbBAUNaiVHJcTSkKdxb7GgDIXi1wIDAQAB";
    public static String DEFAUT_PrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCSJ4xvQSe1ii1NQ231nK6xTiaHcZb2TiUvirjZo0NLppU3//nkSczjkKtQLmqNbbN8SqsUs08bA4255gaYOhXJJeZLhTLR6eGclJaotGUuGIivehUfM9RlDW/9wa6W0xJ6kJWxCYnnP4uFxi3tR9UCHx0SkyDzVG2G1/VXZGC3IBd7ckmOXxvbhUbaXCQPygkKnZ8mnA6eExso97+3lcSBRv75cDDkJnE3d8xHK4zPf7cYx5StA59q1emdfhHFQ9jS23dftjbj0gJs4bhB1XIbz3B0BOKUQKH9jcNPKDrWTwMN+zbMRTA89RHHRdsEBQ1qJUclxNKQp3FvsaAMheLXAgMBAAECggEAcpQSon2f1dBXp4S/RZfp1BKCXhiM0td8i4LjW6uEbGBSBy7kBzZcK+MUiq141xszZLNc38OatoDYsO5GKT0QKztNCdOIfcwvFQPgAK47zCFrtrWsrWOClR4tt/AJITCkAfYrhDkIJYRJqzwscSKpORqPPiqd4GVF4DqZmkOVFroc4gJ2DhU5kkwZoUx40MgPtelds/Hf3EAXAGY7nPcOuGRcH8PofN/PoRJaXcLteiovUm3C+JSyaEx63ataFJdj6EnLjHx8vzbf1HHFq6KQYIFBu/SaVBQpiUHOLJz84MXr9BmBYVOOH0nFKTdoCOlGjR84RPog6SqA/JKF124IYQKBgQDiZQ1JkkUm+YSo4WXB6y5gPZ/SZraxOSPzwFKBGfuE2I9VJwgFnERf0WIunkLkkYgcRJiTzatxxLSysjWTMclARrdaUXCMoiFqeO9a4/gpaI+zTmWxrVCrmWDh0PHBIiCQoc3aEaXTZ2e7QSKTG7zyf0SGSb7zGbPLNKBISmDm/wKBgQClRFFylWl4y+5H/AVzTpuOqWjjsXpq6BvArGnpKq3An5qNT3ZEzuHbLs6EDuhhyBQoeSyxxpUKsgs8MKQWVrkGvvS1iMQeiKo6/94GXLKAriN51bzldgHsBTBXdfc1OR2DH9hj5mAvXjeu+0SMiYO7BNa9v9GqO7XsVOZ/60McKQKBgC+hSvZbi6jzffF52kq+C/e7f1Bow67tcp7pq3H14XDFjwI/P/TON6RhDOL58ODdrWElHWSbNKKu28VssNDS/KBFQHYEBEXSJm2cG99nbuJnWTREHikPaZVwo21e5D3ZzDghkol6hQTBEJXY0klY4Ju0ItqN0Vqb4bOy0rXi0BhJAoGANWzqb4JBgF1cgETGdkoZG3tEfybmjhzOq/CQpGG5naV6zue7Me83MCXneOVg1gtaCONzQV0S3UxFOYX35YjPc5DThsPZso03iq8gVSouQk8JBN/FmuAVKvlacUufSMNX+QWYbGMTAP0SPyzXMh9aSGD+EoUqAEAhRW8EvKGPJlECgYEAnvAVwqZnFXCwSGvDVdGlppbRT9WMNtaeEb32XU7fQstU7dtyYXoaw2nEC0lq5UoCS33ylt74GJX5/z7QPP+hbX2SwCzY7GdmpQCivAIo7JP6+VElKn3bqBxxGbJ2346V4SqOf843y8Fsxc12MffsGQMKx/pNw3tJ3Sn6OR7BgvQ=";

    /**
     * 生成密钥对：Base64 转码的字符串
     *
     * @param
     * @return
     */
    public static Map<String, String> initKeyBase64Str() throws Exception {
        Map<String, String> map = new HashMap<>(2);
        Map<String, Key> keyMap = initKey();
        PublicKey publicKey = (PublicKey) keyMap.get("PublicKey");
        PrivateKey privateKey = (PrivateKey) keyMap.get("PrivateKey");
        map.put("PublicKey", new String(Base64.encodeBase64(publicKey.getEncoded())));
        map.put("PrivateKey", new String(Base64.encodeBase64(privateKey.getEncoded())));
        logger.info("生成密钥 = " + JSON.toJSONString(map));
        return map;
    }

    /**
     * 生成默认密钥
     *
     * @return 密钥对象
     */
    public static Map<String, Key> initKey() throws Exception {
        return initKey(DEFAULT_SEED);
    }

    /**
     * 生成密钥对：若seed为null，那么结果是随机的；若seed不为null且固定，那么结果也是固定的；
     *
     * @param seed 种子
     * @return 密钥对象
     */
    public static Map<String, Key> initKey(String seed) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);

        // 如果指定seed，那么secureRandom结果是一样的，所以生成的公私钥也永远不会变
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(seed.getBytes());
        // Modulus size must range from 512 to 1024 and be a multiple of 64
        keygen.initialize(KEYSIZE, secureRandom);

        // 生成一个密钥对，保存在keyPair中
        KeyPair keys = keygen.genKeyPair();
        PublicKey publicKey = keys.getPublic();
        PrivateKey privateKey = keys.getPrivate();

        // 将公钥和私钥保存到Map
        Map<String, Key> map = new HashMap<>(2);
        map.put("PublicKey", publicKey);
        map.put("PrivateKey", privateKey);
        logger.info("生成密钥 = " + JSON.toJSONString(map));
        return map;
    }

    // - - - - - - - - - - - - - - - - - - - - RSA 加密、解密 - - - - - - - - - - - - - - - - - - - - //

    /**
     * 获取公钥 PublicKey 信息
     *
     * @param
     * @return
     */
    public static PublicKey getPublicKey(String pubKeyStr) throws Exception {
        byte[] publicKeys = Base64.decodeBase64(pubKeyStr);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeys);
        KeyFactory mykeyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = mykeyFactory.generatePublic(publicKeySpec);
        logger.info("传入的公钥为：【" + pubKeyStr + "】，转义后的公钥为：【" + publicKey + "】");
        return publicKey;
    }

    /**
     * 公钥加密，指定 RSA 方式的 PublicKey 对象
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        // base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(CODE_FORMATE_UTF8)));
        return outStr;
    }

    /**
     * 公钥加密，任意 PublicKey 对象
     *
     * @param publicKey
     * @param encryptData
     * @param encode
     */
    public static String encrypt(PublicKey publicKey, String encryptData, String encode) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空，请设置。");
        }
        try {
            final Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(encryptData.getBytes(encode));
            return Base64.encodeBase64String(output);
        } catch (Exception e) {
            logger.info("加密异常:" + e.getMessage());
            return null;
        }
    }

    /**
     * 私钥解密，指定 RSA 方式的 PrivateKey 对象
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(CODE_FORMATE_UTF8));
        // base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * BASE64Encoder 加密
     *
     * @param data 要加密的数据
     * @return 加密后的字符串
     */
    private static String encryptBASE64(byte[] data) {
        return new String(Base64.encodeBase64(data));
    }

    /**
     * BASE64Encoder 解密
     *
     * @param data 要解密的数据
     * @return 解密后的字节
     */
    private static byte[] decryptBASE64(String data) {
        return Base64.decodeBase64(data);
    }

    // - - - - - - - - - - - - - - - - - - - - SIGN 签名，验签 - - - - - - - - - - - - - - - - - - - - //

    /**
     * 加签：生成报文签名
     *
     * @param content    报文内容
     * @param privateKey 私钥
     * @param encode     编码
     * @return
     */
    public static String doSign(String content, String privateKey, String encode) {
        try {
            String unsign = Base64.encodeBase64String(content.getBytes(StandardCharsets.UTF_8));
            byte[] privateKeys = Base64.decodeBase64(privateKey.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeys);
            KeyFactory mykeyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey psbcPrivateKey = mykeyFactory.generatePrivate(privateKeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(psbcPrivateKey);
            signature.update(unsign.getBytes(encode));
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception e) {
            logger.error("生成报文签名出现异常");
        }
        return null;
    }

    /**
     * 验证：验证签名信息
     *
     * @param content   签名报文
     * @param signed    签名信息
     * @param publicKey 公钥
     * @param encode    编码格式
     * @return
     */
    public static boolean doCheck(String content, String signed, PublicKey publicKey, String encode) {
        try {
            // 解密之前先把content明文，进行base64转码
            String unsigned = Base64.encodeBase64String(content.getBytes(encode));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(unsigned.getBytes(encode));
            boolean bverify = signature.verify(Base64.decodeBase64(signed));
            return bverify;
        } catch (Exception e) {
            logger.error("报文验证签名出现异常");
        }
        return false;
    }

    public static boolean doCheck(String content, String signed, String publicKey, String encode) {
        try {
            return doCheck(content, signed, getPublicKey(publicKey), encode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("报文验证签名出现异常");
        }
        return false;
    }

//    public static void main(String[] args) throws Exception {
////        Map<String, String> stringStringMap = initKeyBase64Str();
//        String word = "第三方个梵蒂冈";
////        String key =encrypt(word,  DEFAUT_PublicKey);
////        System.out.println(key);
////        String key1 = decrypt(key,  DEFAUT_PrivateKey);
////        System.out.println(key1);
//
////        //验签
////        String sign = doSign(word, DEFAUT_PrivateKey, "utf-8");
////        System.out.println("加签:" + sign);
////        //验签
////        boolean b = doCheck(word, sign, DEFAUT_PublicKey, "utf-8");
////        System.out.println("验签结果：" + b);
//    }

}