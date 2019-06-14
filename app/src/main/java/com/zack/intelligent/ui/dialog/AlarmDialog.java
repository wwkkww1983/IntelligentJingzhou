package com.zack.intelligent.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.DataCache;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 报警窗体
 */
public class AlarmDialog extends Dialog implements
        FingerManager.IFingerStatus {
    private static final String TAG = "AlarmDialog";

    @BindView(R.id.dl_alarm_txt_msg)
    TextView dlAlarmTxtMsg;
    @BindView(R.id.dl_btn_relieve_alarm)
    Button dlBtnRelieveAlarm;
    @BindView(R.id.dl_img_alarm)
    ImageView dlImgAlarm;
    private Unbinder bind;
    private int streamId;
    @BindView(R.id.dl_alarm_txt_reason)
    TextView dlAlarmTxtReason;
    @BindView(R.id.dl_alarm_txt_verify)
    TextView dlAlarmTxtVerify;
    private Context context;
    private String reason;
    private int alarmType;
    private int retIdentify;
    private boolean isStop;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<MembersBean> policeList =new ArrayList<>();
    private String tag;

    public AlarmDialog(@NonNull Context context, String reason, int alarmLogType) {
        super(context, R.style.dialog);
        this.context = context;
        this.reason = reason;
        this.alarmType = alarmLogType;
        initView();
    }

    public void initView() {
        setContentView(R.layout.dialog_alarm);
        bind = ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPoliceList();
            }
        }).start();
        dlAlarmTxtReason.setText(reason);
        dlImgAlarm.setBackgroundResource(R.drawable.alarm_anim);
        AnimationDrawable animation = (AnimationDrawable) dlImgAlarm.getBackground();
        animation.setOneShot(false);
        animation.start();
        tag = UUID.randomUUID().toString();
        Log.i(TAG, "initView  uuid: "+tag);
        GreendaoMg.addAlarmLog(alarmType, reason, tag);
    }

    /**
     * 获取所有用户
     */
    private void getPoliceList() {
        HttpClient.getInstance().getPoliceList(context, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "getPolice onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String msg = dataBean.getMsg();
                        String body = dataBean.getBody();
                        if(!TextUtils.isEmpty(body)){
                            policeList = JSON.parseArray(body, MembersBean.class);
                        }else{
                            Log.i(TAG, "onSucceed 获取警员信息为空: ");
                        }
                    }else{
                        Log.i(TAG, "onSucceed 获取警员信息失败: ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getPolice onFailed error: " + response.getException().getMessage());
            }
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String txtMsg = (String) msg.obj;
            if (dlAlarmTxtMsg != null && !TextUtils.isEmpty(txtMsg)) {
                LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                if (dlAlarmTxtMsg != null) {
                    dlAlarmTxtMsg.setText(txtMsg);
                }
            }
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        Log.i(TAG, "alarm dialog dismiss: ");
        try {
            bind.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SoundPlayUtil.getInstance().stop(streamId);
        stopVerify(); //取消验证
    }

    private void stopVerify() {
        switch (SharedUtils.getBiosVerify()) {
            case Constants.DEVICE_FINGER://指纹识别解除报警
                FingerManager.getInstance().fpsearch = true;
                break;
            case Constants.DEVICE_VEIN://指静脉验证解除报警
                isStop = true;
                break;
            case Constants.DEVICE_IRIS: //虹膜识别解除报警
                break;
            case Constants.DEVICE_FACE://人脸识别解除报警
                break;
        }
    }

    @Override
    public void didVerify(int id, boolean success) {
        Log.i(TAG, "didVerify  id: " + id + " success: " + success);
        if (success) {
            verifyRelieve(id, Constants.DEVICE_FINGER);
        } else {
            streamId = SoundPlayUtil.getInstance().play(R.raw.verifyfailed);
            sendMsg("验证失败，请重试！");
        }
    }

    @Override
    public void timeout() {
        //读取指纹超时 重复读取数据
        FingerManager.getInstance().searchfp(dlAlarmTxtVerify, null, this);
    }

    private void verifyRelieve(int id, int devType) {
        MembersBean curPolicce = verifyIdentity(id, devType);
        if (curPolicce != null) {
            //获取到人员数据
            int policeType = curPolicce.getPoliceType(); //只有枪管员或者领导才有权限解除报警
            if (policeType == 1 || policeType == 3) {
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        SharedUtils.saveBackupOpenCabStatus(1);
                        SharedUtils.saveBackup2OpenCabStatus(1);
                        AlarmDialog.this.dismiss();
                    }
                });
                updateAlarmLog(curPolicce.getId(), curPolicce.getName(), tag);
                //生成报警日志
            } else {//非管理员 重新验证指纹
                streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                sendMsg("您没有权限");
                if (SharedUtils.getBiosVerify() == Constants.DEVICE_FINGER) {
                    verifyRetryFinger();
                }
            }
        } else {
            Log.i(TAG, "didVerify 身份识别失败: ");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            sendMsg("用户不存在");
            if (SharedUtils.getBiosVerify() == Constants.DEVICE_FINGER) {
                verifyRetryFinger();
            }
        }
    }

    private void updateAlarmLog(String policeId, String policeName, String tag) {
            try {
                //更新数据
                GreendaoMg.updateAlarmLog(policeId, policeName, tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private Dialog dialog;

    protected void showDialog(String msg) {
        if (!((Activity) context).isFinishing()) {
            if (dialog != null) {
                Log.i(TAG, "showDialog is not null and is not isShowing: ");
                DialogUtils.setTipText(msg);
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            } else {
                dialog = DialogUtils.creatTipDialog(context, "提示", msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedUtils.saveBackupOpenCabStatus(1);
                        SharedUtils.saveBackup2OpenCabStatus(1);
                        dialog.dismiss();
                    }
                });
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

    private void verifyRetryFinger() {
        FingerManager.getInstance().searchfp(dlAlarmTxtMsg, null, this);
    }

    private void sendMsg(String msg) {
        Message message = mHandle.obtainMessage();
        message.obj = msg;
        mHandle.sendMessage(message);
    }

    //根据指纹id获取人员信息
    private MembersBean verifyIdentity(int id, int devType) {
        List<MembersBean> membersBeanList = DataCache.getInstance().membersBeanList;
        if (policeList !=null && !policeList.isEmpty()) {
            MembersBean membersBean = getMembersBean(policeList, id, devType);
            if (membersBean != null) return membersBean;
        }else if (membersBeanList !=null && !membersBeanList.isEmpty()){
            MembersBean membersBean = getMembersBean(membersBeanList, id, devType);
            if (membersBean != null) return membersBean;
        }
        return null;
    }

    @Nullable
    private MembersBean getMembersBean(List<MembersBean> policeList, int id, int devType) {
        Log.i(TAG, "verifyIdentity size: " + policeList.size());
        for (int i = 0; i < policeList.size(); i++) {
            MembersBean membersBean = policeList.get(i);
            List<PoliceBiosBean> policeBios = membersBean.getPoliceBios();
            if (!policeBios.isEmpty()) {
                Log.i(TAG, "verifyIdentity policeBios size: " + policeBios.size());
                for (int j = 0; j < policeBios.size(); j++) {
                    PoliceBiosBean policeBiosBean = policeBios.get(j);
                    int deviceType = policeBiosBean.getDeviceType();
                    if (deviceType == devType) {
                        int fingerprintId = policeBiosBean.getFingerprintId();
                        if (fingerprintId == id) {
                            String policeId = policeBiosBean.getPoliceId();
                            String name = membersBean.getName();
                            int policeType = membersBean.getPoliceType();
                            LogUtil.i(TAG, "getIdentity  policeId: " + policeId + "  警员姓名: " + name
                                    + "policeType:" + RTool.convertPoliceType(policeType));
                            return membersBean;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean flag = false;

    @OnClick(R.id.dl_btn_relieve_alarm)
    public void onViewClicked() {
        //解除报警
        if (!flag) {
            flag = true;
            startVerify();
        } else {
            SoundPlayUtil.getInstance().stop(streamId);
            stopVerify();
            startVerify();
        }
    }

    private void startVerify() {
        try {
            streamId = SoundPlayUtil.getInstance().play(R.raw.viryfy_relieve);
            switch (SharedUtils.getBiosVerify()) {
                case Constants.DEVICE_FINGER: //指纹方式解除
                    Log.i(TAG, "startVerify 指纹方式 解除报警: ");
                    FingerManager.getInstance().fpsearch = false;
                    FingerManager.getInstance().searchfp(dlAlarmTxtMsg, null, this);
                    break;
                case Constants.DEVICE_VEIN: //指静脉方式
                    isStop = false;
                    break;
                case Constants.DEVICE_FACE://人脸识别方式解除
                    Log.i(TAG, "startVerify 人脸识别 解除报警: ");
                    break;
                case Constants.DEVICE_IRIS://虹膜识别方式解除
                    Log.i(TAG, "startVerify 虹膜识别 解除报警: ");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
