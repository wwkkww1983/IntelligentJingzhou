package com.zack.intelligent.serial;

import android.os.Build;
import android.util.Log;

import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;

import org.winplus.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;


/**
 * 串口工具类
 */

public class SerialPortUtil {

    private String TAG = SerialPortUtil.class.getSimpleName();
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    //    private String path = "/dev/ttySAC3"; //友坚
    private String path = "/dev/ttyS3";  //瑞芯微
//    private String path = "/dev/ttyS4";  //瑞芯微s
//    private String path = "/dev/ttyAMA3";  //九鼎
//    private String path = "/dev/ttyS3";  //九鼎
    private int baudrate = 9600;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;

    public interface OnDataReceiveListener {
        void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    private SerialPortUtil() {
        onCreate();
    }

    public static SerialPortUtil getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPortUtil();
        }
        return portUtil;
    }

    /**
     * 初始化串口信息
     */
    public void onCreate() {
        String device = Build.DEVICE;
        if (device.equals("x4418")) { //九鼎
            path = "/dev/ttyAMA3";
        } else if (device.equals("daluotuo")) {
            path = "/dev/ttyS3";
        }
        Log.i(TAG, "onCreate device: " + device);
        try {
            if (mSerialPort == null) {
                mSerialPort = new SerialPort(new File(path), baudrate, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();

                isStop = false;
                mReadThread = new ReadThread();
                mReadThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开枪柜/开枪锁
     *
     * @param no
     */
    public boolean openLock(String no) {
        Log.i(TAG, "openLock: ");
        byte[] data = new byte[5];
        int i = Integer.parseInt(no);
        try {
            data[0] = (byte) (i & 0xff);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        data[1] = 0x55;
        data[2] = 0x01;
        data[3] = 0x23;
        data[4] = (byte) (data[0] ^ data[1] ^ data[2] ^ data[3] ^ data[4]);
        return sendBuffer(data);
    }

    //超级指令打开枪锁
    public boolean superCmdOpenLock() {
        Log.i(TAG, "superComandOpenLock: ");
        byte[] superCmd = {0x01, (byte) 0x88, 0x00, 0x23, (byte) 0xaa};
        return sendBuffer(superCmd);
    }

    /**
     * 设置新的锁地址
     *
     * @param address
     */
    public boolean setAddress(String address) {
        Log.i(TAG, "setAddress: ");
        byte[] data = new byte[6];
        data[0] = 0x01;
        data[1] = (byte) 0x88;
        data[2] = 0x01;
        data[3] = 0x24;
        data[4] = (byte) Integer.parseInt(address); //锁编号
        data[5] = (byte) (data[0] ^ data[1] ^ data[2] ^ data[3] ^ data[4]);
        return sendBuffer(data);
    }

    /**
     * 查询枪锁状态
     *
     * @param no
     */
    public void checkStatus(String no) {
        Log.i(TAG, "checkStatus: ");
        byte[] data = new byte[5];
        data[0] = (byte) Integer.parseInt(no);
        data[1] = 0x55;
        data[2] = 0x01;
        data[3] = 0x22;
        data[4] = (byte) (data[0] ^ data[1] ^ data[2] ^ data[3] ^ data[4]);
        sendBuffer(data);
    }

    /**
     * 查询锁在不在
     *
     * @param no
     */
    public void isLockExist(String no) {
        Log.i(TAG, "isLockExist: ");
        byte[] data = new byte[5];
        data[0] = (byte) Integer.parseInt(no);
        data[1] = 0x55;
        data[2] = 0x01;
        data[3] = 0x21;
        data[4] = (byte) (data[0] ^ data[1] ^ data[2] ^ data[3] ^ data[4]);
        sendBuffer(data);
    }

    /**
     * 发送指令到串口
     */
    public boolean sendCmds(String cmd) {
        Log.i(TAG, "sendCmds"+cmd);
        boolean result = true;
        byte[] mBuffer = TransformUtil.hex2bytes(cmd);
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean sendBuffer(byte[] mBuffer) {
        if (mSerialPort == null) {
            Log.e(TAG, "mSerialPort is null");
            onCreate();
        }
        boolean result = true;
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1, byte[] byte2) {
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    //设置子弹重量
    public boolean setBulletWeight(String no) {
        Log.i(TAG, "setBulletWeight: ");
        byte[] setWeight = {0x00, 0x55, 0x01, 0x2b, 0x00};
        setWeight[0] = (byte) Integer.parseInt(no);
        setWeight[4] = (byte) (setWeight[0] ^ setWeight[1] ^ setWeight[2] ^ setWeight[3]);
        return sendBuffer(setWeight);
    }

    //读取子弹重量
    public boolean readBulletWeight(String no) {
        Log.i(TAG, "readBulletWeight: ");
        byte[] readWeight = {0x00, 0x55, 0x01, 0x2d, 0x00};
        readWeight[0] = (byte) Integer.parseInt(no);
        readWeight[4] = (byte) (readWeight[0] ^ readWeight[1] ^ readWeight[2] ^ readWeight[3]);
        return sendBuffer(readWeight);
    }

    //读取子弹个数
    public boolean readBulletCount(String no) {
        Log.i(TAG, "readBulletCount: " + no);
        byte[] readCount = {0x00, 0x55, 0x01, 0x2e, 0x00};
        readCount[0] = (byte) Integer.parseInt(no);
        readCount[4] = (byte) (readCount[0] ^ readCount[1] ^ readCount[2] ^ readCount[3]);
        String s = TransformUtil.BinaryToHexString(readCount);
        Log.i(TAG, "readBulletCount 获取子弹数量命令: " + s);
        return sendBuffer(readCount);
    }

    //设置皮重
    public boolean setBulletTare(String no) {
        Log.i(TAG, "setBulletTare: ");
        byte[] setTare = {0x00, 0x55, 0x01, 0x27, 0x00};
        setTare[0] = (byte) Integer.parseInt(no);
        setTare[4] = (byte) (setTare[0] ^ setTare[1] ^ setTare[2] ^ setTare[3]);
        return sendBuffer(setTare);
    }

    private OnCountListener onCountListener;

    public void setOnCountListener(OnCountListener onCountListener) {
        this.onCountListener = onCountListener;
    }

    public interface OnCountListener {
        void onCountReceive(int no, int count);
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            Log.i(TAG, "run ReadThread: ");
            while (!isStop && !isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null) {
                        return;
                    }
                    Thread.sleep(100);
                    int nCount = mInputStream.available();
                    if (nCount == 0) {
                        continue;
                    }
//                    Log.i(TAG, "run  nCount: "+nCount);
                    byte[] buffer = new byte[nCount];
                    size = mInputStream.read(buffer);
//                    Log.i(TAG, "run size: "+size);
                    if (size > 0) {
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceive(buffer, size);
                            String hexString = TransformUtil.BinaryToHexString(buffer);
                            Log.i(TAG, "run  buffer:  " + hexString);
                            String[] split = hexString.split(" ");
                            if (split.length == 9) { //数据长度为9
                                if (split[0].equals("55") && split[2].equals("05") && split[3].equals("2E")) {
                                    int no = Integer.parseInt(split[1], 16);
                                    int count = Integer.parseInt(split[7], 16);
                                    Log.i(TAG, "onDataReceive : " + no + " 号子弹盒 数量:" + count);
                                    if (onCountListener != null) {
                                        onCountListener.onCountReceive(no, count);
                                    }
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

//    private class ReadThread extends Thread {
//        public void run() {
//            super.run();
//            for (; ; ) {
//                if (!isStop && !isInterrupted()) {
//                    try {
//                        if (mInputStream == null) {
//                            return;
//                        }
//                        int a = mInputStream.available();
//                        if(a <=0){
//                            return;
//                        }
//                        byte[] b = new byte[a];
//                        int i = mInputStream.read(b);
//                        if (i > 0) {
//                            if (null != onDataReceiveListener) {
//                                onDataReceiveListener.onDataReceive(b, i);
//                                Log.i(TAG, "run Arrays2String: "+ Arrays.toString(b)+"" +
//                                        " HEX: "+Utils.BinaryToHexString(b));
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    /**
     * 关闭串口
     */
    public void close() {
        try {
            isStop = true;
            if (this.mReadThread != null && !this.mReadThread.isInterrupted()) {
                this.mReadThread.interrupt();
            }
            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
            }
            if (onDataReceiveListener != null) {
                onDataReceiveListener = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
