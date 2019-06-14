package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.BitmapUtils;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FingerDialog extends Dialog implements FingerManager.IEnrollStatus {
    private static final String TAG = "FingerDialog";

    @BindView(R.id.left_little_finger)
    ImageView leftLittleFinger;
    @BindView(R.id.left_ring_finger)
    ImageView leftRingFinger;
    @BindView(R.id.left_middle_finger)
    ImageView leftMiddleFinger;
    @BindView(R.id.left_index_finger)
    ImageView leftIndexFinger;
    @BindView(R.id.left_thumb)
    ImageView leftThumb;
    @BindView(R.id.right_thumb)
    ImageView rightThumb;
    @BindView(R.id.right_index_finger)
    ImageView rightIndexFinger;
    @BindView(R.id.right_middle_finger)
    ImageView rightMiddleFinger;
    @BindView(R.id.right_ring_finger)
    ImageView rightRingFinger;
    @BindView(R.id.right_little_finger)
    ImageView rightLittleFinger;
    @BindView(R.id.ll_finger)
    LinearLayout llFinger;
    @BindView(R.id.finger_tv_msg)
    TextView fingerTvMsg;
    @BindView(R.id.dl_iv_close)
    ImageView dlIvClose;
    private MembersBean curPolice;
    private Map<Integer, String> bioList = new HashMap<>();
    private List<Integer> fingerIdList = new ArrayList<>();
    private Bitmap bitmap;
    private List<PoliceBiosBean> policeBiosList;
    private int bioType;
    private Unbinder bind;
    private Context context;
    private Dialog choiceDialog;
    private int streamId;

    public FingerDialog(@NonNull Context context, MembersBean curPolice, List<PoliceBiosBean> policeBiosList) {
        super(context, R.style.dialog);
        this.curPolice = curPolice;
        this.policeBiosList = policeBiosList;
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.hands);
        bind = ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initData();
    }

    public void initData() {
//        Log.i(TAG, "initData: " + membersBean);
        List<PoliceBiosBean> policeBios = curPolice.getPoliceBios();
        if (policeBios != null && !policeBios.isEmpty()) {
            clearImgFinger(); //重置
            bioList.clear();
            for (int i = 0; i < policeBios.size(); i++) {
                PoliceBiosBean policeBiosBean = policeBios.get(i);
                int deviceType = policeBiosBean.getDeviceType();
                if (deviceType == Constants.DEVICE_FINGER) {
                    int bioType = policeBiosBean.getBioType();
                    String bioId = policeBiosBean.getId();
                    bioList.put(bioType, bioId);
                    setImageByBioType(bioType);
                }
            }
        } else { //没有指纹数据则在切换时重置
            Log.i(TAG, "initData police bios is null: ");
            bioList.clear();
            clearImgFinger();
        }
    }

    /**
     * 重置
     */
    private void clearImgFinger() {
        leftThumb.setImageResource(R.drawable.left_thumb_blue);
        leftIndexFinger.setImageResource(R.drawable.left_index_finger_blue);
        leftMiddleFinger.setImageResource(R.drawable.left_middle_finger_blue);
        leftRingFinger.setImageResource(R.drawable.left_ring_finger_blue);
        leftLittleFinger.setImageResource(R.drawable.left_little_finger_blue);
        rightThumb.setImageResource(R.drawable.right_thumb_blue);
        rightIndexFinger.setImageResource(R.drawable.right_index_finger_blue);
        rightMiddleFinger.setImageResource(R.drawable.right_middle_finger_blue);
        rightRingFinger.setImageResource(R.drawable.right_ring_finger_blue);
        rightLittleFinger.setImageResource(R.drawable.right_little_finger_blue);
    }

    /**
     * 根据bioType设置图片
     */
    public void setImageByBioType(int bioType) {

        switch (bioType) {
            case 1:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.left_thumb_green);
                leftThumb.setImageBitmap(bitmap);
                break;
            case 2:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.left_index_finger_green);
                leftIndexFinger.setImageBitmap(bitmap);
                break;
            case 3:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.left_middle_finger_green);
                leftMiddleFinger.setImageBitmap(bitmap);
                break;
            case 4:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.left_ring_finger_green);
                leftRingFinger.setImageBitmap(bitmap);
                break;
            case 5:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.left_little_finger_green);
                leftLittleFinger.setImageBitmap(bitmap);
                break;
            case 6:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.right_thumb_green);
                rightThumb.setImageBitmap(bitmap);
                break;
            case 7:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.right_index_finger_green);
                rightIndexFinger.setImageBitmap(bitmap);
                break;
            case 8:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.right_middle_finger_green);
                rightMiddleFinger.setImageBitmap(bitmap);
                break;
            case 9:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.right_ring_finger_green);
                rightRingFinger.setImageBitmap(bitmap);
                break;
            case 10:
                bitmap = BitmapUtils.readBitMap(
                        getContext(), R.drawable.right_little_finger_green);
                rightLittleFinger.setImageBitmap(bitmap);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.left_little_finger, R.id.left_ring_finger, R.id.left_middle_finger,
            R.id.left_index_finger, R.id.left_thumb, R.id.right_thumb, R.id.right_index_finger,
            R.id.right_middle_finger, R.id.right_ring_finger, R.id.right_little_finger})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_little_finger:  //左手小指
                    if (bioList.containsKey(5)) {
                        deleteFinger(bioList.get(5));
                    } else {
                        addFingerPrint(5);
                    }
                break;
            case R.id.left_ring_finger:
                if (bioList.containsKey(4)) {
                    deleteFinger(bioList.get(4));
                } else {
                    addFingerPrint(4);
                }
                break;
            case R.id.left_middle_finger:
                if (bioList.containsKey(3)) {
                    deleteFinger(bioList.get(3));
                } else {
                    addFingerPrint(3);
                }
                break;
            case R.id.left_index_finger:
                if (bioList.containsKey(2)) {
                    deleteFinger(bioList.get(2));
                } else {
                    addFingerPrint(2);
                }
                break;
            case R.id.left_thumb:
                if (bioList.containsKey(1)) {
                    deleteFinger(bioList.get(1));
                } else {
                    addFingerPrint(1);
                }
                break;
            case R.id.right_thumb:
                if (bioList.containsKey(6)) {
                    deleteFinger(bioList.get(6));
                } else {
                    addFingerPrint(6);
                }
                break;
            case R.id.right_index_finger:
                if (bioList.containsKey(7)) {
                    deleteFinger(bioList.get(7));
                } else {
                    addFingerPrint(7);
                }
                break;
            case R.id.right_middle_finger:
                if (bioList.containsKey(8)) {
                    deleteFinger(bioList.get(8));
                } else {
                    addFingerPrint(8);
                }
                break;
            case R.id.right_ring_finger:
                if (bioList.containsKey(9)) {
                    deleteFinger(bioList.get(9));
                } else {
                    addFingerPrint(9);
                }
                break;
            case R.id.right_little_finger:
                if (bioList.containsKey(10)) {
                    deleteFinger(bioList.get(10));
                } else {
                    addFingerPrint(10);
                }
                break;
        }
    }

    private void addFingerPrint(int bioType) {
        this.bioType = bioType;
        FingerManager.getInstance().fperoll = false;
        FingerManager.getInstance().erollfp(fingerTvMsg, getFingerPrintId(), this);
    }

    public int getFingerPrintId() {
        Log.i(TAG, "getFingerPrintId: ");
        if (policeBiosList != null && !policeBiosList.isEmpty()) {
            for (int i = 0; i < policeBiosList.size(); i++) {
                PoliceBiosBean policeBiosBean = policeBiosList.get(i);
                int deviceType = policeBiosBean.getDeviceType();
                if (deviceType == Constants.DEVICE_FINGER) { //指纹
                    int fingerprintId = policeBiosBean.getFingerprintId();
                    fingerIdList.add(fingerprintId);
                }
//                Log.i(TAG, "getFingerId: " + fingerprintId);
            }
        }

        if (!fingerIdList.isEmpty()) {
            int emptyId = Collections.min(Utils.compare(fingerIdList, 1000)); //获取最大值
            Log.i(TAG, "initView min: " + emptyId);
            return emptyId;
        }
        return 1;
    }

    /**
     * 删除指纹
     */
    private void deleteFinger(final String bioId) {
        Log.i(TAG, "deleteFinger: " + bioId);
        choiceDialog = DialogUtils.createChoiceDialog(context,
                "确定要删除这条指纹数据吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除指纹
                        Log.i(TAG, "onClick 删除指纹。。。 ");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                deleteFingerBios(bioId);
                            }
                        }).start();
                    }
                });
        choiceDialog.show();
    }

    /**
     * 删除指纹
     *
     * @param bioId
     */
    private void deleteFingerBios(String bioId) {
        HttpClient.getInstance().deleteBios(context, bioId, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "deleteFingerBios onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    Log.i(TAG, "onSucceed 指纹删除成功: ");
                    getPoliceList();
                    showDialogAndFinish("删除成功");
                } else {
                    Log.i(TAG, "onSucceed 指纹删除失败: ");
                    showDialogAndFinish("删除失败");
                }
                choiceDialog.dismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "deleteFingerBios onFailed error: " + response.getException().getMessage());
                choiceDialog.dismiss();
            }
        });
    }

    @Override
    public void onEnrollStatus(int id, byte[] fpChar, boolean success) {
        if (success) {//注册指纹成功 上传指纹特征数据
            Log.i(TAG, "didVerify id: " + id);
            String key = TransformUtil.toHexString(fpChar);
            Log.i(TAG, "onEnrollStatus key1: "+key);
            PoliceBiosBean postPoliceBio = new PoliceBiosBean();
//            String encode = Base64.encodeToString(fpChar, Base64.DEFAULT); //特征转为base64编码格式
//            String key = new String(encode, 0, encode.length); //将字节流转为String字符串
//            Log.i(TAG, "onEnrollStatus encode: " + encode);

            postPoliceBio.setDeviceType(Constants.DEVICE_FINGER);
            postPoliceBio.setPoliceId(curPolice.getId()); //警员id
            postPoliceBio.setBioType(bioType);  //指纹类型
            postPoliceBio.setBioCheckType(1);  //指纹验证类型
            postPoliceBio.setFingerprintId(id);
            postPoliceBio.setKey(key);
            postPoliceBio.setTemplateType("LD9900");
            String jsonBody = JSON.toJSONString(postPoliceBio, SerializerFeature.WriteMapNullValue);
            Log.i(TAG, "onEnrollStatus jsonBody: " + jsonBody);

            HttpClient.getInstance().postPoliceBios(getContext(), jsonBody, new HttpListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) throws JSONException {
                    Log.i(TAG, "onSucceed response: " + response.get());
                    try {
                        DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                        boolean isSuccess = dataBean.isSuccess();
                        if (isSuccess) {
                            getPoliceList();
                            showDialogAndFinish("注册成功");
                        } else {
                            showDialogAndFinish("注册失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, Response<String> response) {
                    Log.i(TAG, "onFailed error: " + response.getException().getMessage());
                }
            });
        }
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
                        UserActivity ua = (UserActivity) context;
                        ua.policeList = polices;
                        curPolice = ua.policeList.get(ua.selectedPosition);
                        ua.curPolice = curPolice;
                        LogUtil.i(TAG, "onSucceed curPolice: " + JSON.toJSONString(curPolice));
//                            ua.initData(curPolice);
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

    @Override
    public void dismiss() {
        super.dismiss();
        bind.unbind();
        FingerManager.getInstance().fperoll = true;
        SoundPlayUtil.getInstance().stop(streamId);
    }

    Dialog tipDialog;

    private void showDialog(String msg) {
        tipDialog = DialogUtils.creatTipDialog(getContext(), "提示", msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
            }
        });
        tipDialog.show();
    }

    private void showDialogAndFinish(String msg) {
        tipDialog = DialogUtils.creatTipDialog(getContext(), "提示", msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                FingerDialog.this.dismiss();
            }
        });
        tipDialog.show();
    }
    @OnClick(R.id.dl_iv_close)
    public void onViewClicked() {
        dismiss();
    }
}
