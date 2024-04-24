package com.flappygo.flappyim.Tools.Secret;

import android.util.Base64;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.security.*;


/******
 * RSA工具类
 */
public class RSATool {


    /*******
     * 公钥加密数据
     * @param data         数据
     * @param publicKeyStr 公钥加密数据
     */
    public static String encryptWithPublicKey(String publicKeyStr, String data) throws Exception {
        //要加密的原始数据
        byte[] originalBytes = data.getBytes();

        //移除PEM字符串中的首尾标识符和换行符
        publicKeyStr = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        //对字符串进行Base64解码
        byte[] publicKeyBytes = android.util.Base64.decode(publicKeyStr, android.util.Base64.DEFAULT);

        //生成X509EncodedKeySpec对象
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

        //获取KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //从KeySpec对象生成PublicKey
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        //使用公钥加密
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //开始加密
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //获取数组
        byte[] encryptedBytes = encryptCipher.doFinal(originalBytes);

        //返回
        return android.util.Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
    }


    /******
     * 通过私钥解密
     * @param data             数据
     * @param privateKeyStr    字符串
     */
    public static String decryptWithPrivatePKCS8(String privateKeyStr, String data) throws Exception {

        // 移除PEM字符串中的首尾标识符和换行符
        privateKeyStr = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        //对字符串进行Base64解码
        byte[] privateKeyBytes = android.util.Base64.decode(privateKeyStr, android.util.Base64.DEFAULT);

        //生成PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        //获取KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //从KeySpec对象生成PrivateKey
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        //使用私钥解密
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //初始化
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        //解密字符串
        byte[] decryptedBytes = decryptCipher.doFinal(android.util.Base64.decode(data, android.util.Base64.DEFAULT));

        //解密字符串
        return new String(decryptedBytes);
    }


}
