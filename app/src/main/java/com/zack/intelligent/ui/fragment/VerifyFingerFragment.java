package com.zack.intelligent.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.ui.BackActivity;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.ui.GetActivity;
import com.zack.intelligent.ui.InStoreActivity;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.LoginActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.SettingsActivity;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.SoundPlayUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 指纹验证
 */
public class VerifyFingerFragment extends Fragment implements FingerManager.IFingerStatus {
    private static final String TAG = "VerifyFingerFragment";

    @BindView(R.id.verify_finger_iv_img)
    ImageView verifyFingerIvImg;
    @BindView(R.id.verify_finger_tv_msg)
    TextView verifyFingerTvMsg;
    Unbinder unbinder;
    @BindView(R.id.verify_finger_tv_user)
    TextView verifyFingerTvUser;
    private int streamId;
    private boolean isDutyManager = false;
    private boolean isDutyLeader = false;
    private MembersBean currentLeader;
    private List<MembersBean> currentManagers;
    private List<MembersBean> membersBeanList;
    private String target;
    private Class<?> toClass;
    private LoginActivity login;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String txtMsg = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    if (verifyFingerTvUser != null && !TextUtils.isEmpty(txtMsg)) {
                        LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                        verifyFingerTvUser.setText(txtMsg);
                    }
                    break;
                case 1:
                    if (verifyFingerTvMsg != null && !TextUtils.isEmpty(txtMsg)) {
                        LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                        verifyFingerTvMsg.setText(txtMsg);
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        login = (LoginActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_finger, container, false);
        unbinder = ButterKnife.bind(this, view);
        target = getActivity().getIntent().getStringExtra("activity");
        Log.i(TAG, "onCreateView activity: " + target);
        if (!TextUtils.isEmpty(target)) {
            if (Constants.isFirstVerify) {
                //第一次验证
                if (target.equals(Constants.ACTIVITY_USER) ||
                        target.equals(Constants.ACTIVITY_SETTING)) {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.admin_verify_finger);
                    verifyFingerTvUser.setText("请系统管理员验证指纹");
                } else if (target.equals(Constants.ACTIVITY_EXCHANGE)) {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.leader_manager_verify_finger);
                    verifyFingerTvUser.setText("请领导或管理员验证指纹");
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_finger);
                    verifyFingerTvUser.setText("请值班管理员验证指纹");
                }
            } else {
                //第二次验证
                if (target.equals(Constants.ACTIVITY_URGENT)) { //紧急领枪
                    //验证值班领导指纹
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_finger);
                    verifyFingerTvUser.setText("请值班领导验证指纹");
                } else {
                    //验证值班管理员指纹
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_finger);
                    verifyFingerTvUser.setText("请下一位值班管理员验证指纹");
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
                case Constants.ACTIVITY_IN_STORE://枪弹入库
                    toClass = InStoreActivity.class;
                    break;
                case Constants.ACTIVITY_SETTING://系统设置
                    toClass = SettingsActivity.class;
                    break;
                case Constants.ACTIVITY_USER://人员管理
                    toClass = UserActivity.class;
                    break;
            }
        }
        if (!Constants.isFingerConnect) {
            verifyFingerTvMsg.setText("指纹设备未连接");
        }
        if (!Constants.isFingerInit) {
            FingerManager.getInstance().init(getContext());
        }
        FingerManager.getInstance().fpsearch = false;
        FingerManager.getInstance().searchfp(verifyFingerTvMsg, verifyFingerIvImg, this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        FingerManager.getInstance().fpsearch = true;
        SoundPlayUtil.getInstance().stop(streamId);
    }

    public void sendMsg(int what, Object obj) {
        Message message = mHandler.obtainMessage(what, obj);
        message.sendToTarget();
    }

    @Override
    public void didVerify(int id, boolean success) {
        if (success) {
            if (Constants.isFirstVerify) {
                //第一次验证指纹
                firstVerifyPolice(id);
            } else {
                //第二次验证指纹
                secondVerifyPolice(id);
            }
        }
    }

    @Override
    public void timeout() {
        try {
            getActivity().finish();//读取指纹超时结束
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.i(TAG, "run policeId: " + policeId + " name：" + name);
            sendMsg(0, "验证成功，当前警员：" + name);
            if (target.equals(Constants.ACTIVITY_EXCHANGE)) {
                try {
                    sendMsg(0, "验证成功 当前用户:" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                    getContext().startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (target.equals(Constants.ACTIVITY_SETTING) ||
                    target.equals(Constants.ACTIVITY_USER)) {
                int policeType = login.firstPolice.getPoliceType();
                if (policeType == 0) {
                    sendMsg(0, "验证成功 当前用户:" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                    getContext().startActivity(intent);
                    getActivity().finish();
                } else {
                    SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendMsg(0, "您没有权限 当前用户：" + name);
                }
            } else {
                if (isDutyManager) {
                    if (target.equals(Constants.ACTIVITY_URGENT)) {
                        //值班领导验证指纹
                        sendMsg(0, "请值班领导验证指纹");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_finger);
                    } else {
                        //值班管理员验证指纹
                        sendMsg(0, "请第二位值班管理员验证指纹");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_finger);
                    }
                    Constants.isFirstVerify = false;
                    FingerManager.getInstance().searchfp(verifyFingerTvMsg, verifyFingerIvImg, this);
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendMsg(0, "您没有权限 当前用户：" + name);
                    verifyFingerAgain();//重新验证一次
                }
            }
        } else {
            Log.i(TAG, "获取用户信息失败: ");
            sendMsg(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            verifyFingerAgain();
        }
    }

    /**
     * 重新验证指纹
     */
    private void verifyFingerAgain() {
        FingerManager.getInstance().searchfp(verifyFingerTvMsg, verifyFingerIvImg, this);
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
                    verifyFingerAgain();
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
                    verifyFingerAgain();
                }
            }
        } else {
            Log.i(TAG, "用户不存在: ");
            sendMsg(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
            verifyFingerAgain();
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
                        if (deviceType == Constants.DEVICE_FINGER) { //设备类型
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
                                            isDutyLeader = true;
                                        }
                                    }
                                } else if (policeType == 1) {
                                    isDutyManager = true;
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
}
