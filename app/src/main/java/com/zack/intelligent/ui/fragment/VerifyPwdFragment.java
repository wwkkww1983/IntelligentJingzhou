package com.zack.intelligent.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.BackActivity;
import com.zack.intelligent.ui.GetActivity;
import com.zack.intelligent.ui.InStoreActivity;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.LoginActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.SettingsActivity;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 验证指纹
 */
public class VerifyPwdFragment extends Fragment {
    private static final String TAG = "VerifyPwdFragment";

    @BindView(R.id.edt_user_name)
    EditText edtUserName;
    @BindView(R.id.ll_user_name)
    LinearLayout llUserName;
    @BindView(R.id.edt_user_pwd)
    EditText edtUserPwd;
    @BindView(R.id.open_line_pwd)
    LinearLayout openLinePwd;
    @BindView(R.id.open_btn_login)
    Button openBtnLogin;
    @BindView(R.id.open_btn_cancel)
    Button openBtnCancel;
    @BindView(R.id.open_ll_confirm)
    LinearLayout openLlConfirm;
    Unbinder unbinder;
    @BindView(R.id.verify_pwd_tv_user)
    TextView verifyPwdTvUser;
    private int streamId;
    private String target;
    private Class<?> toClass;
    private List<MembersBean> membersBeanList;
    private LoginActivity login;

    public VerifyPwdFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        login = (LoginActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_pwd, container, false);
        unbinder = ButterKnife.bind(this, view);
        target = getActivity().getIntent().getStringExtra("activity");
        if (!TextUtils.isEmpty(target)) {
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
                case Constants.ACTIVITY_IN_STORE://枪弹入库
                    toClass = InStoreActivity.class;
                    break;
                case Constants.ACTIVITY_USER://人员管理
                    toClass = UserActivity.class;
                    break;
                case Constants.ACTIVITY_SETTING://人员管理
                    toClass = SettingsActivity.class;
                    break;
            }
        }
        if (Constants.isFirstVerify) {
            //第一次验证
            //验证值班管理员指纹
            streamId = SoundPlayUtil.getInstance().play(R.raw.user_pwd_login);
            verifyPwdTvUser.setText("请输入警号和密码验证");
        } else {
            //第二次验证
            if (target.equals(Constants.ACTIVITY_URGENT)) { //紧急领枪
                //验证值班领导指纹
                streamId = SoundPlayUtil.getInstance().play(R.raw.user_pwd_login);
                verifyPwdTvUser.setText("请输入警号和密码验证");
            } else { //非紧急领枪
                //验证值班管理员指纹
                streamId = SoundPlayUtil.getInstance().play(R.raw.user_pwd_login);
                verifyPwdTvUser.setText("请第二位值班人员输入警号和密码验证");
            }
        }
        streamId = SoundPlayUtil.getInstance().play(R.raw.user_pwd_login);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        SoundPlayUtil.getInstance().stop(streamId);
    }

    @OnClick({R.id.open_btn_login, R.id.open_btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_btn_login:
                loginVerify();
                break;
            case R.id.open_btn_cancel:
                getActivity().finish();//返回
                break;
        }
    }

    /**
     * 登入确认
     */
    private void loginVerify() {
        final String policeNo = edtUserName.getText().toString();
        final String password = edtUserPwd.getText().toString();
        if (TextUtils.isEmpty(policeNo)) {
            Log.i(TAG, "onClick 警号为空: ");
            streamId = SoundPlayUtil.getInstance().play(R.raw.user_name_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Log.i(TAG, "onClick 密码为空: ");
            streamId = SoundPlayUtil.getInstance().play(R.raw.password_null);
            return;
        }

        userLogin(policeNo, password);
    }

    /**
     * 验证警号和密码是否正确
     *
     * @param policeNo
     * @param password
     */
    private void userLogin(String policeNo, String password) {
        HttpClient.getInstance().userLogin2(getContext(), policeNo, password, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "userLogin onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String body = dataBean.getBody();
//                    if (true) {
                        if (!TextUtils.isEmpty(body)) {
                            MembersBean membersBean = JSON.parseObject(body, MembersBean.class);
                            if (membersBean != null) {
                                int policeType = membersBean.getPoliceType();
                                if (Constants.isFirstVerify) {
                                    login.firstPolice =membersBean;
                                    if (target.equals(Constants.ACTIVITY_SETTING) ||
                                            target.equals(Constants.ACTIVITY_USER)) {
                                        if (policeType == Constants.POLICE_TYPE_ADMIN) { //超级管理员
                                            //值班领导验证指纹
                                            verifyPwdTvUser.setText("验证成功");
                                            edtUserPwd.setText("");
                                            edtUserName.setText("");
//                                Constants.isFirstVerify = true;
                                            Intent intent = new Intent(getContext(), toClass);
                                            intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                                            getActivity().startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            verifyPwdTvUser.setText("您没有权限，请重新输入");
                                            edtUserPwd.setText("");
                                            edtUserName.setText("");
                                        }
                                    } else if (target.equals(Constants.ACTIVITY_URGENT)) {
                                        //值班领导验证指纹
                                        verifyPwdTvUser.setText("请输入警号和密码验证");
                                        edtUserPwd.setText("");
                                        edtUserName.setText("");
                                        Constants.isFirstVerify = false;
                                    } else {
                                        //值班管理员验证指纹
                                        verifyPwdTvUser.setText("请第二位警员输入警号和密码验证");
                                        edtUserPwd.setText("");
                                        edtUserName.setText("");
                                        Constants.isFirstVerify = false;
                                    }
                                } else {
                                    login.secondPolice =membersBean;
                                    if (target.equals(Constants.ACTIVITY_URGENT)) {
                                        if (policeType == Constants.POLICE_TYPE_LEADER) {
                                            //值班领导验证指纹
                                            verifyPwdTvUser.setText("警号和密码验证成功");
//                               streamId = SoundPlayUtil.getInstance().play(R.raw.duty_leader_finger);
                                            edtUserPwd.setText("");
                                            edtUserName.setText("");
                                            Constants.isFirstVerify = true;
                                            Intent intent = new Intent(getContext(), toClass);
                                            intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                                            intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                                            getActivity().startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            verifyPwdTvUser.setText("您没有权限，请重新输入");
                                            edtUserPwd.setText("");
                                            edtUserName.setText("");
                                        }
                                    } else {
                                        //值班管理员验证指纹
                                        if (policeType == Constants.POLICE_TYPE_MANAGER) {
                                            verifyPwdTvUser.setText("警号和密码验证成功");
                                            edtUserPwd.setText("");
                                            edtUserName.setText("");
                                            Constants.isFirstVerify = true;
                                            Intent intent = new Intent(getContext(), toClass);
                                            intent.putExtra("firstPoliceInfo", JSON.toJSONString(login.firstPolice));
                                            intent.putExtra("secondPoliceInfo", JSON.toJSONString(login.secondPolice));
                                            getActivity().startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            verifyPwdTvUser.setText("您没有权限，请重新输入");
                                            edtUserPwd.setText("");
                                            edtUserName.setText("");
                                        }
                                    }
                                }
                            } else {
                                verifyPwdTvUser.setText("验证警号和密码失败");
                            }
                        } else {
                            verifyPwdTvUser.setText("警号或密码输入错误");
                        }
//                    } else {
//                        verifyPwdTvUser.setText("警号或密码输入错误");
////                        edtUserPwd.setText("");
////                        edtUserName.setText("");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "userLogin onFailed: " + response.getException().getMessage());
            }
        });
    }

    public void setPoliceData(List<MembersBean> policeList) {
        this.membersBeanList = policeList;
        Log.i(TAG, "setPoliceData memberList size: " + membersBeanList.size());
    }

}
