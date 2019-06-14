package com.zack.intelligent.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.Constants;
import com.zack.intelligent.DataCache;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.GunAmmoDataAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.GunTypeBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.face.FaceManager;
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.iris.IrisManager;
import com.zack.intelligent.service.AlarmService;
import com.zack.intelligent.service.CaptureService;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.ac_top_back)
    ImageButton acTopBack;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.iv_duty_bg)
    ImageView ivDutyBg;
    @BindView(R.id.main_tv_leader)
    TextView mainTvLeader;
    @BindView(R.id.main_tv_manager)
    TextView mainTvManager;
    @BindView(R.id.main_rl_duty)
    RelativeLayout mainRlDuty;
    @BindView(R.id.main_iv_handover)
    ImageView mainIvHandover;
    @BindView(R.id.main_btn_get_gun)
    Button mainBtnGetGun;
    @BindView(R.id.main_btn_back_gun)
    Button mainBtnBackGun;
    @BindView(R.id.main_rv_gun_ammo)
    RecyclerView mainRvGunAmmo;
    @BindView(R.id.main_ll_bottom_view)
    LinearLayout mainLlBottomView;
    @BindView(R.id.main_btn_sync)
    Button mainBtnSync;
    @BindView(R.id.main_btn_urgency_open)
    Button mainBtnUrgencyOpen;
    @BindView(R.id.main_iv_dq1)
    ImageView mainIvDq1;
    @BindView(R.id.main_rl_short1)
    RelativeLayout mainRlShort1;
    @BindView(R.id.main_iv_dq2)
    ImageView mainIvDq2;
    @BindView(R.id.main_rl_short2)
    RelativeLayout mainRlShort2;
    @BindView(R.id.main_iv_dq3)
    ImageView mainIvDq3;
    @BindView(R.id.main_rl_short3)
    RelativeLayout mainRlShort3;
    @BindView(R.id.main_iv_dq4)
    ImageView mainIvDq4;
    @BindView(R.id.main_rl_short_gun_4)
    RelativeLayout mainRlShortGun4;
    @BindView(R.id.main_ll_shortgun)
    LinearLayout mainLlShortgun;
    @BindView(R.id.main_iv_dq5)
    ImageView mainIvDq5;
    @BindView(R.id.main_iv_dq6)
    ImageView mainIvDq6;
    @BindView(R.id.main_iv_dq7)
    ImageView mainIvDq7;
    @BindView(R.id.main_iv_dq8)
    ImageView mainIvDq8;
    @BindView(R.id.main_ll_shortgun2)
    LinearLayout mainLlShortgun2;
    @BindView(R.id.main_iv_dq9)
    ImageView mainIvDq9;
    @BindView(R.id.main_rl_short9)
    RelativeLayout mainRlShort9;
    @BindView(R.id.main_iv_dq10)
    ImageView mainIvDq10;
    @BindView(R.id.main_rl_short10)
    RelativeLayout mainRlShort10;
    @BindView(R.id.main_iv_dq11)
    ImageView mainIvDq11;
    @BindView(R.id.main_iv_dq12)
    ImageView mainIvDq12;
    @BindView(R.id.main_rl_short_gun_12)
    RelativeLayout mainRlShortGun12;
    @BindView(R.id.main_ll_shortgun3)
    LinearLayout mainLlShortgun3;
    @BindView(R.id.main_iv_cq1)
    ImageView mainIvCq1;
    @BindView(R.id.main_rl_long1)
    RelativeLayout mainRlLong1;
    @BindView(R.id.main_iv_cq2)
    ImageView mainIvCq2;
    @BindView(R.id.main_rl_long2)
    RelativeLayout mainRlLong2;
    @BindView(R.id.main_iv_cq3)
    ImageView mainIvCq3;
    @BindView(R.id.main_rl_long3)
    RelativeLayout mainRlLong3;
    @BindView(R.id.main_iv_cq4)
    ImageView mainIvCq4;
    @BindView(R.id.main_rl_long4)
    RelativeLayout mainRlLong4;
    @BindView(R.id.main_ll_long)
    LinearLayout mainLlLong;
    @BindView(R.id.main_iv_ammo1)
    ImageView mainIvAmmo1;
    @BindView(R.id.main_rl_ammo1)
    RelativeLayout mainRlAmmo1;
    @BindView(R.id.main_iv_ammo2)
    ImageView mainIvAmmo2;
    @BindView(R.id.main_rl_ammo2)
    RelativeLayout mainRlAmmo2;
    @BindView(R.id.main_iv_ammo3)
    ImageView mainIvAmmo3;
    @BindView(R.id.main_rl_ammo3)
    RelativeLayout mainRlAmmo3;
    @BindView(R.id.main_iv_ammo4)
    ImageView mainIvAmmo4;
    @BindView(R.id.main_rl_ammo4)
    RelativeLayout mainRlAmmo4;
    @BindView(R.id.main_ll_ammo)
    LinearLayout mainLlAmmo;
    @BindView(R.id.main_tv_short_gun_1)
    TextView mainTvShortGun1;
    @BindView(R.id.main_tv_short_gun_2)
    TextView mainTvShortGun2;
    @BindView(R.id.main_tv_short_gun_3)
    TextView mainTvShortGun3;
    @BindView(R.id.main_tv_short_gun_4)
    TextView mainTvShortGun4;
    @BindView(R.id.main_tv_short_gun_5)
    TextView mainTvShortGun5;
    @BindView(R.id.main_tv_short_gun_6)
    TextView mainTvShortGun6;
    @BindView(R.id.main_tv_short_gun_7)
    TextView mainTvShortGun7;
    @BindView(R.id.main_tv_short_gun_8)
    TextView mainTvShortGun8;
    @BindView(R.id.main_tv_short_gun_9)
    TextView mainTvShortGun9;
    @BindView(R.id.main_tv_short_gun_10)
    TextView mainTvShortGun10;
    @BindView(R.id.main_tv_short_gun_11)
    TextView mainTvShortGun11;
    @BindView(R.id.main_tv_short_gun_12)
    TextView mainTvShortGun12;
    @BindView(R.id.main_tv_long_gun_1)
    TextView mainTvLongGun1;
    @BindView(R.id.main_tv_long_gun_2)
    TextView mainTvLongGun2;
    @BindView(R.id.main_tv_long_gun_3)
    TextView mainTvLongGun3;
    @BindView(R.id.main_tv_long_gun_4)
    TextView mainTvLongGun4;
    @BindView(R.id.main_tv_ammo_name_1)
    TextView mainTvAmmoName1;
    @BindView(R.id.main_tv_ammo_num_1)
    TextView mainTvAmmoNum1;
    @BindView(R.id.main_tv_ammo_name_2)
    TextView mainTvAmmoName2;
    @BindView(R.id.main_tv_ammo_num_2)
    TextView mainTvAmmoNum2;
    @BindView(R.id.main_tv_ammo_name_3)
    TextView mainTvAmmoName3;
    @BindView(R.id.main_tv_ammo_num_3)
    TextView mainTvAmmoNum3;
    @BindView(R.id.main_tv_ammo_name_4)
    TextView mainTvAmmoName4;
    @BindView(R.id.main_tv_ammo_num_4)
    TextView mainTvAmmoNum4;
    @BindView(R.id.main_rl_short_gun_5)
    RelativeLayout mainRlShortGun5;
    @BindView(R.id.main_rl_short_gun_6)
    RelativeLayout mainRlShortGun6;
    @BindView(R.id.main_rl_short_gun_7)
    RelativeLayout mainRlShortGun7;
    @BindView(R.id.main_rl_short8)
    RelativeLayout mainRlShort8;
    @BindView(R.id.main_rl_short_gun_11)
    RelativeLayout mainRlShortGun11;
    @BindView(R.id.main_iv_dq13)
    ImageView mainIvDq13;
    @BindView(R.id.main_tv_short_gun_13)
    TextView mainTvShortGun13;
    @BindView(R.id.main_rl_short_gun_13)
    RelativeLayout mainRlShortGun13;
    @BindView(R.id.main_iv_dq14)
    ImageView mainIvDq14;
    @BindView(R.id.main_tv_short_gun_14)
    TextView mainTvShortGun14;
    @BindView(R.id.main_rl_short_gun_14)
    RelativeLayout mainRlShortGun14;
    @BindView(R.id.main_iv_dq15)
    ImageView mainIvDq15;
    @BindView(R.id.main_tv_short_gun_15)
    TextView mainTvShortGun15;
    @BindView(R.id.main_rl_short15)
    RelativeLayout mainRlShort15;
    @BindView(R.id.main_iv_dq16)
    ImageView mainIvDq16;
    @BindView(R.id.main_tv_short_gun_16)
    TextView mainTvShortGun16;
    @BindView(R.id.main_rl_short16)
    RelativeLayout mainRlShort16;
    @BindView(R.id.main_iv_dq17)
    ImageView mainIvDq17;
    @BindView(R.id.main_tv_short_gun_17)
    TextView mainTvShortGun17;
    @BindView(R.id.main_rl_short17)
    RelativeLayout mainRlShort17;
    @BindView(R.id.main_iv_dq18)
    ImageView mainIvDq18;
    @BindView(R.id.main_tv_short_gun_18)
    TextView mainTvShortGun18;
    @BindView(R.id.main_rl_short_gun_18)
    RelativeLayout mainRlShortGun18;
    @BindView(R.id.main_iv_dq19)
    ImageView mainIvDq19;
    @BindView(R.id.main_tv_short_gun_19)
    TextView mainTvShortGun19;
    @BindView(R.id.main_rl_short_gun_19)
    RelativeLayout mainRlShortGun19;
    @BindView(R.id.main_iv_dq20)
    ImageView mainIvDq20;
    @BindView(R.id.main_tv_short_gun_20)
    TextView mainTvShortGun20;
    @BindView(R.id.main_rl_short_gun_20)
    RelativeLayout mainRlShortGun20;
    @BindView(R.id.main_ll_functional)
    LinearLayout mainLlFunctional;
    @BindView(R.id.main_ll_left_view)
    LinearLayout mainLlLeftView;
    @BindView(R.id.main_ll_shortgun4)
    LinearLayout mainLlShortgun4;
    @BindView(R.id.main_btn_pre_page)
    Button mainBtnPrePage;
    @BindView(R.id.main_tv_cur_page)
    TextView mainTvCurPage;
    @BindView(R.id.main_btn_next_page)
    Button mainBtnNextPage;
    @BindView(R.id.main_ll_include)
    LinearLayout mainLlInclude;

    private int index = 0;
    private int pageCount = 24;

    private List<SubCabsBean> subCabsBeanList;
    private GunAmmoDataAdapter gunAmmoDataAdapter;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (!Constants.isDebug) {
            //初始化虹膜
            if (SharedUtils.getIrisOpen()) {
                IrisManager.getInstance().initIris();
            }
            //初始化人脸识别
            if (SharedUtils.getFaceOpen()) {
                FaceManager.getInstance().init(this);
            }
        }
        acTopBack.setVisibility(View.GONE);
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        subCabsBeanList = new ArrayList<>();
        SharedUtils.setIsCapturing(false);
        SharedUtils.setOpenCapture(true);
        mainRvGunAmmo.setLayoutManager(new GridLayoutManager(this, 6));
        gunAmmoDataAdapter = new GunAmmoDataAdapter(subCabsBeanList, pageCount);
        mainRvGunAmmo.setAdapter(gunAmmoDataAdapter);

        initAlarmState();

        startService(new Intent(MainActivity.this, AlarmService.class));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbStateChangeReceiver, intentFilter);

        getUsbDevices();

    }

    @Override
    protected void onResume() {
        super.onResume();
        millisInFuture = 60;
        SharedUtils.setOpenCapture(true);
        //打开震动报警
        Constants.openVibration = true;

        mainTvCurPage.setVisibility(View.INVISIBLE);
        mainBtnPrePage.setVisibility(View.INVISIBLE);
        mainBtnNextPage.setVisibility(View.INVISIBLE);

        mainLlInclude.setVisibility(View.VISIBLE);
        mainRvGunAmmo.setVisibility(View.INVISIBLE);

        int cabType = SharedUtils.getGunCabType();
        if (cabType == 1) {
            pageCount = 12;
        } else {
            pageCount = 24;
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //获取枪柜数据
                getCabData();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取枪柜数据
                getDuty();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取枪支弹药类型
                getGunType();
            }
        }).start();
    }

    private BroadcastReceiver mUsbStateChangeReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive action: " + action);
            switch (action) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED: //USB设备插入
                    ToastUtil.showShort("USB设备插入");
                    UsbDevice inputDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    int vendorId1 = inputDevice.getVendorId();
                    int productId1 = inputDevice.getProductId();
                    String deviceName = inputDevice.getDeviceName();
                    String manufacturerName1 = inputDevice.getManufacturerName();
                    String productName1 = inputDevice.getProductName();

                    Log.i(TAG, "onReceive input device name: " + deviceName
                            + "  PID VID:" + String.format("%04X, %04X", productId1, vendorId1));
                    if (vendorId1 == 0x2109 && productId1 == 0x7638) {
                        if (manufacturerName1.equals("USBKey Chip") && productName1.equals("USBKey Module")) {
                            ToastUtil.showShort("指纹设备插入");
                            Constants.isFingerConnect = true;
                            FingerManager.getInstance().init(MainActivity.this);
                        }
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://USB设备拔出
                    ToastUtil.showShort("USB设备拔出");
                    UsbDevice outputDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    String manufacturerName = outputDevice.getManufacturerName();
                    String productName = outputDevice.getProductName();
                    String serialNumber = outputDevice.getSerialNumber();
//                    String version = outputDevice.getVersion();
                    Log.i(TAG, "onReceive manufacturerName: " + manufacturerName
                            + "  productName: " + productName
                            + "  serialNumber: " + serialNumber);

                    Log.i(TAG, "onReceive output device name: " + outputDevice.getDeviceName()
                            + "  PID VID:" + String.format("%04X, %04X",
                            outputDevice.getProductId(), outputDevice.getVendorId()));
                    int vendorId = outputDevice.getVendorId();
                    int productId = outputDevice.getProductId();
                    if (vendorId == 0x2109 && productId == 0x7638) {
                        if (manufacturerName.equals("USBKey Chip") && productName.equals("USBKey Module")) {
                            ToastUtil.showShort("指纹设备拔出");
                            Constants.isFingerConnect = false;
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 当前插入的usb设备
     *
     * @return
     */
    private int getUsbDevices() {
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        Iterator<String> iterator = deviceList.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Log.i(TAG, "getUsbDevices next: " + next);
        }
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.i(TAG, "getUsbDevices deviceName:" + device.getDeviceName()
                    + " VID PID:" + String.format("%04x", device.getVendorId(), device.getProductId()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if ((device.getVendorId() == 0x2109) && (0x7638 == device.getProductId())) {
                    String manufacturerName = device.getManufacturerName();
                    String productName = device.getProductName();
                    String serialNumber = device.getSerialNumber();
                    Log.i(TAG, "getUsbDevices manufacturerName: " + manufacturerName
                            + "  productName:" + productName
                            + "  serialNumber:" + serialNumber);
                    if (manufacturerName.equals("USBKey Chip") && productName.equals("USBKey Module")) {
                        ToastUtil.showShort("指纹设备已插入");
                        Log.i(TAG, "getUsbDevices 指纹设备已插入: ");
                        Constants.isFingerConnect = true;
                        FingerManager.getInstance().init(this);
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String version = device.getVersion();
                Log.i(TAG, "getUsbDevices version: " + version);
            }
        }
        return 0;
    }

    //获取枪支弹药类型
    private void getGunType() {
        HttpClient.getInstance().getGunType(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getGunType onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<GunTypeBean> gunTypeList = JSON.parseArray(body, GunTypeBean.class);
                            Log.i(TAG, "getGunType onSucceed gunType: " + gunTypeList.size());
                            DataCache.gunTypeBeanList.addAll(gunTypeList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getLog onFailed error: " + response.getException().getMessage());
            }
        });
    }

    /**
     * 报警状态初始化
     */
    private void initAlarmState() {
        SharedUtils.saveOpenCabStatus(0);//非正常开启柜门
        SharedUtils.saveOperGunStatus(0);//非正常领取枪支弹药
        SharedUtils.saveOutTimeStatus(0);//柜门超时未锁闭
        SharedUtils.savePowerStatus(0);//智能柜断电
        SharedUtils.saveBackupOpenCabStatus(0);//备用方式开启柜门
        SharedUtils.saveBackup2OpenCabStatus(0);//备用方式开启柜门
        SharedUtils.saveTempStatus(0);//温湿度异常报警
        SharedUtils.saveAlcoholStatus(0);//酒精检测异常
        SharedUtils.setVibration(0);//震动检测报警
        SharedUtils.saveNetworkStatus(0);//网络异常报警
    }

    /**
     * 获取值班领导和值班管理员
     */
    private void getDuty() {
        if (!Utils.isNetworkAvailable()) {
            Log.i(TAG, "getDuty isNetworkAvailable is false: ");
            return;
        }
        //获取值班领导
        HttpClient.getInstance().getCurrentDuty(this, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "get leaders onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<MembersBean> leaders = JSON.parseArray(body, MembersBean.class);
                            if (mainTvLeader != null) {
                                mainTvLeader.setText("值班领导:");
                                if (leaders != null && !leaders.isEmpty()) {
                                    for (int i = 0; i < leaders.size(); i++) {
                                        MembersBean membersBean = leaders.get(i);
                                        Log.i(TAG, "onSucceed 值班领导: " + membersBean.getName());
                                        mainTvLeader.append(" " + membersBean.getName());
                                        String leaderId = membersBean.getId();
                                        SharedUtils.saveDutyLeaderId(leaderId);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getDutyLeader onFailed error: " + response.getException().getMessage());
            }
        });

        //获取值班枪管员
        HttpClient.getInstance().getCurrentDuty(this, 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getManagers onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<MembersBean> managers = JSON.parseArray(body, MembersBean.class);
                            if (managers != null && !managers.isEmpty()) {
                                if (mainTvManager != null) {
                                    mainTvManager.setText("值班管理员:");
                                    for (int i = 0; i < managers.size(); i++) {
                                        MembersBean membersBean = managers.get(i);
                                        mainTvManager.append(" " + membersBean.getName());
                                        String curManageId = membersBean.getId();
                                        if (i == 0) {
                                            SharedUtils.saveDutyManagerId(curManageId);
                                        } else if (i == 1) {
                                            SharedUtils.saveDutyManagerId2(curManageId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "getDutyManager onFailed error: " + response.getException().getMessage());

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedUtils.setOpenCapture(false);
        Constants.openVibration = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, AlarmService.class));
        unregisterReceiver(mUsbStateChangeReceiver);
        stopService(new Intent(MainActivity.this, CaptureService.class));
    }

    /**
     * 获取枪柜数据
     */
    private void getCabData() {
        if (!Utils.isNetworkAvailable()) {
            Log.i(TAG, "getCabData isNetworkAvailable is false: ");
            return;
        }
        HttpClient.getInstance().getCabById(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                try {
                    LogUtil.i(TAG, "getCabs onSucceed  response: " + response.get());
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        String msg = dataBean.getMsg();
//                        Log.i(TAG, "getCabById onSucceed  msg: " + msg);
                        if (!TextUtils.isEmpty(body)) {
                            GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                            List<SubCabsBean> subCabs = gunCabsBean.getSubCabs();
                            mainRvGunAmmo.setVisibility(View.VISIBLE);
                            mainLlInclude.setVisibility(View.GONE);
                            subCabsBeanList.clear();
                            subCabsBeanList.addAll(subCabs);
                            gunAmmoDataAdapter.notifyDataSetChanged();
                            initPreNextBtn();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "getCabs onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private void initPreNextBtn() {
        if (subCabsBeanList.isEmpty()) {
            mainTvCurPage.setVisibility(View.INVISIBLE);
        } else {
            if (subCabsBeanList.size() <= pageCount) {
                mainTvCurPage.setVisibility(View.INVISIBLE);
                mainBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                mainTvCurPage.setVisibility(View.VISIBLE);
                mainBtnNextPage.setVisibility(View.VISIBLE);
            }
            mainTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsBeanList.size() / pageCount));
        }
    }

    @OnClick({R.id.main_iv_handover, R.id.main_btn_get_gun, R.id.main_btn_back_gun,
            R.id.main_btn_urgency_open, R.id.main_btn_sync, R.id.ac_top_setting,
            R.id.main_btn_pre_page, R.id.main_btn_next_page
    })
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.main_btn_get_gun://领枪
//                loginDialog = new LoginDialog(MainActivity.this, GetActivity.class);
//                loginDialog.show();
                if (!Constants.isDebug) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("activity", Constants.ACTIVITY_GET);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, GetActivity.class));
                }
                break;
            case R.id.main_btn_back_gun://还枪
//                loginDialog = new LoginDialog(MainActivity.this, BackActivity.class);
//                loginDialog.show();
                if (!Constants.isDebug) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("activity", Constants.ACTIVITY_BACK);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, BackActivity.class));
                }
                break;
            case R.id.main_btn_urgency_open: //紧急开锁
//                loginDialog = new LoginDialog(MainActivity.this, UrgentGoActivity.class);
//                loginDialog.show();
                if (!Constants.isDebug) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("activity", Constants.ACTIVITY_URGENT);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, UrgentGoActivity.class));
                }
                break;
            case R.id.ac_top_setting: //系统设置
//                if(!Constants.isDebug){
//                    intent = new Intent(MainActivity.this, LoginActivity.class);
//                    intent.putExtra("activity", Constants.ACTIVITY_SETTING);
//                    startActivity(intent);
//                }else{
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                }
                break;
            case R.id.main_iv_handover://交接班
//                loginDialog = new LoginDialog(MainActivity.this, ExchangeActivity.class);
//                loginDialog.show();
                if (!Constants.isDebug) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("activity", Constants.ACTIVITY_EXCHANGE);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, ExchangeActivity.class));
                }
                break;
            case R.id.main_btn_sync://系统维护
                intent = new Intent(MainActivity.this, IntegratedActivity.class);
                startActivity(intent);
                break;
            case R.id.main_btn_pre_page://上一页
                prePager();
                break;
            case R.id.main_btn_next_page://下一页
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        gunAmmoDataAdapter.setIndex(index);
        mainTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        gunAmmoDataAdapter.setIndex(index);
        mainTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            mainBtnPrePage.setVisibility(View.INVISIBLE);
            mainBtnNextPage.setVisibility(View.VISIBLE);
        } else if (subCabsBeanList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            mainBtnPrePage.setVisibility(View.VISIBLE);
            mainBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            mainBtnNextPage.setVisibility(View.VISIBLE);
            mainBtnPrePage.setVisibility(View.VISIBLE);
        }
    }


}
