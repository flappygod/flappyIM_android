package com.flappygo.flappyim.Tools;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

/******
 * Aes加密工具
 */
public class AESTool {

    //Encrypt CBC
    public static String EncryptCBC(String sSrc, String sKey, String iv) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("Key is null");
        }
        if (sKey.length() != 16 && sKey.length() != 32) {
            throw new RuntimeException("Key length must be 16 or 32");
        }
        //set iv
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        //keybyte
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        //aes
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //"AES/CBC/PKCS5Padding"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //Encrypt
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, zeroIv);
        //Encrypt
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        //base64
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    //Decrypt CBC
    public static String DecryptCBC(String sSrc, String sKey, String iv) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("Key is null");
        }
        if (sKey.length() != 16 && sKey.length() != 32) {
            throw new RuntimeException("Key length must be 16 or 32");
        }
        //set iv
        IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
        //bytes
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        //aes
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //"AES/CBC/PKCS5Padding"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //set
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, zeroIv);
        //base 64
        byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
        //original
        byte[] original = cipher.doFinal(encrypted1);
        //set string
        return new String(original, StandardCharsets.UTF_8);
    }

    //Encrypt ECB
    public static String EncryptECB(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("Key is null");
        }
        if (sKey.length() != 16 && sKey.length() != 32) {
            throw new RuntimeException("Key length must be 16 or 32");
        }
        //keybyte
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        //AES
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //"AES/ECB/PKCS5Padding"
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //Encrypt
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        //Encrypt
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        //base64
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    //Decrypt ECB
    public static String DecryptECB(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("Key is null");
        }
        if (sKey.length() != 16 && sKey.length() != 32) {
            throw new RuntimeException("Key length must be 16 or 32");
        }
        //keybyte
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        //AES
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //AES/ECB/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //Decrypt
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        //base64
        byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
        //Decrypt
        byte[] original = cipher.doFinal(encrypted1);
        //ret string
        return new String(original, StandardCharsets.UTF_8);
    }


}
