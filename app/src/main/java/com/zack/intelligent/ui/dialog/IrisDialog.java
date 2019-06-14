package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.event.EventConsts;
import com.zack.intelligent.event.MessageEvent;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.iris.IrisManager;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.BitmapUtils;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.zack.intelligent.utils.DialogUtils.creatTipDialog;

/**
 * 注册虹膜
 */
public class IrisDialog extends Dialog {
    private static final String TAG = "IrisDialog";

    @BindView(R.id.dl_iris_iv_status)
    ImageView dlIrisIvStatus;
    @BindView(R.id.dl_iris_tv_msg)
    TextView dlIrisTvMsg;
    @BindView(R.id.dl_iris_iv_close)
    ImageView dlIrisIvClose;
    private MembersBean curPolice;
    private List<PoliceBiosBean> policeBiosBeanList;
    private Unbinder bind;
    private String bioId;
    private Context mContext;
    private boolean isRegistered;
    private int streamId;

    public IrisDialog(@NonNull Context context, MembersBean membersBean, List<PoliceBiosBean> list) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.curPolice = membersBean;
        this.policeBiosBeanList = list;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_iris);
        bind = ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        EventBus.getDefault().register(this);
        //初始化虹膜特征
        initData();
    }

    private void initData() {
        setImageByBioType(false);
        List<PoliceBiosBean> policeBios = curPolice.getPoliceBios();
        if (!policeBios.isEmpty()) {
            for (int i = 0; i < policeBios.size(); i++) {
                PoliceBiosBean policeBiosBean = policeBios.get(i);
                int deviceType = policeBiosBean.getDeviceType();
                if (deviceType == Constants.DEVICE_IRIS) { //虹膜
                    bioId = policeBiosBean.getId();
                    setImageByBioType(true);
                }
            }
        } else {
            setImageByBioType(false);
        }
    }

    private void setImageByBioType(boolean isRegister) {
        if (isRegister) {
            Log.i(TAG, "setImageByBioType : ");
            Bitmap bitmap = BitmapUtils.readBitMap(
                    mContext, R.drawable.iris_registered);
            dlIrisIvStatus.setImageBitmap(bitmap);
            dlIrisTvMsg.setText("已注册");
            isRegistered = true;
        } else {
            Log.i(TAG, "clearImgFinger: ");
            Bitmap bitmap = BitmapUtils.readBitMap(
                    mContext, R.drawable.iris_register);
            dlIrisIvStatus.setImageBitmap(bitmap);
            dlIrisTvMsg.setText("未注册");
            isRegistered = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSubscriber(MessageEvent messageEvent) {
        String message = messageEvent.getMessage();
//        Log.i(TAG, "onEventSubscriber message: " + message);
        switch (message) {
            case EventConsts.KEEP_CURRENT_STATUS:
                streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_keep_current_status);
                break;
            case EventConsts.ADJUST_DISTANCE:
                streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_adjust_distance);
                break;
            case EventConsts.WATCH_MIRROR:
                streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_watch_mirror);
                break;
            case EventConsts.CLOSE_TO:
                streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_please_close);
                break;
            case EventConsts.OPEN_EYES:
                streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_open_eyes);
                break;
            case EventConsts.DONT_LOOK_AWRY:
                streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_dont_look_awry);
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bind.unbind();

        IrisManager.getInstance().cancelAction();
        EventBus.getDefault().unregister(this);
    }

    private List<Integer> bioIdList = new ArrayList<>();

    private int getIrisId() {
        if (!policeBiosBeanList.isEmpty()) {
            for (int i = 0; i < policeBiosBeanList.size(); i++) {
                PoliceBiosBean policeBiosBean = policeBiosBeanList.get(i);
                int deviceType = policeBiosBean.getDeviceType();
                if (deviceType == Constants.DEVICE_IRIS) { //虹膜
                    int biosId = policeBiosBean.getFingerprintId();
                    Log.i(TAG, "getIrisId bioID: " + biosId);
                    bioIdList.add(biosId);
                }
            }
        }
        if (!bioIdList.isEmpty()) {
            List<Integer> compare = Utils.compare(bioIdList, 100);
            int min = Collections.min(compare);
            Log.i(TAG, "getIrisId min: " + min);
//                int i = Collections.max(bioIdList) + 1;
//                Log.i(TAG, "getIrisId i: " + i);
            return min;
        }
        return 1;
    }

    @OnClick({R.id.dl_iris_iv_close, R.id.dl_iris_iv_status})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.dl_iris_iv_close:
                dismiss();
                break;
            case R.id.dl_iris_iv_status:
                //注册删除虹膜
                enrollOrDeleteIris();
                break;
        }
    }

    private void enrollOrDeleteIris() {
        dlIrisTvMsg.setText("");
        Log.i(TAG, "onViewClicked isRegistered: " + isRegistered);
        if (isRegistered) { //已注册
            Log.i(TAG, "onViewClicked 已注册 删除模板: ");
            if (!TextUtils.isEmpty(bioId)) {
                deleteIris(bioId);
            }
        } else { //未注册过
            Log.i(TAG, "onViewClicked 未注册 注册模板: ");
            String irisId = String.valueOf(getIrisId());
            Log.i(TAG, "onViewClicked irisId: " + irisId);
            enrollIris(irisId);
        }
    }

    private void enrollIris(final String irisId) {
        IrisManager.getInstance().registerIris(irisId, dlIrisTvMsg, new IrisManager.OnRegisteredReceiv() {
            @Override
            public void onResult(int result) {
                Log.i(TAG, "onResult : " + result);
                switch (result) {
                    case 0: //注册成功 上传服务器
                        streamId = SoundPlayUtil.getInstance().play(R.raw.enroll_ok);
                        IrisManager.getInstance().getTemp(irisId, dlIrisTvMsg, new IrisManager.OnTempReceiv() {
                            @Override
                            public void onTempReceiv(byte[] temp) {
                                Log.i(TAG, "onTempReceiv: " + TransformUtil.toHexString(temp));
                                uploadChar(temp, Integer.parseInt(irisId));
                            }
                        });
                        break;
                    case 1: //注册失败
                        streamId = SoundPlayUtil.getInstance().play(R.raw.enroll_failed);
                        break;
                    case 2: //不同id重复注册
                        streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_diff_duplicated);
                        break;
                    case 3://相同id重复注册
                        streamId = SoundPlayUtil.getInstance().play(R.raw.iris_enroll_same_duplicated);
                        break;
                }
            }
        });
    }

    private void uploadChar(byte[] enrollBuf, final int userId) {
        Log.i(TAG, "uploadChar userId: " + userId);
//        byte[] encode = Base64.encode(enrollBuf, Base64.DEFAULT);
//        String key = new String(encode, 0, encode.length); //将字节流转为String字符串
        String key = TransformUtil.toHexString(enrollBuf);
        Log.i(TAG, "onEnrollStatus key: " + key);
        PoliceBiosBean postPoliceBio = new PoliceBiosBean();
        postPoliceBio.setPoliceId(curPolice.getId()); //警员id
        postPoliceBio.setDeviceType(Constants.DEVICE_IRIS);
        postPoliceBio.setBioType(1);  //虹膜类型
        postPoliceBio.setBioCheckType(1);  //虹膜验证类型
        postPoliceBio.setFingerprintId(userId);
        postPoliceBio.setKey(key);
        postPoliceBio.setTemplateType("ELF-02");
        String jsonBody = JSON.toJSONString(postPoliceBio, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "onEnrollStatus jsonBody: " + jsonBody);

        HttpClient.getInstance().postPoliceBios(mContext, jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postPoliceBios onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                String msg = dataBean.getMsg();
                if (success) {
                    showDialogAndDismiss("注册成功");
                    getPoliceList();
                } else {
                    showDialogAndDismiss("注册失败");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error: " + response.getException().getMessage());
                showDialogAndDismiss("注册失败");
            }
        });
    }

    private Dialog choiceDialog;

    /**
     * 删除虹膜
     *
     * @param bioId 生物特征id
     */
    private void deleteIris(final String bioId) {
        choiceDialog = DialogUtils.createChoiceDialog(mContext,
                "确定要删除这条虹膜数据吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteBioById(bioId);
                    }
                });
        choiceDialog.show();
    }

    private void deleteBioById(final String bioId) {
        HttpClient.getInstance().deleteBios(mContext, bioId, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "onSucceed  response : " + response.get());

                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    showDialogAndDismiss("删除成功");
                    //删除模块内特征
                    List<PoliceBiosBean> policeBios = curPolice.getPoliceBios();
                    if (policeBios != null && !policeBios.isEmpty()) {
                        for (int i = 0; i < policeBios.size(); i++) {
                            PoliceBiosBean policeBiosBean = policeBios.get(i);
                            String id = policeBiosBean.getId();
                            if (id.equals(bioId)) {
                                int fingerprintId = policeBiosBean.getFingerprintId();
                                IrisManager.getInstance().deleteTempByID(String.valueOf(fingerprintId), dlIrisTvMsg);
                            }
                        }
                    }
                    getPoliceList();
                } else {
                    showDialogAndDismiss("删除失败");
                }
                choiceDialog.dismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed  error: " + response.getException().getMessage());
                showDialogAndDismiss("删除失败");
            }
        });
    }

    private void getPoliceList() {
        HttpClient.getInstance().getPoliceList(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getPoliceList onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String msg = dataBean.getMsg();
                    if (success) {
                        String body = dataBean.getBody();
                        List<MembersBean> polices = JSON.parseArray(body, MembersBean.class);
                        UserActivity ua = (UserActivity) mContext;
                        ua.policeList = polices;
                        curPolice = ua.policeList.get(ua.selectedPosition);
                        ua.curPolice = curPolice;
                        LogUtil.i(TAG, "onSucceed curPolice: " + JSON.toJSONString(curPolice));
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getPoliceList onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private Dialog tipDialog;

    private void showDialogAndDismiss(String msg) {
        if (tipDialog == null) {
            tipDialog = creatTipDialog(mContext, "提示", msg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipDialog.dismiss();
                    IrisDialog.this.dismiss();
                }
            });
            if (!tipDialog.isShowing()) {
                tipDialog.show();
            }
        } else {
            if (!tipDialog.isShowing()) {
                tipDialog.show();
            }
        }
    }

}
