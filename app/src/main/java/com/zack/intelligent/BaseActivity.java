package com.zack.intelligent;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.intelligent.bean.AlarmLog;
import com.zack.intelligent.db.DBManager;
import com.zack.intelligent.event.EventConsts;
import com.zack.intelligent.event.MessageEvent;
import com.zack.intelligent.hardware.Sensor;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ui.MainActivity;
import com.zack.intelligent.ui.SettingsActivity;
import com.zack.intelligent.ui.dialog.AlarmDialog;
import com.zack.intelligent.ups.Ups;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity基类
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener,
        Sensor.OnHumitureValueListener {

    private static final String TAG = "BaseActivity";
    @BindView(R.id.ac_top_back)
    protected ImageButton ac_top_back;     //返回
    @BindView(R.id.ac_top_temper_txt)
    protected TextView ac_top_temper_txt;  //温度
    @BindView(R.id.ac_top_humidity_txt)
    protected TextView ac_top_humidity_txt;  //湿度
    @BindView(R.id.ac_top_net_txt)
    protected TextView ac_top_net_txt;     //网络状态
    @BindView(R.id.ac_top_date_txt)
    protected TextView ac_top_date_txt;    //当前日期
    @BindView(R.id.ac_top_power_txt)
    protected TextView acTopPowerTxt;
    @BindView(R.id.ac_top_date_img)
    protected ImageView acTopDateImg;
    @BindView(R.id.ac_top_setting)
    protected TextView acTopSetting;

    // 最大的屏幕亮度
    float maxLight;
    //当前的亮度
    float currentLight;
    //用来控制屏幕亮度
    Handler handler;
    //延时时间
    long delayTime = 10 * 60 * 1000L;

    protected long millisInFuture = 60;
    private Handler timeHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisInFuture--;
//            Log.i(TAG,"剩余时间：" + millisInFuture + "s");
            timeHandler.postDelayed(this, 1000);
            if (millisInFuture == 0) {
                Log.i(TAG, "run 倒计时结束: ");
                timeHandler.removeCallbacks(runnable);
                finish();
            }
        }
    };

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
//        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InitData();
        new Thread(timeTask).start();


        Sensor.getInstance().setOnHumitureValueListener(this);
    }


    private void InitData() {
        Log.i(TAG, "InitData: ");
        handler = new Handler(Looper.getMainLooper());
        maxLight = GetLightness(this);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {   //higher api
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            Log.i(TAG, "hideBottomUIMenu  uiOption: " + uiOptions);
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideDialogBottomUIMenu(final Dialog mDialog) {
        //隐藏虚拟按键，并且全屏
        mDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mDialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                //布局位于状态栏下方
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                //全屏
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                //隐藏导航栏
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                        if (Build.VERSION.SDK_INT >= 19) {
                            uiOptions |= 0x00001000;
                        } else {
                            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                        }
                        mDialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscriber(MessageEvent messageEvent) {
        if (messageEvent.getMessage().equals(EventConsts.EVENT_POWER_NORMAL)) {
            acTopPowerTxt.setText("市电正常");
            SharedUtils.savePowerStatus(0);
        } else if (messageEvent.getMessage().equals(EventConsts.EVENT_POWER_ABNORMAL)) {
            acTopPowerTxt.setText("备用电源");
            if (!isFinishing()) {
                if (SharedUtils.getAlarmOpen() && SharedUtils.getPowerStatus() == 0) {
                    Sensor.getInstance().alarmSwitch(1);
                    SharedUtils.savePowerStatus(1);
                    alarmDialog = new AlarmDialog(this, "智能柜断电",
                            Constants.ALARM_POWER_ABNORMAL);
                    if (!alarmDialog.isShowing()) {
                        alarmDialog.show();
                    }
                    cancelAlarm(alarmDialog);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        startSleepTask();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (!Constants.isDebug) {
            SerialPortUtil.getInstance().onCreate();
            SerialPortUtil.getInstance().setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {
                @Override
                public void onDataReceive(byte[] buffer, int size) {
                    final String hexString = TransformUtil.BinaryToHexString(buffer);
                    Log.i(TAG, "onDataReceive buffer: " + hexString);
                }
            });
        }

        if (ac_top_humidity_txt != null && ac_top_temper_txt != null) {
            ac_top_temper_txt.setText(SharedUtils.getTemperatureValue() + "℃");
            ac_top_humidity_txt.setText(SharedUtils.getHumidityValue() + "%");
        }

        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Window win = getWindow();
        hideBottomUIMenu();//隐藏虚拟按键
        win.getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        Log.i(TAG, "onSystemUiVisibilityChange visibility: " + visibility);
                        if (visibility == View.VISIBLE) {
                            hideBottomUIMenu();//隐藏虚拟按键
                        }
                    }
                });
        initwindows(win);
    }

    /**
     * 动态隐藏导航栏和状态栏
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged: ");
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 设置状态栏,导航栏透明
     */
    public void initwindows(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        stopSleepTask();
        Ups.getInstance().closeSerial();
        if (!Constants.isDebug) {
            SerialPortUtil.getInstance().close();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
//        if (alarmDialog != null) {
//            if (alarmDialog.isShowing()) {
//                alarmDialog.dismiss();
//            }
//            alarmDialog = null;
//        }
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if (alarmDialog != null) {
            alarmDialog.dismiss();
        }
    }


    private Runnable timeTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CharSequence time = DateFormat.format("yyyy-MM-dd HH:mm:ss",
                                System.currentTimeMillis());
                        if (ac_top_date_txt != null) {
                            ac_top_date_txt.setText(time);
                        }
//                        Log.i(TAG, "run: " + time);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_top_back:
                finish();
                break;
        }
    }

    protected AlarmDialog alarmDialog;
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Log.i(TAG, "onReceive action:  " + action);
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        SharedUtils.saveNetworkStatus(0);
                        Log.i(TAG, getString(R.string.net_normal));
                        ac_top_net_txt.setText(R.string.net_normal);
                        Drawable drawable = getResources().getDrawable(R.drawable.icon_net_status_connect);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        ac_top_net_txt.setCompoundDrawables(drawable, null, null, null);
                    } else {
                        //连接断开 报警 产生日志
                        Log.i(TAG, getString(R.string.net_disconnect));
                        ac_top_net_txt.setText(R.string.net_disconnect);
                        Drawable drawable = getResources().getDrawable(R.drawable.icon_net_status_disconnect);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        ac_top_net_txt.setCompoundDrawables(drawable, null, null, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void cancelAlarm(AlarmDialog alarmDialog) {
        if (alarmDialog != null) {
            alarmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Sensor.getInstance().alarmSwitch(0);
                }
            });
        }
    }


    /**
     * 设置亮度
     *
     * @param context
     * @param light
     */
    void SetLight(Activity context, int light) {
        currentLight = light;
        WindowManager.LayoutParams localLayoutParams = context.getWindow().getAttributes();
        localLayoutParams.screenBrightness = (light / 255.0F);
        context.getWindow().setAttributes(localLayoutParams);
    }

    /**
     * 获取亮度
     *
     * @param context
     * @return
     */
    float GetLightness(Activity context) {
        WindowManager.LayoutParams localLayoutParams = context.getWindow().getAttributes();
        float light = localLayoutParams.screenBrightness;
        return light;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (currentLight == 1) {
            startSleepTask();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开启休眠任务
     */
    void startSleepTask() {
        SetLight(this, (int) maxLight);
        handler.removeCallbacks(sleepWindowTask);
        handler.postDelayed(sleepWindowTask, delayTime);
    }

    /**
     * 结束休眠任务
     */
    void stopSleepTask() {
        handler.removeCallbacks(sleepWindowTask);
    }

    /**
     * 休眠任务
     */
    Runnable sleepWindowTask = new Runnable() {

        @Override
        public void run() {
            SetLight(BaseActivity.this, 1);
        }
    };

//    private AlarmDialog alarmDialog;

    /**
     * KEYCODE_F11 门磁靠近，监听到F11按键按下 门磁远离，监听到F11按键弹起
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F10://钥匙开门1
                Log.i(TAG, "onKeyDown 按下F10按键: ");
                backUp2OpenAlarm();
                return false;
            case KeyEvent.KEYCODE_F11://钥匙开门2
                Log.i(TAG, "onKeyDown 按下F11按键: ");
                backUpOpenAlarm();
                return false;
            case KeyEvent.KEYCODE_F12://震动传感器开启
                Log.i(TAG, "onKeyDown 按下F12按键: ");
                vibrationAlarm();
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void vibrationAlarm() {
        Log.i(TAG, "onKeyDown 按下了F12键: ");
        //打开报警器
        int openCabStatus = SharedUtils.getOpenCabStatus();
        boolean alarmOpen = SharedUtils.getAlarmOpen();
        Log.i(TAG, "onKeyDown open: " + alarmOpen + " status:" + openCabStatus);
        if (alarmOpen && openCabStatus == 0 && Constants.openVibration) {
            SharedUtils.saveOpenCabStatus(1);
            Sensor.getInstance().alarmSwitch(1);
            if (!isFinishing()) {
//                if (alarmDialog != null) {
//                    Log.i(TAG, "run alarmDialog is not null: ");
//                    alarmDialog.addAlarmType("柜体异常震动报警", Constants.ALARM_ABNORMAL_OPEN_DOOR);
//                    alarmDialog.show();
//                } else {
//                    Log.i(TAG, "run alarmDialog is null: ");
                alarmDialog = new AlarmDialog(BaseActivity.this,
                        "柜体异常震动报警", Constants.ALARM_ABNORMAL_OPEN_DOOR);
                if (!alarmDialog.isShowing()) {
                    alarmDialog.show();
                }
//                }
                cancelAlarm(alarmDialog);
            }
        }
    }

    private void backUpOpenAlarm() {
//        AlarmDialog alarmDialog = null;
//        Log.i(TAG, "onKeyDown 按下了F11键: ");
        int backupOpenCabStatus = SharedUtils.getBackupOpenCabStatus();
//                Log.i(TAG, "onKeyDown backupOpenCabStatus: "+SharedUtils.getBackupOpenCabStatus());
        if (SharedUtils.getAlarmOpen() && backupOpenCabStatus == 0) {
            SharedUtils.saveBackupOpenCabStatus(1);
            Sensor.getInstance().alarmSwitch(1);
            if (!isFinishing()) {
//                if (alarmDialog != null) {
//                    Log.i(TAG, "run alarmDialog is not null: ");
//                    alarmDialog.addAlarmType("备用方式打开柜门", Constants.ALARM_BACKUP_OPEN_GUN_LOCK);
//                    alarmDialog.show();
//                } else {
//                    Log.i(TAG, "run alarmDialog is null: ");
                alarmDialog = new AlarmDialog(BaseActivity.this,
                        "备用方式打开2柜门", Constants.ALARM_BACKUP_OPEN_GUN_LOCK);
                if (!alarmDialog.isShowing()) {
                    alarmDialog.show();
                }
//                }
                cancelAlarm(alarmDialog);
            }
        }
    }

    private void backUp2OpenAlarm() {
//        AlarmDialog alarmDialog = null;
//        Log.i(TAG, "onKeyDown 按下了F10键: ");
        int backup2OpenCabStatus = SharedUtils.getBackup2OpenCabStatus();
//                Log.i(TAG, "onKeyDown backupOpenCabStatus: "+SharedUtils.getBackupOpenCabStatus());
        if (SharedUtils.getAlarmOpen() && backup2OpenCabStatus == 0) {
            SharedUtils.saveBackup2OpenCabStatus(1);
            Sensor.getInstance().alarmSwitch(1);
            if (!isFinishing()) {
//                if (alarmDialog != null) {
//                    Log.i(TAG, "run alarmDialog is not null: ");
//                    alarmDialog.addAlarmType("备用方式打开柜门", Constants.ALARM_BACKUP_OPEN_GUN_LOCK);
//                    alarmDialog.show();
//                } else {
//                    Log.i(TAG, "run alarmDialog is null: ");
                alarmDialog = new AlarmDialog(BaseActivity.this,
                        "备用方式打开1柜门", Constants.ALARM_BACKUP_OPEN_GUN_LOCK);
                if (!alarmDialog.isShowing()) {
                    alarmDialog.show();
                }
//                }
                cancelAlarm(alarmDialog);
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F10:
                Log.i(TAG, "onKeyUp 弹起了F10键: ");
                SharedUtils.saveBackup2OpenCabStatus(0);
                return false;
            case KeyEvent.KEYCODE_F11: //门磁松开
                Log.i(TAG, "onKeyUp 弹起了F11键: ");
                SharedUtils.saveBackupOpenCabStatus(0);
                return false;
            case KeyEvent.KEYCODE_F12://震动传感器关闭 震动终止
                Log.i(TAG, "onKeyUp 弹起了F12键: ");
                SharedUtils.saveOpenCabStatus(0);
                return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onHumitureValue(String hValue) {
        if (!TextUtils.isEmpty(hValue)) {
            final String[] value = hValue.split(" ");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ac_top_humidity_txt != null && ac_top_temper_txt != null) {
                        ac_top_temper_txt.setText(value[2] + "." + value[3] + "℃");
                        ac_top_humidity_txt.setText(value[0] + "." + value[1] + "%");
                    }
                    SharedUtils.saveHumidityValue(value[0] + "." + value[1]);
                    SharedUtils.saveTemperatureValue(value[2] + "." + value[3]);
                }
            });
        }
    }
}
