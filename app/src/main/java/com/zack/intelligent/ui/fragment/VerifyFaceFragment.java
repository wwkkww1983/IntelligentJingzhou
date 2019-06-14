package com.zack.intelligent.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.face.FaceManager;
import com.zack.intelligent.ui.BackActivity;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.ui.GetActivity;
import com.zack.intelligent.ui.InStoreActivity;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.LoginActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;
import com.zack.intelligent.utils.YuvToBgr;
import com.zkteco.android.biometric.ZKLiveFaceService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 人脸识别验证
 */
public class VerifyFaceFragment extends Fragment implements SurfaceHolder.Callback {
    private static final String TAG = "VerifyFaceFragment";

    @BindView(R.id.verify_face_surface_view)
    SurfaceView verifyFaceSurfaceView;
    @BindView(R.id.verify_face_tv_msg)
    TextView verifyFaceTvMsg;
    Unbinder unbinder;
    private SurfaceHolder holder;
    private boolean contrast;
    private long context;
    private Camera mCamera;
    private int width = 640;
    private int height = 480;

    private List<MembersBean> membersBeanList;
    private List<MembersBean> currentManagers;
    private MembersBean currentLeader;
    private String target;
    private Class<?> toClass;
    private boolean isDutyManager = false;
    private boolean isDutyLeader = false;
    private int streamId;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String txtMsg = (String) msg.obj;
            if (verifyFaceTvMsg != null && !TextUtils.isEmpty(txtMsg)) {
                LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                verifyFaceTvMsg.setText(txtMsg);
            }
        }
    };
    private LoginActivity login;

    public VerifyFaceFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        login = (LoginActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_face, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        target = getActivity().getIntent().getStringExtra("activity");
        Log.i(TAG, "onCreateView activity: " + target);
        if (!TextUtils.isEmpty(target)) {
            if (Constants.isFirstVerify) {
                //第一次验证
                if(target.equals(Constants.ACTIVITY_USER) ||
                        target.equals(Constants.ACTIVITY_SETTING)){
                    streamId = SoundPlayUtil.getInstance().play(R.raw.admin_verify_face);
                    verifyFaceTvMsg.setText("请系统管理员验证人脸");
                }else if(target.equals(Constants.ACTIVITY_EXCHANGE)){
                    streamId = SoundPlayUtil.getInstance().play(R.raw.leader_manager_verify_face);
                    verifyFaceTvMsg.setText("请领导或管理员验证人脸");
                }else{
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_face);
                    verifyFaceTvMsg.setText("请值班管理员验证人脸");
                }
            } else {
                //第二次验证
                if (target.equals(Constants.ACTIVITY_URGENT)) { //紧急领枪
                    //验证值班领导指纹
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_face);
                    verifyFaceTvMsg.setText("请值班领导验证人脸");
                } else {
                    //验证值班管理员指纹
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_face);
                    verifyFaceTvMsg.setText("请下一位值班管理员验证人脸");
                }
            }
            switch (target) {
                case Constants.ACTIVITY_URGENT: //紧急出警
                    toClass = UrgentGoActivity.class;
                    break;
                case Constants.ACTIVITY_GET: //领取枪弹
                    toClass = GetActivity.class;
                    break;
                case Constants.ACTIVITY_BACK://归还枪弹
                    toClass = BackActivity.class;
                    break;
                case Constants.ACTIVITY_KEEP://保养枪弹
                    toClass = KeepActivity.class;
                    break;
                case Constants.ACTIVITY_SCRAP://报废枪弹
                    toClass = ScrapActivity.class;
                    break;
                case Constants.ACTIVITY_TEMP_STORE://临时存放
                    toClass = TempStoreActivity.class;
                    break;
                case Constants.ACTIVITY_EXCHANGE://值班管理
                    toClass = ExchangeActivity.class;
                    break;
                case Constants.ACTIVITY_USER: //紧急出警
                    toClass = UserActivity.class;
                    break;
                case Constants.ACTIVITY_IN_STORE://值班管理
                    toClass = InStoreActivity.class;
                    break;

            }
        }
        context = FaceManager.context;
        Log.i(TAG, "initView context: " + context);
        holder = verifyFaceSurfaceView.getHolder();
        contrast = true;
        holder.addCallback(this);
        OpenCameraAndSetSurfaceviewSize();
    }

    private void OpenCameraAndSetSurfaceviewSize() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        contrast = false;
        SoundPlayUtil.getInstance().stop(streamId);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated: ");
        if (mCamera != null) {
            SetAndStartPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed: ");
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

    public void setPoliceData(List<MembersBean> policeList) {
        this.membersBeanList = policeList;
        Log.i(TAG, "setPoliceData memberList size: " + membersBeanList.size());
    }

    public void setManagerData(List<MembersBean> currentManagers) {
        this.currentManagers = currentManagers;
        Log.i(TAG, "setManagerData  currentManager SIZE: " + currentManagers.size());
    }

    public void setLeaderData(MembersBean currentLeader) {
        this.currentLeader = currentLeader;
        Log.i(TAG, "setLeaderData  currentLeader name: " + currentLeader.getName());
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
                if (data != null) {
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
                    byte[] bgrData = YuvToBgr.YV12ToRGB(image.getYuvData(), width, height);
                    BitmapFactory.decodeByteArray(bgrData, 0, 100);

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
                    if (Constants.isFirstVerify) {
                        //第一次验证指纹
                        firstVerifyPolice(Integer.parseInt(asciiFaceID));
                    } else {
                        //第二次验证指纹
                        secondVerifyPolice(Integer.parseInt(asciiFaceID));
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
        Message message = handle.obtainMessage(what, obj);
        message.sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (verifyFaceTvMsg == null) {
                return;
            }
            switch (msg.what) {
                case 0://进球
                    verifyFaceTvMsg.setText((String) msg.obj);
                    break;
                case 1://死球
                    verifyFaceTvMsg.setText((String) msg.obj);
                    contrast = true;
                    break;
                case 6://争球
                    if ((int) msg.obj > 72) {
                        verifyFaceTvMsg.setText("比对成功");
                    } else {
                        verifyFaceTvMsg.setText("比对失败");
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

    /**
     * 第一次验证警员身份
     *
     * @param id
     */
    private void firstVerifyPolice(int id) {
        //根据id获取当前警员身份
        login.firstPolice = verifyIdentity(id);
        if (login.firstPolice != null) {
            String policeId = login.firstPolice.getId();
            String name = login.firstPolice.getName();
            String no = login.firstPolice.getNo();
            Log.i(TAG, "run policeId: " + policeId + " name：" + name);
            sendMsg(0, "验证成功，当前警员：" + name);
            if (target.equals(Constants.ACTIVITY_EXCHANGE)) {
                try {
                    sendMsg(0, "验证成功 当前用户:" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
//                        intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                    getContext().startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (target.equals(Constants.ACTIVITY_USER)) {
                if (name.equals("admin")) {
                    sendMsg(0, "验证成功 当前用户:" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
//                        intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                    getContext().startActivity(intent);
                    getActivity().finish();
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendMsg(0, "您没有权限 当前用户：" + name);
                    contrast = true;
                }
            } else {
                if (isDutyManager) {
                    if (target.equals(Constants.ACTIVITY_URGENT)) {
                        //值班领导验证指纹
                        sendMsg(0, "请值班领导验证指纹");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_face);
                    } else {
                        //值班管理员验证指纹
                        sendMsg(0, "请第二位值班管理员验证指纹");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_face);
                    }
                    Constants.isFirstVerify = false;
                    contrast = true;
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendMsg(0, "您没有权限 当前用户：" + name);
                    contrast = true;
                }
            }

        } else {
            Log.i(TAG, "获取用户信息失败: ");
            sendMsg(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            contrast = true;
        }
    }

    /**
     * 验证第二人指纹
     *
     * @param id
     */
    private void secondVerifyPolice(int id) {
        login.secondPolice = verifyIdentity(id);
        if (login.secondPolice != null) {
            String secondPoliceId = login.secondPolice.getId();
            String name = login.secondPolice.getName();
            Log.i(TAG, "run policeId: " + secondPoliceId + " 姓名：" + name);
            if (target.equals(Constants.ACTIVITY_URGENT)) {
                //判断是否值班领导
                if (isDutyLeader) {
                    sendMsg(0, "验证成功 当前用户：" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                    intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else {
                    sendMsg(0, "您没有权限！当前用户：" + name);
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    contrast = true;
                }
            } else {
                //判断是否值班管理员
                if (isDutyManager) { //值班管理员
                    sendMsg(0, "验证成功 当前用户:" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                    intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                    getContext().startActivity(intent);
                    getActivity().finish();
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendMsg(0, "您没有权限 当前用户：" + name);
                    contrast = true;
                }
            }
        } else {
            Log.i(TAG, "用户不存在: ");
            sendMsg(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            contrast = true;
        }
    }

    private MembersBean verifyIdentity(int id) {
        isDutyLeader = false;
        isDutyManager = false;
        //根据指纹id获取警员信息
        if (membersBeanList != null && !membersBeanList.isEmpty()) {
            Log.i(TAG, "verifyIdentity polices from memory: " + id);
            for (int i = 0; i < membersBeanList.size(); i++) {
                MembersBean membersBean = membersBeanList.get(i);
                List<PoliceBiosBean> policeBios = membersBean.getPoliceBios();
                if (policeBios != null && !policeBios.isEmpty()) {
//                Log.i(TAG, "getPoliceInfo policeBios size: " + policeBios.size());
                    for (int j = 0; j < policeBios.size(); j++) {
                        PoliceBiosBean policeBiosBean = policeBios.get(j);
                        int deviceType = policeBiosBean.getDeviceType();
                        if (deviceType == Constants.DEVICE_FACE) { //设备类型为人脸
                            int faceprintId = policeBiosBean.getFingerprintId();
                            Log.i(TAG, "getPoliceInfo faceprintId: " + faceprintId);
                            if (faceprintId == id) {
                                String policeId = policeBiosBean.getPoliceId();
                                String name = membersBean.getName();
                                int policeType = membersBean.getPoliceType();
                                //验证是否值班管理员
                                verifyIsCurrentManager(policeId);
                                if (policeType == 3) { //领导
                                    if (currentLeader != null) {
                                        if (policeId.equals(currentLeader.getId())) {//值班领导
                                            isDutyLeader = true;
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

    public void sendMsg(int what, Object obj) {
        Message message = mHandler.obtainMessage(what, obj);
        message.sendToTarget();
    }
}
