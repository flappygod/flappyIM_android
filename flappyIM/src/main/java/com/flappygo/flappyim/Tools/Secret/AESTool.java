package com.flappygo.flappyim.Tools.Secret;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import com.flappygo.flappyim.Tools.StringTool;

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
        //key byte
        byte[] raw = sKey.getBytes("utf-8");
        //aes
        SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
        //"AES/CBC/PKCS5Padding"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //Encrypt
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, zeroIv);
        //Encrypt
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        //base64
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
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
        byte[] raw = sKey.getBytes("utf-8");
        //aes
        SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
        //"AES/CBC/PKCS5Padding"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //set
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, zeroIv);
        //base 64
        byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);
        //original
        byte[] original = cipher.doFinal(encrypted1);
        //set string
        return new String(original, "utf-8");
    }

    //Encrypt ECB
    public static String EncryptECB(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("Key is null");
        }
        if (sKey.length() != 16 && sKey.length() != 32) {
            throw new RuntimeException("Key length must be 16 or 32");
        }
        //key byte
        byte[] raw = sKey.getBytes("utf-8");
        //AES
        SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
        //"AES/ECB/PKCS5Padding"
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //Encrypt
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        //Encrypt
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        //base64
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    //Decrypt ECB
    public static String DecryptECB(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("Key is null");
        }
        if (sKey.length() != 16 && sKey.length() != 32) {
            throw new RuntimeException("Key length must be 16 or 32");
        }
        //key byte
        byte[] raw = sKey.getBytes("utf-8");
        //AES
        SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
        //AES/ECB/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        //Decrypt
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        //base64
        byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);
        //Decrypt
        byte[] original = cipher.doFinal(encrypted1);
        //ret string
        return new String(original, "utf-8");
    }

    //encrypt ecb no throw exception
    public static String EncryptECBNoThrow(String sSrc, String sKey) {
        try {
            if(StringTool.isEmpty(sSrc)){
                return sSrc;
            }
            return EncryptECB(sSrc, sKey);
        } catch (Exception ex) {
            return sSrc;
        }
    }

    //decrypt ecb no throw exception
    public static String DecryptECBNoThrow(String sSrc, String sKey) {
        try {
            if(StringTool.isEmpty(sSrc)){
                return sSrc;
            }
            return DecryptECB(sSrc, sKey);
        } catch (Exception ex) {
            return sSrc;
        }
    }
}
