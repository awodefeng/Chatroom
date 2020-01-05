package com.xxun.watch.xunchatroom.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
//import org.json.JSONObject;
//import org.json.JSONException;
import android.os.AsyncTask;
//import json.JSONValue;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.xiaoxun.smart.uploadfile.AESUtil;
import com.xiaoxun.smart.uploadfile.BASE64Encoder;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import java.net.URLDecoder;

public class DownloadManagerUtil {
    private static Context mContext;
    private XiaoXunNetworkManager mNetService;//=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
    String mEID = null;
    String mGID = null;
    String mToken = null;
    String mAES_KEY = null;
    String TAG="DownloadManagerUtil";
    private static DownloadManagerUtil downloadManagerUtil;

    public synchronized static DownloadManagerUtil getInstance(Context context) {
	 mContext = context;
        if (downloadManagerUtil == null)
            downloadManagerUtil = new DownloadManagerUtil(context);
        return downloadManagerUtil;
    }

    public DownloadManagerUtil(Context context) {
        mContext = context;
	mNetService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");

        mGID = mNetService.getWatchGid();
        mEID = mNetService.getWatchEid();
        mToken = mNetService.getSID();
        mAES_KEY = mNetService.getAESKey();
  
        Log.i(TAG, ">> mGID " + mGID);
        Log.i(TAG, ">> mEID " + mEID);
        Log.i(TAG, ">> mToken " + mToken);
        Log.i(TAG, ">> mAES_KEY " + mAES_KEY);
    }
//    //简单的下载功能
//    public long download(String url, String title, String desc) {
//        Uri uri = Uri.parse(url);
//        DownloadManager.Request req = new DownloadManager.Request(uri);
//        //设置WIFI下进行更新
//        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//        //下载中和下载完后都显示通知栏
//        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        //使用系统默认的下载路径 此处为应用内 /android/data/packages ,所以兼容7.0
//        req.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, title);
//        //通知栏标题
//        req.setTitle(title);
//        //通知栏描述信息
//        req.setDescription(desc);
//        //设置类型为.apk
//        req.setMimeType("application/vnd.android.package-archive");
//        //获取下载任务ID
//        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        return dm.enqueue(req);
//    }


    /**
     * 比较实用的升级版下载功能
     *
     * @param url   下载地址
     * @param title 文件名字
     * @param desc  文件路径
     */
    public long download(String url, String title, String desc) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        long ID;

        //以下两行代码可以让下载的apk文件被直接安装而不用使用Fileprovider,系统7.0或者以上才启动。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder localBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(localBuilder.build());
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 仅允许在WIFI连接情况下下载
//        request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        // 通知栏中将出现的内容
        request.setTitle(title);
        request.setDescription(desc);

        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }

        //制定下载的文件类型为APK
        request.setMimeType("application/vnd.android.package-archive");

        // 下载过程和下载完成后通知栏有通知消息。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 指定下载文件地址，使用这个指定地址可不需要WRITE_EXTERNAL_STORAGE权限。
        request.setDestinationUri(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/"+"123.mp4")));

        //大于11版本手机允许扫描
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //表示允许MediaScanner扫描到这个文件，默认不允许。
            request.allowScanningByMediaScanner();
        }


        ID = downloadManager.enqueue(request);
        return ID;
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public void clearCurrentTask(long downloadId) {
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

public void downloadNoticeVideo(Context context,final String eid, final String key, final OnDownloadListener listener,String dirPath,String fileName) {
	Log.i(TAG, "downloadNoticeVideo");
    mEID=eid;
        final JSONObject obj = new JSONObject();
	//try{
        obj.put("key", key);
        obj.put("sid", mToken);
	//}catch(JSONException ex){
	// ex.printStackTrace();
	//}
	Log.i(TAG, "obj=" + obj.toString());
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
			Log.i(TAG, "doInBackground");
                try {
                    String encryptData = BASE64Encoder.encode(AESUtil.encryptAESCBC(obj.toString(), mAES_KEY , mAES_KEY ));
                    String result =HttpNetUtils.PostJsonWithURLConnection(encryptData + mToken,"https://fdsfile.xunkids.com/download", false, context.getApplicationContext().getAssets().open("dxclient_t.bks")); 
		    Log.i(TAG, "result=" + result);
                    if (result != null && result.length() > 0) {
                        String out = new String(AESUtil.decryptAESCBC(org.java_websocket.util.Base64.decode(result), mAES_KEY ,mAES_KEY ));
		        Log.i(TAG, "out=" + out);
                        JSONObject json = (JSONObject) JSONValue.parse(out);
                        //net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) json.get("files");
                        Object code = json.get("code");
			String urlOld =(String)json.get("url");
			 Log.i(TAG, "urlOld=" + urlOld);
			URLDecoder ud=new URLDecoder();
			String url=ud.decode(urlOld,"UTF-8");
			Log.i(TAG, "url=" + url);
			Log.i(TAG, "code=" + code);
                        if ((code != null && ((Integer) code) != 0) || url == null) {
                            return null;
                        }
                        
			//url=jurl.toString();
                       /* for (int i = 0; i < array.size(); i++) {
                            JSONObject file = (JSONObject) array.get(i);
                            String tempKey = (String) file.get("key");
                            if (tempKey.equals(key)) {
                                url = (String) file.get("url");
                                break;
                            }
                        }*/
                        if (url != null && url.length() > 0) {
                            HttpURLConnection conn = null;
                            try {
                                URL target = new URL(url);
                                conn = (HttpURLConnection) target.openConnection();
                                conn.setConnectTimeout(10 * 1000);
                                conn.setReadTimeout(30 * 1000);
                                conn.setDoInput(true);

                                if (conn.getResponseCode() == 200) {
                                    InputStream is = conn.getInputStream();
                                    File fp = new File(dirPath, fileName);
                                    OutputStream fout = new FileOutputStream(fp);
                                    int len = 0;
                                    byte[] buf = new byte[1024];
                                    while ((len = is.read(buf)) != -1) {
                                        fout.write(buf, 0, len);
                                    }
                                    is.close();
                                    fout.close();
				    Log.i(TAG, "savePath=" + fp.getAbsolutePath());
                                    AESUtil.decryptFile(fp.getAbsolutePath(), mEID.substring(0, 16), mEID.substring(0, 16));
                                    return fp.getAbsolutePath();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
		Log.i(TAG, "onPostExecute,s=" + s);
                if (listener != null) {
                    if (s != null && s.length() > 0) {
                        listener.onSuccess(s);
                    } else {
                        listener.onFail();
                    }
                }
            }
        }.execute();
    }

}
