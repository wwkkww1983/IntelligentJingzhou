package com.zack.intelligent.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.face.FaceManager;
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.BackActivity;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.ui.GetActivity;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;
import com.zack.intelligent.utils.YuvToBgr;
import com.zkteco.android.biometric.ZKLiveFaceService;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 指静脉验证
 */

public class LoginDialog extends Dialog implements
        FingerManager.IFingerStatus, SurfaceHolder.Callback {
    private static final String TAG = "LoginDialog";
    private static final int TASK_GET = 1;
    private static final int TASK_BACK = 2;
    private static final int TASK_URGENT = 3;
    private static final int TASK_KEEP = 4;
    private static final int TASK_SCRAP = 5;
    private static final int TASK_TEMPSTORE = 6;
    private static final int TASK_EXCHANGE = 7;
    private static final int TASK_USER = 8;

    @BindView(R.id.dialog_img_close)
    ImageView dialogImgClose;
    @BindView(R.id.dialog_txt_user)
    TextView dialogTxtUser;
    @BindView(R.id.dialog_txt_msg)
    TextView dialogTxtMsg;
    @BindView(R.id.login_dialog_iv_image)
    ImageView loginDialogIvImage;
    @BindView(R.id.login_dl_surface_view)
    SurfaceView loginDlSurfaceView;
    private Context mContext;
    private int retIdentify; //userID
    private boolean isStop = false;
    private Unbinder bind;
    private Class<?> toClass;
    private int streamId;
    private boolean isDutyManager;
    private boolean isCurrentLeader;
    private int event;
    private long context;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String txtMsg = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    if (dialogTxtUser != null && !TextUtils.isEmpty(txtMsg)) {
                        LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                        dialogTxtUser.setText(txtMsg);
                    }
                    break;
                case 1:
                    if (dialogTxtMsg != null && !TextUtils.isEmpty(txtMsg)) {
                        LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                        dialogTxtMsg.setText(txtMsg);
                    }
                    break;
            }
        }
    };
    private SurfaceHolder holder;
    private boolean contrast;
    private Camera mCamera;
    private int width = 640;
    private int height = 480;
    private Message message;
    private boolean isFirst = true;
    private List<MembersBean> policeList = new ArrayList<>();
    private MembersBean currentLeader;
    private List<MembersBean> currentManagers;
    private ExecutorService mExecutorService;
    private MembersBean firstPolice;

    public LoginDialog(@NonNull Context context, Class<?> toCls) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.toClass = toCls;
        Constants.openVibration = false;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_verify);
        bind = ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getPoliceList();
            }
        });
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getDutyLeader();
            }
        });
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getDutyManager();
            }
        });

        dialogTxtMsg.setText("请验证身份");

        int firstVerify = SharedUtils.getFirstVerify();
        Log.i(TAG, "initView first: " + firstVerify);
        if (toClass.equals(GetActivity.class)) { //出警
            LogUtil.i(TAG, "领枪: ");
            event = TASK_GET;
        } else if (toClass.equals(BackActivity.class)) { //出警
            LogUtil.i(TAG, "还枪: ");
            event = TASK_BACK;
        } else if (toClass.equals(UrgentGoActivity.class)) { //紧急出警
            LogUtil.i(TAG, "紧急出警: ");
            event = TASK_URGENT;
        } else if (toClass.equals(KeepActivity.class)) { //枪弹保养
            LogUtil.i(TAG, "枪弹保养: ");
            event = TASK_KEEP;
        } else if (toClass.equals(ScrapActivity.class)) { //枪弹报废
            LogUtil.i(TAG, "枪弹报废: ");
            event = TASK_SCRAP;
        } else if (toClass.equals(TempStoreActivity.class)) { //临时存放
            LogUtil.i(TAG, "临时存放: ");
            event = TASK_TEMPSTORE;
        } else if (toClass.equals(ExchangeActivity.class)) {
            event = TASK_EXCHANGE;
        } else if (toClass.equals(UserActivity.class)) {
            event = TASK_USER;
        } else {
            ToastUtil.showShort("不可执行的操作");
            dismiss();
        }

        switch (firstVerify) { //验证第一个人员身份
            case Constants.DEVICE_FINGER: //指纹验证
                if (event == TASK_USER || event == TASK_EXCHANGE) {
                    if (dialogTxtUser != null) {
                        dialogTxtUser.setText("请领导或管理员验证指纹");
                    }
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_finger);
                    if (dialogTxtUser != null) {
                        dialogTxtUser.setText("请值班管理员验证指纹");
                    }
                }
                if (loginDialogIvImage != null) {
                    loginDialogIvImage.setVisibility(View.VISIBLE);
                    loginDialogIvImage.setImageResource(R.drawable.finger_bg);
                }
                if (!Constants.isFingerInit) {
                    dialogTxtMsg.setText("指纹设备初始化失败");
                }
                if (!Constants.isFingerConnect) {
                    dialogTxtMsg.setText("指纹设备未连接");
                }
                if (Constants.isFingerConnect && Constants.isFingerInit) {
                    FingerManager.getInstance().fpsearch = false;
                    FingerManager.getInstance().searchfp(dialogTxtMsg, loginDialogIvImage, this);
                }
                break;
            case Constants.DEVICE_VEIN:
                break;
            case Constants.DEVICE_IRIS:  //虹膜
                break;
            case Constants.DEVICE_FACE:
                if (event == TASK_USER || event == TASK_EXCHANGE) {
                    if (dialogTxtUser != null) {
                        dialogTxtUser.setText("请领导或管理员验证人脸");
                    }
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_face);
                    if (dialogTxtUser != null) {
                        dialogTxtUser.setText("请值班管理员验证人脸");
                    }
                }
                loginDlSurfaceView.setVisibility(View.VISIBLE);
                if (!Constants.isFaceInit) {
                    dialogTxtMsg.setText("人脸初始化失败");
                }
                if (Constants.isFaceInit) {
                    startFace();
                }
                break;
        }
    }

    /**
     * 获取所有用户
     */
    private void getPoliceList() {
        HttpClient.getInstance().getPoliceList(mContext, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getPolice onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    String msg = dataBean.getMsg();
                    String body = dataBean.getBody();
                    policeList = JSON.parseArray(body, MembersBean.class);
                    Log.i(TAG, "onSucceed police list size: " + policeList.size());
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getPolice onFailed error: " + response.getException().getMessage());
            }
        });
    }

    /**
     * 获取值班领导
     */
    private void getDutyLeader() {
        //获取值班领导
        HttpClient.getInstance().getCurrentDuty(mContext, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getDutyLeader onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<MembersBean> leaders = JSON.parseArray(body, MembersBean.class);
                            if (leaders != null && !leaders.isEmpty()) {
                                currentLeader = leaders.get(0);
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
    }

    /**
     * 获取值班管理员
     */
    private void getDutyManager() {
        //获取值班枪管员
        HttpClient.getInstance().getCurrentDuty(mContext, 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getManagers onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            currentManagers = JSON.parseArray(body, MembersBean.class);
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

    private void startFace() {
        context = FaceManager.context;
        Log.i(TAG, "initView context: " + context);
        holder = loginDlSurfaceView.getHolder();
        contrast = true;
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        openCameraAndSetSurfaceviewSize();
    }

    private void openCameraAndSetSurfaceviewSize() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(0);
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(width, height);
                //parameters.setRotation(270);
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(0, info);
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                ToastUtil.showShort("无法打开相机");
                e.printStackTrace();
            }
        } else {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width, height);
            //parameters.setRotation(270);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            mCamera.setParameters(parameters);
        }
    }

    @OnClick(R.id.dialog_img_close)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_img_close:
                dismiss();
                break;
        }
    }

    private void verifySecondPolice(int id, int dtype) {
        MembersBean member = verifyIdentity(id, dtype);
        if (member != null) {
            String secondPoliceId = member.getId();
            String name = member.getName();
            Log.i(TAG, "run policeId: " + secondPoliceId + " 姓名：" + name);
            switch (event) {
                case TASK_GET:
                case TASK_BACK:
                    if (isDutyManager) { //值班管理员
                        sendHandle(0, "验证成功 当前用户:" + name);
                        Intent intent = new Intent(mContext, toClass);
                        intent.putExtra("firstPoliceInfo", JSON.toJSONString(firstPolice));
                        intent.putExtra("secondPoliceInfo", JSON.toJSONString(member));
                        mContext.startActivity(intent);
                        handle.post(new Runnable() {
                            @Override
                            public void run() {
                                LoginDialog.this.dismiss();
                            }
                        });

                    } else {
                        streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                        sendHandle(0, "没有权限 当前用户：" + name);
                        switch (SharedUtils.getSecondVerify()) {
                            case Constants.DEVICE_FINGER:
                                verifyFingerAgain();
                                break;
                            case Constants.DEVICE_VEIN:
                                break;
                            case Constants.DEVICE_IRIS:
                                break;
                            case Constants.DEVICE_FACE:
                                contrast = true;
                                break;
                        }
                    }
                    break;
                case TASK_URGENT:
                case TASK_KEEP:
                case TASK_SCRAP:
                case TASK_TEMPSTORE:
                    if (isCurrentLeader) {
                        sendHandle(0, "验证成功 当前用户：" + name);
                        Intent intent = new Intent(mContext, toClass);
                        intent.putExtra("firstPoliceInfo", JSON.toJSONString(firstPolice));
                        intent.putExtra("secondPoliceInfo", JSON.toJSONString(member));
                        mContext.startActivity(intent);
                        handle.post(new Runnable() {
                            @Override
                            public void run() {
                                LoginDialog.this.dismiss();
                            }
                        });
                    } else {
                        sendHandle(0, "没有权限！当前用户：" + name);
                        streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                        switch (SharedUtils.getSecondVerify()) {
                            case Constants.DEVICE_FINGER:
                                verifyFingerAgain();
                                break;
                            case Constants.DEVICE_VEIN:
                                break;
                            case Constants.DEVICE_IRIS:
                                break;
                            case Constants.DEVICE_FACE:
                                contrast = true;
                                break;
                        }
                    }
                    break;
            }
        } else {
            Log.i(TAG, "用户不存在: ");
            sendHandle(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            verifyFingerAgain();
        }
    }

    /**
     * 指纹验证
     *
     * @param id
     * @param success
     */
    @Override
    public void didVerify(int id, boolean success) {
        if (id != -1 && success) {
            if (isFirst) {
                verifyPolice(id, Constants.DEVICE_FINGER);
            } else {
                verifySecondPolice(id, Constants.DEVICE_FINGER);
            }
        } else {
            sendHandle(0, "验证失败！请重试! ");
            streamId = SoundPlayUtil.getInstance().play(R.raw.retry);
        }
    }

    @Override
    public void timeout() {
        LoginDialog.this.dismiss();
    }

    private void verifyFingerAgain() {
        if (Constants.isFingerConnect && Constants.isFingerInit) {
            FingerManager.getInstance().searchfp(dialogTxtMsg, loginDialogIvImage, LoginDialog.this);
        }
    }


    /**
     * 第一次验证警员身份
     *
     * @param id
     * @param dType
     */
    private void verifyPolice(int id, int dType) {
        //根据id获取当前警员身份
        firstPolice = verifyIdentity(id, dType);
        if (firstPolice != null) {
            String policeId = firstPolice.getId();
            String name = firstPolice.getName();
            Log.i(TAG, "run policeId: " + policeId + " 姓名：" + name);
            if (event == TASK_EXCHANGE || event == TASK_USER) {
                Intent intent = new Intent(mContext, toClass);
                intent.putExtra("police_info", JSON.toJSONString(firstPolice));
                mContext.startActivity(intent);
                LoginDialog.this.dismiss();
            } else {
                if (isDutyManager) {
                    //第二个值班管理员验证身份
                    secondVerify();
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendHandle(0, "没有权限 当前用户：" + name);
                    switch (SharedUtils.getFirstVerify()) {
                        case Constants.DEVICE_FINGER:
                            verifyFingerAgain();
                            break;
                        case Constants.DEVICE_VEIN:
                            break;
                        case Constants.DEVICE_IRIS:
                            break;
                        case Constants.DEVICE_FACE:
                            contrast = true;
                            break;
                    }
                }
            }
        } else {
            Log.i(TAG, "用户不存在: ");
            sendHandle(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            verifyFingerAgain();
        }
    }

    /**
     * 第二名人员验证身份
     */
    private void secondVerify() {
        isFirst = false;
        switch (SharedUtils.getSecondVerify()) {
            case Constants.DEVICE_FINGER:
                if (event == TASK_GET || event == TASK_BACK) {
                    sendHandle(0, "请值班管理员验证指纹");
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_finger);

                } else {
                    sendHandle(0, "请值班领导验证指纹");
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_finger);
                }

                if (Constants.isFingerConnect && Constants.isFingerInit) {
                    FingerManager.getInstance().fpsearch = false;
                    FingerManager.getInstance().searchfp(dialogTxtMsg, loginDialogIvImage, this);
                }
                handle.post(new Runnable() {
                    @Override
                    public void run() {
                        loginDlSurfaceView.setVisibility(View.GONE);
                        loginDialogIvImage.setVisibility(View.VISIBLE);
                        loginDialogIvImage.setImageResource(R.drawable.finger_bg);
                    }
                });

                break;
            case Constants.DEVICE_VEIN:
                break;
            case Constants.DEVICE_IRIS:
                break;
            case Constants.DEVICE_FACE:
                if (event == TASK_GET || event == TASK_BACK) {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_face);
                    sendHandle(0, "请值班管理员验证人脸");
                } else if (event == TASK_URGENT || event == TASK_KEEP || event == TASK_SCRAP || event == TASK_TEMPSTORE) {
                    sendHandle(0, "请值班领导验证人脸");
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_face);
                }
                handle.post(new Runnable() {
                    @Override
                    public void run() {
                        loginDlSurfaceView.setVisibility(View.VISIBLE);
                        loginDialogIvImage.setVisibility(View.GONE);
                    }
                });

                if (SharedUtils.getFaceOpen() && Constants.isFaceInit) {
                    startFace();
                }
                break;
        }
    }

    AlcoholDetectionDialog alcoholDetectionDialog;

    public void sendHandle(int what, Object obj) {
        Message message = mHandler.obtainMessage(what, obj);
        message.sendToTarget();
    }

    private MembersBean verifyIdentity(int id, int dType) {
        isCurrentLeader = false;
        isDutyManager = false;
        //根据指纹id获取警员信息
        if (policeList != null && !policeList.isEmpty()) {
            Log.i(TAG, "verifyIdentity policeId: " + id);
            for (int i = 0; i < policeList.size(); i++) {
                MembersBean membersBean = policeList.get(i);
                List<PoliceBiosBean> policeBios = membersBean.getPoliceBios();
                if (policeBios != null && !policeBios.isEmpty()) {
//                Log.i(TAG, "getPoliceInfo policeBios size: " + policeBios.size());
                    for (int j = 0; j < policeBios.size(); j++) {
                        PoliceBiosBean policeBiosBean = policeBios.get(j);
                        int deviceType = policeBiosBean.getDeviceType();
                        Log.i(TAG, "getPoliceInfo dType: " + dType);
                        if (deviceType == dType) { //设备类型
                            int fingerprintId = policeBiosBean.getFingerprintId();
                            Log.i(TAG, "getPoliceInfo fingerprintId: " + fingerprintId);
                            if (fingerprintId == id) {
                                String policeId = policeBiosBean.getPoliceId();
                                String name = membersBean.getName();
                                int policeType = membersBean.getPoliceType();
                                //验证是否值班管理员
                                verifyIsCurrentManager(policeId);
                                if (policeType == 3) { //领导
                                    if (currentLeader != null) {
                                        if (policeId.equals(currentLeader.getId())) {//值班领导
                                            isCurrentLeader = true;
                                        }
                                    }
                                }
                                LogUtil.i(TAG, "getIdentity  policeId: " + policeId + " ===警员姓名: " + name
                                        + " ===policeType:" + RTool.convertPoliceType(policeType));
                                return membersBean;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 验证管理员身份
     *
     * @param policeId
     */
    private void verifyIsCurrentManager(String policeId) {
        if (currentManagers != null && !currentManagers.isEmpty()) {
            for (int i = 0; i < currentManagers.size(); i++) {
                MembersBean membersBean = currentManagers.get(i);
                String managerId = membersBean.getId();
                LogUtil.i(TAG, "didVerify managerId: " + managerId);
                if (policeId != null && policeId.equals(managerId)) {
                    isDutyManager = true;
                    LogUtil.i(TAG, "verifyIsManager 是值班枪管员: ");
                }
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        SoundPlayUtil.getInstance().stop(streamId);
        FingerManager.getInstance().fpsearch = true;
        if (alcoholDetectionDialog != null && alcoholDetectionDialog.isShowing()) {
            alcoholDetectionDialog.dismiss();
            alcoholDetectionDialog = null;
        }
        try {
            bind.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Constants.openVibration = true;
        contrast = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null) {
            SetAndStartPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Void SetAndStartPreview(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            //mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewCallback(new Preview());
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class Preview implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (contrast) {
                contrast = false;
                analysis(data);
            }
        }
    }

    private void analysis(final byte[] data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "analysis run thread id: " + Thread.currentThread().getId());
                // TODO Auto-generated method stub
                if (data != null) {
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
                    byte[] bgrData = YuvToBgr.YV12ToRGB(image.getYuvData(), width, height);

                    int[] detectedFaces = new int[1];
                    long nTickStart = System.currentTimeMillis();
                    int ret = ZKLiveFaceService.detectFacesFromNV21(context, data, width, height, detectedFaces);
                    Log.i(TAG, "zktest-detectFaces ret=" + ret + ",count=" + detectedFaces[0] + ", timeUsed=" + (System.currentTimeMillis() - nTickStart));
                    if (ret == 0 && detectedFaces[0] > 0) {
                        SendOtherHandle(0, "探测人脸成功");
                        getFaceContext();
                    } else {
                        SendOtherHandle(1, "探测人脸失败，错误码:" + ret);
                    }
                }
            }
        }).start();
    }

    private void getFaceContext() {
        long[] faceContext = new long[1];
        int ret = 0;
        //获取单个人脸实例
        ret = ZKLiveFaceService.getFaceContext(context, 0, faceContext);
        Message msg = new Message();
        if (ret == 0) {
            Log.i(TAG, "_getFaceContext 获取单个人脸实例成功: ");
            SendOtherHandle(0, "获取人脸实例成功");
            extractTemplate(faceContext[0]);

        } else {
            Log.i(TAG, "_getFaceContext 获取单个人脸实例失败 ret: " + ret);
            SendOtherHandle(1, "获取人脸实例失败，错误码:" + ret);
            if (ret == 11) {
                getLastError(context);
            }
        }
    }

    /**
     * 提取人脸模板
     *
     * @param faceContext
     */
    private void extractTemplate(long faceContext) {
        Log.i(TAG, "_extractTemplate faceContext: " + faceContext);
        int ret = 0;
        byte[] template = new byte[2048];
        int[] size = new int[1];
        int[] resverd = new int[1];
        size[0] = 2048;
        //提取模板
        ret = ZKLiveFaceService.extractTemplate(faceContext, template, size, resverd);

        if (ret == 0) {
            Log.i(TAG, "_extractTemplate 提取模板成功: ");
            SendOtherHandle(0, "提取模板成功");

            int[] points = new int[4];
            //获取探测到人脸的矩形框
            int _ret = ZKLiveFaceService.getFaceRect(faceContext, points, 4);
            if (_ret == 0) {

            } else {

            }

            int[] score = new int[1];
            byte[] faceIDS = new byte[256];
            int[] maxRetCount = new int[1];
            maxRetCount[0] = 1;
            long nTickStart = System.currentTimeMillis();
            //1:N识别
            ret = ZKLiveFaceService.dbIdentify(context, template, faceIDS, score, maxRetCount, 72, 100);
            System.out.println("zktest- dbIdentify ret=" + ret + ",score=" + score[0] +
                    ",timeUsed=" + (System.currentTimeMillis() - nTickStart));
            String asciiFaceID = null;
            try {
                asciiFaceID = new String(Utils.byte2byte(faceIDS), "ascii");
                Log.i(TAG, "_extractTemplate ascii: " + asciiFaceID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "_extractTemplate template: " + TransformUtil.toHexString(template));
            if (ret == 0) {
                Log.i(TAG, "_extractTemplate 比对成功: " + score[0] + " ID:" + asciiFaceID);

                if (score[0] > 72) {
                    SendOtherHandle(0, "比对成功");
                    if (isFirst) {
                        verifyPolice(Integer.parseInt(asciiFaceID), Constants.DEVICE_FACE);
                    } else {
                        verifySecondPolice(Integer.parseInt(asciiFaceID), Constants.DEVICE_FACE);
                    }
                } else {
                    SendOtherHandle(1, "人脸比对失败 得分: " + score[0]);
                }
            } else {
                Log.i(TAG, "_extractTemplate 比对失败: " + score[0]);
                SendOtherHandle(1, "比对失败 ");
            }
        } else {
            Log.i(TAG, "_extractTemplate 提取模板失败: ");
            SendOtherHandle(1, "提取模板失败，错误码:" + ret);
        }
    }

    public void SendOtherHandle(int what, Object obj) {
        message = handle.obtainMessage(what, obj);
        message.sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (dialogTxtMsg == null) {
                return;
            }
            switch (msg.what) {
                case 0://进球
                    dialogTxtMsg.setText((String) msg.obj);
                    break;
                case 1://死球
                    dialogTxtMsg.setText((String) msg.obj);
                    contrast = true;
                    break;
                case 6://争球
                    if ((int) msg.obj > 72) {
                        dialogTxtMsg.setText("比对成功");
                    } else {
                        dialogTxtMsg.setText("比对失败");
                        contrast = true;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void getLastError(long context) {
        byte[] lasterror = new byte[256];
        int[] size = new int[1];
        size[0] = 256;
        if (0 == ZKLiveFaceService.getLastError(context, lasterror, size)) {
            String errStr = new String(lasterror, 0, size[0]);
            ToastUtil.showShort(errStr);
            SendOtherHandle(-1, errStr);
        }
    }

}
