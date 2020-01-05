package com.xxun.watch.xunchatroom.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import android.util.Log;
/**
 * Created by huangyouyang on 2016/12/12.
 */

public class HttpNetUtils {
private static final String TAG="watch update";
    public static String httpPostJson(String postData, String postUrl, boolean needTimeout) {

        StringBuilder sData = new StringBuilder();
        HttpURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //发送POST请求
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();

            // check for new app/watch version, set timeout - 3s
            if (needTimeout) {
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;

            Log.i(TAG,"post json string length:" + ll + "bytes length" + lngth+":"+postData);
            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(sData.toString());
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG,"connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());

                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    static public String httpPostJsonWithKey(String postData, String postUrl, boolean needTimeout, InputStream certificate) {

        SSLContext sslContext = null;
        try {

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(certificate, "123456".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringBuilder sData = new StringBuilder();
        HttpsURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //发送POST请求
            URL url = new URL(postUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            if (needTimeout) {
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;


            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(sData.toString());
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG,"connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());
                Log.i(TAG,"responseJson = " + responseJson);
 
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }


  
 static public String PostJsonWithURLConnection(String postData, String postUrl, boolean checkForUpdate, InputStream certificate) {

        SSLContext sslContext = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(certificate, "123456".toCharArray());

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sData = new StringBuilder();
        HttpsURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //·¢ËÍPOSTÇëÇó
            URL url = new URL(postUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());

            // check for new app/watch version, set timeout - 3s
            if (checkForUpdate) {
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;

            Log.i(TAG,"post json string length:" + ll + "bytes length" + lngth + "  sData:" + sData+" url:"+postUrl);
            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(sData.toString());
            out.flush();
            out.close();

            //»ñÈ¡ÏìÓŠ×ŽÌ¬
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG,"connect failed!");

            } else {
                //»ñÈ¡ÏìÓŠÄÚÈÝÌå
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());
                Log.i(TAG,"responseJson = " + responseJson);
                result = responseJson;    //»ØžŽœá¹û
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
}
