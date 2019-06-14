package com.zack.intelligent.ups;

import android.text.TextUtils;
import android.util.Log;

import com.zack.intelligent.Constants;
import com.zack.intelligent.event.EventConsts;
import com.zack.intelligent.event.MessageEvent;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.winplus.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2018/4/26.
 */

public class Ups {

    private static Ups instance;
    private SerialPort serialPort;
    private InputStream is;
    private OutputStream os;
//    private String path = "/dev/ttysWK3";
    private String path = "/dev/ttyS4";
    private int baudrate = 2400;
    private boolean isStop;
    private ReadThread mReadThread;

    private Ups() {}

    public static Ups getInstance() {
        if(instance ==null){
            instance =new Ups();
        }
        return instance;
    }

    public void init() {
//        if(Constants.IS_NEW_BOARD){ //新板子
//            path ="ttyS4";
//        }else{//旧版子
//            path ="/dev/ttysWK3";
//        }
        try {
            if (serialPort == null) {
                serialPort = new SerialPort(new File(path), baudrate, 0);
                is = serialPort.getInputStream();
                os = serialPort.getOutputStream();

                isStop = false;
                mReadThread = new ReadThread();
                mReadThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] buf){
        if(os !=null){
            try {
                os.write(buf);
            } catch (IOException e) {
                Log.w("UPS", "sendData error: " +e.getMessage());
//                e.printStackTrace();
            }
        }
    }

    /**
     * 发送查询参数指令
     *  28  起始字节（
     *  32 32 38 2E 39  228.9V 输入电压
     *  20 空格
     *  32 32 39 2E 34  229.4V 输入电压异常转电池逆变输出时的输入电压值
     *  20
     *  32 32 39 2E 34  229.4V输出电压
     *  20
     *  30 30 30  00.0%  输出功率百分比
     *  20
     *  34 39 2E 39  49.9HZ 输出电压的频率
     *  20
     *  32 34 2E 32  24.2V  电池电压
     *  20
     *  32 35 2E 30  25.0℃  电池温度
     *  20
     *  30 （30市电正常 31市电异常）
     *  30 （30电池正常 31电池低压）
     *  30  （30市电电压正常范围输出205V-253V    31市电AVR模式输出（变压器工作状态））
     *  30  （30 UPS没有故障 31 UPS发生故障）
     *  31  默认
     *  30  默认
     *  30  （30 输出开关关闭 31输出开关打开 ）
     *  31  默认
     *  0D  结束符
     *
     */
    public void queryParam(){
        byte[] b ={0x51, 0x31, 0x0d};
        sendData(b);
    }

    /**
     * 电发送池百分比查询指令
     */
    public void queryPercent(){
        byte[] b ={0x51, 0x32, 0x0d};
        sendData(b);
    }

    /**
     * 发送关机和重启命令
     */
    public void shutdownAndReboot(){
        byte[] b ={0x53, 0x2e, 0x38, 0x52, 0x30, 0x30, 0x31, 0x35, 0x0d};
        sendData(b);
    }

    /**
     * 发送取消命令
     */

    public void cancel(){
        byte[] b ={0x43, 0x0d};
        sendData(b);
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            Log.i("Ups", "run ReadThread: ");
            while (!isStop && !isInterrupted()) {
                int size;
                try {
                    if (is == null) {
                        return;
                    }
                    Thread.sleep(250);
                    int nCount = is.available();
                    if (nCount == 0) {
                        continue;
                    }
//                    Log.i(TAG, "run  nCount: "+nCount);
                    byte[] buffer = new byte[nCount];
                    size = is.read(buffer);
//                    Log.i(TAG, "run size: "+size);
                    if (size > 0) {
                        String hexString = TransformUtil.BinaryToHexString(buffer);
                        Log.i("Ups", "run buffer: " + hexString);
                        onParamReceive.onParamData(hexString);
                        if(!TextUtils.isEmpty(hexString)){
                            String[] params = hexString.split(" ");
                            if(params.length == 47){ //接收到参数
                                char c38 = (char)  Integer.parseInt(params[38], 16);
                                if(c38 =='1'){//市电异常
//                                    Log.i("Ups", "onParamData 市电异常: ");
                                    EventBus.getDefault().post(new MessageEvent(EventConsts.EVENT_POWER_ABNORMAL));
                                }else if(c38 =='0'){//市电正常
//                                    Log.i("Ups", "onParamData 市电正常: ");
                                    EventBus.getDefault().post(new MessageEvent(EventConsts.EVENT_POWER_NORMAL));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public interface OnParamReceive{
        void onParamData(String data);
    }

    private OnParamReceive onParamReceive;

    public void setOnParamReceive(OnParamReceive onParamReceive) {
        this.onParamReceive = onParamReceive;
    }

    public void closeSerial(){
        if (this.mReadThread != null && !this.mReadThread.isInterrupted()) {
            this.mReadThread.interrupt();
            mReadThread =null;
        }
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }
}
