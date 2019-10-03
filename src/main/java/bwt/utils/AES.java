package bwt.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import android.util.Base64;
import android.util.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES {
    private static final String IV_STRING = "16-Bytes--String";

    public static byte[] EncryptAES(byte[] iv, byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = null;
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.ENCRYPT_MODE,mSecretKeySpec,mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    //AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
    public static byte[] DecryptAES(byte[] iv,byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.DECRYPT_MODE,
                    mSecretKeySpec,
                    mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static String decryptAES(StringBuffer content, String md5TimeStamp, String sitePath)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        // base64 解码
//        Base64.Decoder decoder = Base64.getDecoder();
//        byte[] encryptedBytes = decoder.decode(content);
        byte[] encryptedBytes = Base64.decode(content.toString(), Base64.DEFAULT);

        byte[] enCodeFormat = calKeyOrIV(md5TimeStamp, sitePath, true).getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");

        byte[] initParam = calKeyOrIV(md5TimeStamp, sitePath, false).getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] result = cipher.doFinal(encryptedBytes);

        return new String(result, "UTF-8");
    }

    /*
    * md5TimeStamp: Phase1回傳下來在最後面的32碼的MD5。
    * sitePath: 如"B713"
    * isKey: true的話回傳AES的key；false的話回傳AES的IV。
    *
Key: (密鑰)
1. timestamp -> MD5 -> 最後倒數第四位開始往前取8碼 (630D7BC7DA5100AEEFB444C432C6D7AC)
2. 站點編號 -> 反轉 + appxjava -> MD5 -> 前面第六位開始取8碼 (9cf8d9d5762a4e1607d554c5e645e89b)
3. 4C432C6D 交叉混合 9d5762a4 = 49Cd453726C26aD4 (取一個Timestamp的再取一個站點編號的 再取一個Timestamp的再一個站點編號的…依此類推)

IV:(偏移量)
前兩點與key相同，僅第三點混合的方式是相反的 (取一個站點編號的再取一個Timestamp的 再取一個站點編號的再一個Timestamp的…依此類推)
3. 9d5762a4 交叉混合 4C432C6D = 94dC5473622Ca64D
    * */
    private static String calKeyOrIV(String md5TimeStamp, String sitePath, boolean isKey) {
        String str1 = md5TimeStamp.substring(21, 29);
        String str2 = new StringBuilder(sitePath).reverse().toString();
        str2 += "appxjava";

        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(str2.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        str2 = hex.toString().substring(5, 13);

        StringBuilder sb1 = new StringBuilder(str1);
        StringBuilder sb2 = new StringBuilder(str2);
        StringBuilder result = new StringBuilder(16);
        for (int i = 0; i < 8; i++) {
            if (isKey) {
                result.append(sb1.charAt(i));
                result.append(sb2.charAt(i));
            } else {
                result.append(sb2.charAt(i));
                result.append(sb1.charAt(i));
            }
        }

        Log.i(isKey? "AES's key = " : "AES's IV = ", result.toString());
        return result.toString();
    }
}
