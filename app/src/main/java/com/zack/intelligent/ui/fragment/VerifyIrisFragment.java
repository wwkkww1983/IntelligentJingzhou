package com.zack.intelligent.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.zack.intelligent.iris.IrisManager;
import com.zack.intelligent.ui.BackActivity;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.ui.GetActivity;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.LoginActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.Utils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 虹膜识别验证身份
 */
public class VerifyIrisFragment extends Fragment implements IrisManager.OnReceiveCallback {

    private static final String TAG = VerifyIrisFragment.class.getSimpleName();
    @BindView(R.id.verify_iris_iv_img)
    ImageView verifyIrisIvImg;
    @BindView(R.id.verify_iris_tv_msg)
    TextView verifyIrisTvMsg;
    Unbinder unbinder;

    private List<MembersBean> membersBeanList;
    private List<MembersBean> currentManagers;
    private MembersBean currentLeader;
    private String target;
    private Class<?> toClass;
    private int streamId;
    private boolean isDutyManager;
    private boolean isDutyLeader;
    private LoginActivity login;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String txtMsg = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    if (verifyIrisTvMsg != null && !TextUtils.isEmpty(txtMsg)) {
                        LogUtil.i(TAG, "handleMessage msg: " + txtMsg);
                        verifyIrisTvMsg.setText(txtMsg);
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        login = (LoginActivity) context;
    }

    public VerifyIrisFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_iris, container, false);
        unbinder = ButterKnife.bind(this, view);
        target = getActivity().getIntent().getStringExtra("activity");
        Log.i(TAG, "onCreateView activity: " + target);
        if (!TextUtils.isEmpty(target)) {
            if (Constants.isFirstVerify) {
                //第一次验证 验证值班管理员虹膜
                if (target.equals(Constants.ACTIVITY_EXCHANGE)) {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.verify_iris);
                    verifyIrisTvMsg.setText("请验证虹膜");
                } else if(target.equals(Constants.ACTIVITY_USER)){
                    streamId =SoundPlayUtil.getInstance().play(R.raw.admin_iris);
                    verifyIrisTvMsg.setText("请系统管理员验证虹膜");
                }else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_iris);
                    verifyIrisTvMsg.setText("请值班管理员验证虹膜");
                }

            } else {
                //第二次验证
                if (target.equals(Constants.ACTIVITY_URGENT)) { //紧急领枪
                    //验证值班领导虹膜
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_iris);
                    verifyIrisTvMsg.setText("请值班领导验证虹膜");
                } else {
                    //非紧急领枪 验证值班管理员虹膜
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_iris);
                    verifyIrisTvMsg.setText("请下一位值班管理员验证虹膜");
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

            }
        }


        if (!Constants.isIrisInit) {
            verifyIrisTvMsg.setText("虹膜初始化失败");
        } else {
            IrisManager.getInstance().recognition(this, verifyIrisTvMsg);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        IrisManager.getInstance().cancelAction();
        SoundPlayUtil.getInstance().stop(streamId);
    }

    @Override
    public void onReceiveInfo(byte[] info) {
        Log.i(TAG, "onReceiveInfo  info:" + Arrays.toString(info));
        String id = Utils.getIrisID(info, info.length);
        if (!TextUtils.isEmpty(id)) {
            Log.i(TAG, "onReceiveInfo 识别成功 : " + id);
            if (Constants.isFirstVerify) {
                verifyPolice(Integer.parseInt(id));
            } else {
                verifySecondPolice(Integer.parseInt(id));
            }
        }
    }

    /**
     * 第一次验证警员身份
     *
     * @param id
     */
    private void verifyPolice(int id) {
        //根据id获取当前警员身份
        login.firstPolice = verifyIdentity(id);
        if (login.firstPolice != null) {
            String policeId = login.firstPolice.getId();
            String name = login.firstPolice.getName();
            Log.i(TAG, "run policeId: " + policeId + " 姓名：" + name);
            if (isDutyManager) {
                //第二个值班管理员验证身份
                Constants.isFirstVerify = false;
                if (target.equals(Constants.ACTIVITY_URGENT)) {
                    verifyIrisTvMsg.setText("请值班领导验证虹膜");
                    streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_iris);
                } else {
                    if (target.equals(Constants.ACTIVITY_EXCHANGE)) {
                        sendHandle(0, "验证成功 当前用户:" + name);
                        Intent intent = new Intent(getContext(), toClass);
                        intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
//                        intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                        getContext().startActivity(intent);
                        getActivity().finish();
                    } else {
                        verifyIrisTvMsg.setText("请下一位值班管理员验证虹膜");
                        streamId = SoundPlayUtil.getInstance().play(R.raw.duty_manager_iris);
                    }
                }
            } else {
                streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                sendHandle(0, "您没有权限 当前用户：" + name);
            }
        } else {
            Log.i(TAG, "用户不存在: ");
            sendHandle(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
        }
    }

    private void verifySecondPolice(int id) {
        login.secondPolice = verifyIdentity(id);
        if (login.secondPolice != null) {
            String secondPoliceId = login.secondPolice.getId();
            String name = login.secondPolice.getName();
            Log.i(TAG, "run policeId: " + secondPoliceId + " 姓名：" + name);
            if (target.equals(Constants.ACTIVITY_URGENT)) {
                if (isDutyLeader) {
                    sendHandle(0, "验证成功 当前用户：" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                    intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else {
                    sendHandle(0, "没有权限！当前用户：" + name);
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                }
            } else {
                if (isDutyManager) { //值班管理员
                    sendHandle(0, "验证成功 当前用户:" + name);
                    Intent intent = new Intent(getContext(), toClass);
                    intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                    intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else {
                    streamId = SoundPlayUtil.getInstance().play(R.raw.no_permission);
                    sendHandle(0, "没有权限 当前用户：" + name);
                }
            }
        } else {
            Log.i(TAG, "用户不存在: ");
            sendHandle(0, "获取用户信息失败！");
            streamId = SoundPlayUtil.getInstance().play(R.raw.usernotexist);
        }
    }

    public void sendHandle(int what, Object obj) {
        Message message = mHandler.obtainMessage(what, obj);
        message.sendToTarget();
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
                        if (deviceType == Constants.DEVICE_IRIS) { //设备类型
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
