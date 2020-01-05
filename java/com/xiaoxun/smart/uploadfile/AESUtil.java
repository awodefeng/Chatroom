package com.xiaoxun.smart.uploadfile;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by guxiaolong on 2015/12/14.
 */
public class AESUtil {

    private static String Algorithm = "AES";
    private static String privKey = "";
    public static AESUtil instance = null;
    static String TAG="AESUtil";
    public static AESUtil getInstance(){
        if (instance ==null){
            instance = new AESUtil();
        }
        return  instance;
    }
    public void init(String key){
        privKey = key;
    }

    public static String encryptDataStr(String inputtext){
        if (inputtext != null && inputtext.length() > 0) {
            try {
                //byte[] rawKey = getRawKey(privKey.getBytes());
                byte[] result = encrypt(inputtext.getBytes());
                return Base64.encodeToString(result, Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
                return inputtext;
            }
        } else {
            return inputtext;
        }
    }

    public static String decryptDataStr(String encrypted) {
        if (encrypted != null && encrypted.length() > 0) {
            try {
                //byte[] rawKey = getRawKey(privKey.getBytes());
                byte[] enc = Base64.decode(encrypted, Base64.NO_WRAP);
                byte[] result = decrypt(enc);
                return new String(result);
            } catch (Exception e) {
                e.printStackTrace();
                return encrypted;
            }
        } else {
            return encrypted;
        }
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(Algorithm);
        SecureRandom sr = null;
        if (Build.VERSION.SDK_INT >= 24) {
            return seed;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);
        kgen.init(256, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    public static byte[] encrypt(byte[] input) throws Exception {
        try {
            byte[] raw = getRawKey(privKey.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(raw, Algorithm);
            Cipher cipher = Cipher.getInstance(Algorithm);

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(input);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
    }

    public static void encryptFile(File file) {
        Log.i(TAG,"encryptFile");
        Log.i(TAG,"path="+file.getPath());
        try {
            FileInputStream fileInputStream = new FileInputStream(file.getPath());
            int length = fileInputStream.available();
            Log.i(TAG,"length="+length);
            byte[] input = new byte[length];
            fileInputStream.read(input);
            fileInputStream.close();
            byte[] encrypted = encrypt(input);
            FileOutputStream outputStream = new FileOutputStream(file.getPath());
            outputStream.write(encrypted);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encryptFile(String filePath, String sKey, String ivParameter) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            int length = fileInputStream.available();
            byte[] input = new byte[length];
            fileInputStream.read(input);
            fileInputStream.close();
            byte[] encrypted = encryptAESCBC(input, sKey, ivParameter);
	    Log.i(TAG,"aes test start");
	    //decryptAESCBC(encrypted,sKey,ivParameter);
	    Log.i(TAG,"aes test end");
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptAESCBC(byte[] sSrc, String sKey, String ivParameter){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc);
            return encrypted;
        }catch (Exception e){
            return null;
        }
    }

    public static byte[] decrypt(byte[] encrypted) throws Exception {
        try {
            byte[] raw = getRawKey(privKey.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(raw, Algorithm);
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return encrypted;
        }
    }

    // AES CBC 加密
    public static byte[] encryptAESCBC(String sSrc,String sKey, String ivParameter){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return encrypted;
        }catch (Exception e){
            return null;
        }
    }

    //AES CBC 解密
    public static byte[] decryptAESCBC(byte [] encrypted, String sKey, String ivParameter){
	Log.i(TAG,"decryptAESCBC");
        try {
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(encrypted);
	    if(original==null){
		Log.i(TAG,"decryptAESCBC,original is null");
		}
            return original;
        } catch (Exception ex) {
	    Log.i(TAG,"decryptAESCBC,Exception");
            return null;
        }
    }

    /**
     *
     * @param data  :需要加密的数据
     * @return 返回加密之后的数据
     * 还函数目前对于加密数据为0~9和a~z字符使用，其他特殊字符为验证:理论是可用的，使用时待验证
     */
    public static String encryKaiSa(String data){
        StringBuilder encryData = new StringBuilder();
        for(int i = 0;i<data.length();i++){
            char a = data.charAt(i);
            if(a == '9'){
                char encryChar = (char)('0');
                encryData.append(encryChar);
            }else if(a == 'z'){
                char encryChar = (char)('a');
                encryData.append(encryChar);
            }else{
                char encryChar = (char)(a+1);
                encryData.append(encryChar);
            }
        }

        return encryData.toString();
    }

    public static String decryptKaiSa(String encryData){
        StringBuilder decryptData = new StringBuilder();
        for(int i = 0;i<encryData.length();i++){
            char a = encryData.charAt(i);
            if(a == '0'){
                char encryChar = (char)('9');
                decryptData.append(encryChar);
            }else if(a == 'a'){
                char encryChar = (char)('z');
                decryptData.append(encryChar);
            }else{
                char encryChar = (char)(a-1);
                decryptData.append(encryChar);
            }
        }

        return decryptData.toString();
    }

    public static String getCustomIdSign(String sectet,String timestamp,String parmsUrl){
        String parms = null;
        parms= "_app_secret="+sectet+"_timestamp="+timestamp+parmsUrl;
        return parms;
    }

    public static void decryptFile(String filePath, String sKey, String ivParameter) {
        Log.i(TAG,"decryptFile");
        Log.i(TAG,"filePath="+filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            int length = fileInputStream.available();
            Log.i(TAG,"length="+length);
            byte[] input = new byte[length];
            fileInputStream.read(input);
            fileInputStream.close();
            byte[] encrypted = decryptAESCBC(input, sKey, ivParameter);
            //Log.i(TAG,"encrypted.length="+encrypted.length);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(encrypted);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encryptBytes(byte[] bytes, String sKey, String ivParameter) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(bytes);
            return encrypted;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void testAES() {
        String masterPassword = "0123456789abcdef";
        String originalText = "0123456789";
        try {
            String encryptingCode = encryptDataStr(originalText);
            Log.i("AESUtil","encrypt result " + encryptingCode);
            String decryptingCode = decryptDataStr(encryptingCode);
            Log.i("AESUtil","decrypt result " + decryptingCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
/*
    public static String calcTransSign(String appSecret,String timeStamp){
        String sign = null;
        String urlencodedParams;
        String params = "#_app_secret=" + appSecret+"#"+"#_timestamp=" + timeStamp + "#";
        try {
            urlencodedParams = URLEncoder.encode(params,"UTF-8");
            sign = MD5.md5_string(urlencodedParams.toLowerCase());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sign;
    }

    public static String calcTransAllParmsSign(String mode,String httpUrl,String parms){
        String sign = null;
        String urlencodedParams;
        String params = mode+httpUrl+parms;
        try {
            LogUtil.e("allParms:"+params);
            urlencodedParams = URLEncoder.encode(params,"UTF-8");
            urlencodedParams = urlencodedParams.toLowerCase();
            sign = MD5.md5_string(urlencodedParams.toLowerCase());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sign;
    }
*/
}
