package com.zack.intelligent.face;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.Utils;
import com.zkteco.android.biometric.ZKLiveFaceService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 人脸识别
 */

public class FaceManager  {
    private static final String TAG = "FaceManager";

    private static FaceManager instance;
    public static long context;

    private FaceManager(){}

    public static FaceManager getInstance() {
       if(instance == null){
          instance =new FaceManager();
       }
        return instance;
    }


    private Context mContext;
    public void init(Context context){
        this.mContext =context;
        getHardwareId();
        setParameter();
    }

    private void getHardwareId() {
        byte[] hwid = new byte[256];
        int[] size = new int[1];
        size[0] = 256;
        if (0 == ZKLiveFaceService.getHardwareId(hwid, size)) {
           String hwidStr = new String(hwid, 0, size[0]);
            Log.i(TAG, "getHardwareId 硬件码:" + hwidStr);

            byte[] version = new byte[256];
            int[] _size = new int[1];
            _size[0] = 256;
            if (0 == ZKLiveFaceService.version(version, _size)) {
                final String verStr = new String(version, 0, _size[0]);
                Log.i(TAG, "getHardwareId 算法版本: "+verStr);
            }
        } else {
            getLastError(0);
        }
    }

    private void setParameter() {
        InputStream is = mContext.getResources().openRawResource(R.raw.zkliveface);
//        InputStream is = mContext.getResources().openRawResource(R.raw.zkliveface2);
        byte[] buf = new byte[0];
        int length = 0;
        try {
            length = is.available();
            buf = new byte[length];
            int read = is.read(buf);
            Log.i("setParameter", "setParameter : " + new String(buf, "utf-8") + " read:" + read);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int retCode = 0;
        retCode = ZKLiveFaceService.setParameter(0, 1012, buf, length);
        Log.i("setParameter", "setParameter code: " + retCode);
        if (retCode == 0) {
            Log.i(TAG, "setParameter 设置参数成功: ");
            getInit();
        } else {
            Log.i(TAG, "setParameter 设置参数失败: ");
            ToastUtil.showShort("设置参数失败");
            if (retCode == 11) {
                getLastError(0);
            }
        }
    }

    private void getInit() {
        long[] retContext = new long[1];
        int ret = ZKLiveFaceService.init(retContext);
        Log.i("getInt", "getInit ret: " + ret +" retContext[0] == "+retContext[0]);
        if (ret == 0) {
            context = retContext[0];
            Log.i(TAG, "getInit context: "+context);
            String strValue = "528";
            //设置参数
            ret = ZKLiveFaceService.setParameter(context, 2001, strValue.getBytes(), strValue.length());
            Log.i("getInt", "getInit  ret: " + ret);
            if (ret == 0) {

            } else {

            }
            strValue = "3";
            //设置参数2
            ret = ZKLiveFaceService.setParameter(context, 1008, strValue.getBytes(), strValue.length());
            Log.i("getInt", "getInit ret2: " + ret);
            if (ret == 0) {

            } else {

            }
            Constants.isFaceInit =true;
        } else {
            Constants.isFaceInit =false;
            ToastUtil.showShort("初始化算法失败，请检查许可文件");
            if (ret == 11) {
                getLastError(0);
            }
        }
    }

    private void getLastError(long context) {
        byte[] lasterror = new byte[256];
        int[] size = new int[1];
        size[0] = 256;
        //获取最近一次的错误信息
        int ret = ZKLiveFaceService.getLastError(context, lasterror, size);
        if (ret == 0) { //I need to make more money
            String errStr = new String(lasterror, 0, size[0]);
            ToastUtil.showShort("" + errStr);
//            sendHandle(-1, errStr);
        }
    }


}
