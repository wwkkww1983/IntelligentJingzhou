package com.zack.intelligent.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zack.intelligent.hardware.Sensor;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ups.Ups;
import com.zack.intelligent.utils.SharedUtils;

/**
 * 报警服务
 */

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private static final Long SLEEP_TIME = 60 *1000L;
    private boolean isQueryParamStop;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        //发送报警检测和发送读取数值命令
        new Thread(new UpsParamRunnable()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isQueryParamStop =true;
    }

    private class UpsParamRunnable implements Runnable {
        @Override
        public void run() {
            while(!isQueryParamStop){
                Ups.getInstance().queryParam();
                Sensor.getInstance().readHumitureValue();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
