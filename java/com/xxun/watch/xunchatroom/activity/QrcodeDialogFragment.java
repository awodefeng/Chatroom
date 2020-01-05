package com.xxun.watch.xunchatroom.activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.xxun.watch.xunchatroom.R;
import android.os.Environment;
import java.io.File;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import java.io.FileInputStream;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Base64;

/**
 * @author lihaizhou
 * @time 2017.12.13
 * @class describe 生成二维码界面
 */
public class QrcodeDialogFragment extends DialogFragment {

    private static final String TAG = "QrcodeDialogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.qrcode_fragment2, container, false);
        ImageView qrcodeImg = (ImageView) view.findViewById(R.id.qrcode_idle);
        String qrcodeContent =  xunReadQrcodeUrlFile();
	if(!"".equals(qrcodeContent)&&qrcodeContent.length()>=0) {
            Bitmap qrcodeBitmap = convertStringToIcon(qrcodeContent);
            qrcodeImg.setImageBitmap(qrcodeBitmap);
	}
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width = layoutParams.MATCH_PARENT;
        layoutParams.height = layoutParams.MATCH_PARENT;
        getDialog().getWindow().getDecorView().setPadding(0,0,0,0);
        getDialog().getWindow().setAttributes(layoutParams);
    }

    private String xunReadQrcodeUrlFile() {
        String readResult = "";
        try {
            //File file = new File(Environment.getExternalStorageDirectory(), "xun_qrcode.txt");
	    File file_dir = new File(Environment.getExternalStorageDirectory()+"/QRCode");  
	    if (!file_dir.exists()) {  
		file_dir.mkdirs();  
	    }  
            File file = new File(Environment.getExternalStorageDirectory()+"/QRCode","xun_qrcode.txt");

            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                readResult = new String(b);
                is.close();
                return readResult;
            } else {
                return readResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return readResult;
        }
    }

    private static Bitmap convertStringToIcon(String st) {
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            Bitmap bitmap =BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }
}
