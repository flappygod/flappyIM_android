package com.flappygo.flappyim.Tools.Secret;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import java.security.*;


/******
 * RSA工具类
 */
public class RSATool {


    /******
     * 生成公钥私钥
     */
    public static void generateKeyPair() throws Exception {
        // 初始化密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 获取公钥和私钥
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 将公钥和私钥转换为Base64编码的字符串
        String publicKeyString = android.util.Base64.encodeToString(publicKey.getEncoded(), android.util.Base64.DEFAULT);
        String privateKeyString = android.util.Base64.encodeToString(privateKey.getEncoded(), android.util.Base64.DEFAULT);

        // 打印Base64编码的公钥和私钥字符串
        System.out.println("公钥(Base64编码): " + publicKeyString);
        System.out.println("私钥(Base64编码): " + privateKeyString);

        // 如果需要，可以添加PEM格式的首尾标识符
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" + formatPEMString(publicKeyString) + "-----END PUBLIC KEY-----";
        String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" + formatPEMString(privateKeyString) + "-----END PRIVATE KEY-----";

        System.out.println("公钥(publicKeyPEM): " + publicKeyPEM + "\n");
        System.out.println("私钥(privateKeyPEM): " + privateKeyPEM + "\n");
    }


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
        Cipher encryptCipher = Cipher.getInstance("RSA");
        //开始加密
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //获取数组
        byte[] encryptedBytes = encryptCipher.doFinal(originalBytes);
        //返回
        return bytesToHex(encryptedBytes);
    }


    /******
     * 通过私钥解密
     * @param data             数据
     * @param privateKeyStr    字符串
     */
    public static String decryptWithPrivate(String privateKeyStr, String data) throws Exception {

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
        Cipher decryptCipher = Cipher.getInstance("RSA");

        //初始化
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        //解密字符串
        byte[] decryptedBytes = decryptCipher.doFinal(hexToBytes(data));

        //解密字符串
        return new String(decryptedBytes);
    }


    /******
     * 辅助方法，用于将字节数组转换为十六进制字符串
     * @param bytes 字节数组
     * @return 字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /******
     * 辅助方法，用于将十六进制字符串转换为字节数组
     * @param hexString 字符串
     * @return 字节数组
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("输入的十六进制字符串长度应该是偶数");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            int firstDigit = Character.digit(hexString.charAt(i), 16);
            int secondDigit = Character.digit(hexString.charAt(i + 1), 16);
            if (firstDigit == -1 || secondDigit == -1) {
                throw new IllegalArgumentException("输入的字符串包含非法字符");
            }
            bytes[i / 2] = (byte) ((firstDigit << 4) + secondDigit);
        }
        return bytes;
    }


    /******
     * 辅助方法，用于将Base64编码的字符串格式化为PEM格式
     * @param base64String Base64字符串
     * @return 格式化的字符串
     */
    private static String formatPEMString(String base64String) {
        // 按照64个字符一行的标准格式化Base64编码的字符串
        StringBuilder pemFormattedString = new StringBuilder();
        int index = 0;
        while (index < base64String.length()) {
            pemFormattedString.append(base64String, index, Math.min(index + 64, base64String.length())).append("\n");
            index += 64;
        }
        return pemFormattedString.toString();
    }

}
