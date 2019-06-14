package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
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
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.Utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 交接班
 */

public class ExchangeDialog extends Dialog implements FingerManager.IFingerStatus {

    private static final String TAG = ExchangeDialog.class.getSimpleName();
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
    private Unbinder bind;
    private Context context;
    private boolean isDutyManager;
    private String exchangeType;
    private boolean isManager;
    private boolean isStop;
    //    private int retIdentify;
    private int streamId;
    private MyHandler myHandle;
    private MembersBean manager;
    private List<MembersBean> managerList;
    private List<MembersBean> policeList;

    public ExchangeDialog(@NonNull Context context, String type, List<MembersBean> policeList) {
        super(context, R.style.dialog);
        this.context = context;
        this.exchangeType = type;
        Log.i(TAG, "initView exchangeType: " + exchangeType);
        this.policeList = policeList;
        initView();
    }

    private void initView() {
        Log.i(TAG, "nitView:i ");
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(R.layout.dialog_verify);
        bind = ButterKnife.bind(this);

        dialogImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExchangeDialog.this.dismiss();
            }
        });

        getDutyManager();

        isManager = false;
        isDutyManager = false;

        streamId = SoundPlayUtil.getInstance().play(R.raw.manage);
        myHandle = new MyHandler();
        int biosVerify = SharedUtils.getBiosVerify();
        Log.i(TAG, "initView  biosVerify: " + biosVerify);
        switch (biosVerify) {
            case Constants.DEVICE_FINGER: //指纹方式
                Log.i(TAG, "initView 指纹: ");
                loginDialogIvImage.setVisibility(View.VISIBLE);
                loginDialogIvImage.setImageResource(R.drawable.finger_bg);
                if (!Constants.isFingerConnect) {
                    dialogTxtMsg.setText("指纹设备未连接");
                }
                if (!Constants.isFingerInit) {
                    dialogTxtMsg.setText("指纹初始化失败");
                }

                dialogTxtUser.setText("请管理员验证指纹");
                if (Constants.isFingerConnect && Constants.isFingerInit) {
                    FingerManager.getInstance().fpsearch = false;
                    FingerManager.getInstance().searchfp(dialogTxtMsg, null, this);
                }
                break;
            case Constants.DEVICE_VEIN: //指静脉
                Log.i(TAG, "initView 指静脉: ");
                loginDialogIvImage.setVisibility(View.VISIBLE);
                loginDialogIvImage.setImageResource(R.drawable.ic_input_vein);
                dialogTxtUser.setText("请管理员验证指静脉");
                break;
            case Constants.DEVICE_FACE://人脸识别
                Log.i(TAG, "initView 人脸识别: ");
                loginDlSurfaceView.setVisibility(View.VISIBLE);
                dialogTxtUser.setText("请管理员验证人脸");
                if (!Constants.isFaceInit) {
                    dialogTxtMsg.setText("人脸初始化失败");
                }
                if (!SharedUtils.getFaceOpen()) {
                    dialogTxtMsg.setText("人脸功能未开启");
                }
                break;
            case Constants.DEVICE_IRIS://虹膜识别
                Log.i(TAG, "initView 虹膜识别: ");
                loginDialogIvImage.setVisibility(View.VISIBLE);
                loginDialogIvImage.setImageResource(R.drawable.iris_registered);
                if (!Constants.isIrisInit) {
                    dialogTxtMsg.setText("虹膜初始化失败");
                }else {
                    dialogTxtUser.setText("请管理员验证虹膜");
                }
                break;
            default:
                Log.i(TAG, "initView  nothing: ");
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.i(TAG, "dismiss: ");
        SoundPlayUtil.getInstance().stop(streamId);
        if (Constants.isFingerConnect && Constants.isFingerInit) {
            FingerManager.getInstance().fpsearch = true;
        }
        try {
            bind.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFirstVerify = true;

    @Override
    public void didVerify(int id, boolean success) {
        if (success) {
            setDutyManger(id, Constants.DEVICE_FINGER);
        } else {
            sendMsg("验证失败，请重试！");
        }
    }

    @Override
    public void timeout() {
        ExchangeDialog.this.dismiss();
    }

    private void setDutyManger(int id, int devType) {
        final MembersBean manager1 = verifyIdentity(id, devType);
        switch (exchangeType) {
            case ExchangeActivity.EXCHANGE: //枪管员值班交接
                if (isFirstVerify) { //第一次验证
                    manager = verifyIdentity(id, devType);
                    if (isDutyManager) { //验证当前值班管理员身份
                        //验证上线的管理员身份
                        isDutyManager = false;
                        streamId = SoundPlayUtil.getInstance().play(R.raw.manage);
                        isFirstVerify = false;
                        FingerManager.getInstance().searchfp(dialogTxtMsg, loginDialogIvImage, this);
                    } else {
                        sendMsg("您不是值班管理员");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.notdutymanager);
                        fingerRetry();
                    }
                } else { //第二次验证
//                    MembersBean manager2 = verifyIdentity(id, devType);
                    Log.i(TAG, "run  manager1 id: " + manager.getId());
                    Log.i(TAG, "run  manager2 id: " + manager1.getId());
                    if (!manager.getId().equals(manager1.getId())) {
                        if (!isDutyManager) { //不是值班管理员
                            if (isManager) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        exchangeManage(manager, manager1);
                                    }
                                }).start();

                            } else {
                                sendMsg("您不是管理员");
                                streamId = SoundPlayUtil.getInstance().play(R.raw.notmanager);
                                fingerRetry();
                            }
                        } else {//已经存在不作处理
                            sendMsg("您已经是值班管理员");
                            streamId = SoundPlayUtil.getInstance().play(R.raw.already_dutymanager);
                            fingerRetry();
                        }
                    } else {
                        sendMsg("必须由另一名管理员验证");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.duplicate_verify);
                        fingerRetry();
                    }
                }

                break;
            case ExchangeActivity.ONLINE: //枪管员上线  当前枪管员不能多于两个 已上线不作处理
                if (!isDutyManager) { //不是值班管理员
                    if (isManager) { //是管理员
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                setManager(manager1);
                            }
                        }).start();

                    } else {
                        streamId = SoundPlayUtil.getInstance().play(R.raw.notmanager);
                        sendMsg("您不是管理员");
                        fingerRetry();
                    }
                } else {
                    sendMsg("您已经是值班管理员");
                    streamId = SoundPlayUtil.getInstance().play(R.raw.already_dutymanager);
                }
                break;
            case ExchangeActivity.OFFLINE: //枪管员离班 当枪管员只有一个时不允许离班只允许交接
//                                if (curManagerList.size() > 1) {
                if (isDutyManager) {
                    Log.i(TAG, "didVerify isDutymanager: ");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setManager(manager1);
                        }
                    }).start();
                } else {
                    sendMsg("您不是值班管理员");
                    streamId = SoundPlayUtil.getInstance().play(R.raw.notdutymanager);
                    fingerRetry();
                }
                break;
            default:
                break;
        }
    }

    private void fingerRetry() {
        if (Constants.isFingerConnect && Constants.isFingerInit) {
            FingerManager.getInstance().searchfp(dialogTxtMsg, loginDialogIvImage, this);
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             if (msg.what == 0) {
                if (dialogTxtMsg != null) {
                    dialogTxtMsg.setText("验证失败, 请重新验证");
                }
            } else if (msg.what > 0) { //用户ID
                if (dialogTxtMsg != null) {
                    dialogTxtMsg.setText("验证成功");
                }
            } else {
                if (dialogTxtMsg != null) {
                    dialogTxtMsg.setText("验证失败, 请重新验证");
                }
            }
            String txtMsg = (String) msg.obj;
            if (dialogTxtMsg != null && !TextUtils.isEmpty(txtMsg)) {
                LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                if (dialogTxtMsg != null) {
                    dialogTxtMsg.setText(txtMsg);
                }
            }
        }
    }

    //发消息
    public void sendMsg(String msgInfo) {
        Message msg = myHandle.obtainMessage();
        msg.obj = msgInfo;
        myHandle.sendMessage(msg);
    }

    private void setManager(final MembersBean manager) {
        String currentId = null;
        String newId = null;
        if (exchangeType.equals(ExchangeActivity.ONLINE)) { //上线
            newId = manager.getId(); //将要离线的管理员
            currentId = null; //上线管理员
        } else if (exchangeType.equals(ExchangeActivity.OFFLINE)) { //离班
            newId = null; //上线管理员
            currentId = manager.getId(); //将要离班的管理员
        }

        HttpClient.getInstance().setDuty(getContext(), currentId, newId, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                try {
                    Log.i(TAG, "onSucceed  response: " + response.get());
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        showTipDialog("设置成功");
                    } else {
                        showTipDialog("设置失败");
                    }
                    ExchangeDialog.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private Dialog tipDialog;

    private void exchangeManage(final MembersBean cur, final MembersBean mem) {
        HttpClient.getInstance().setDuty(getContext(), cur.getId(), mem.getId(), 1,
                new HttpListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) throws JSONException {
                        Log.i(TAG, "onSucceed  response: " + response.get());
                        try {
                            DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                            boolean success = dataBean.isSuccess();
                            if (success) {
                                showTipDialog("设置成功");
                            } else {
                                showTipDialog("设置失败");
                            }
                            ExchangeDialog.this.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int what, Response<String> response) {
                        Log.e(TAG, "onFailed error: " + response.getException().getMessage());

                    }
                });
    }

    private void showTipDialog(String message) {
        tipDialog = DialogUtils.creatTipDialog(context, "提示", message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新值班管理员列表
                getDutyManager();
                tipDialog.dismiss();
            }
        });
        tipDialog.show();
    }

    private void getDutyManager() {
        HttpClient.getInstance().getCurrentDuty(getContext(), 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "getDutyManager onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            managerList = JSON.parseArray(body, MembersBean.class);
                            Log.i(TAG, "onSucceed  duty Manager list: " + managerList.size());
                            if (context instanceof ExchangeActivity) {
                                ExchangeActivity ea = (ExchangeActivity) context;
                                ea.adapter.setList(managerList);
                            }
                        }
                    } else {
                        ToastUtil.showShort(dataBean.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed  error: " + response.getException().getMessage());
            }
        });
    }

    private MembersBean verifyIdentity(int id, int devType) {
        Log.i(TAG, "verifyIdentity police list size: " + policeList.size());
        isManager = false;
        isDutyManager = false;
        //根据指纹id获取警员身份
        MembersBean member = null;
        if (policeList != null && policeList.size() > 0) {
            for (int i = 0; i < policeList.size(); i++) {
                MembersBean membersBean = policeList.get(i);
                List<PoliceBiosBean> policeBios = membersBean.getPoliceBios();
                if (policeBios != null && policeBios.size() > 0) {
                    for (int j = 0; j < policeBios.size(); j++) {
                        PoliceBiosBean policeBiosBean = policeBios.get(j);
                        int deviceType = policeBiosBean.getDeviceType();
                        if (deviceType == devType) {
                            int fingerprintId = policeBiosBean.getFingerprintId();
                            if (fingerprintId == id) {
                                String policeId = policeBiosBean.getPoliceId();
                                String name = membersBean.getName();
                                int policeType = membersBean.getPoliceType();
                                Log.i(TAG, "verifyIdentity policeType: " + policeType);
                                if (policeType == 1) { //枪管员
                                    isManager = true;
                                } else {
                                    isManager = false;
                                }
                                member = membersBean;
                                verifyIsManager(policeId);
                                Log.i(TAG, "getIdentity  policeId: " + policeId + "  警员姓名: " + name
                                        + " policeType:" + RTool.convertPoliceType(policeType));
                            }
                        }
                    }
                }
            }
        }
        return member;
    }

    /**
     * 验证管理员身份
     *
     * @param policeId
     */
    private void verifyIsManager(String policeId) {
        if (managerList != null && managerList.size() > 0) {
            for (int i = 0; i < managerList.size(); i++) {
                String managerId = managerList.get(i).getId();
                Log.i(TAG, "didVerify managerId: " + managerId);
                if (policeId != null && policeId.equals(managerId)) {
                    isDutyManager = true;
                    Log.i(TAG, "verifyIsManager 是值班枪管员: ");
                }
            }
        }
    }
}
