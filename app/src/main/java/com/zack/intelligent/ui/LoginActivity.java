package com.zack.intelligent.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.fragment.VerifyFaceFragment;
import com.zack.intelligent.ui.fragment.VerifyFingerFragment;
import com.zack.intelligent.ui.fragment.VerifyIrisFragment;
import com.zack.intelligent.ui.fragment.VerifyPwdFragment;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SharedUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择多方式登录界面
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.login_tv_tittle)
    TextView loginTvTittle;
    @BindView(R.id.login_content)
    FrameLayout loginContent;
    @BindView(R.id.login_btn_finger_verify)
    Button loginBtnFingerVerify;
    @BindView(R.id.login_btn_vein_verify)
    Button loginBtnVeinVerify;
    @BindView(R.id.login_btn_iris_verify)
    Button loginBtnIrisVerify;
    @BindView(R.id.login_btn_face_verify)
    Button loginBtnFaceVerify;
    @BindView(R.id.login_btn_password_verify)
    Button loginBtnPasswordVerify;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private List<View> viewList;
    private ExecutorService mExecutorService;
    private List<MembersBean> policeList;
    private MembersBean currentLeader;
    private List<MembersBean> currentManagers;
    private VerifyFingerFragment verifyFingerFragment;
    private VerifyFaceFragment verifyFaceFragment;
    private VerifyIrisFragment verifyIrisFragment;
    private VerifyPwdFragment verifyPwdFragment;
    public MembersBean firstPolice, secondPolice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        viewList = new ArrayList<>();
        policeList = new ArrayList<>();

        if(!SharedUtils.getVeinOpen()){
            loginBtnVeinVerify.setVisibility(View.GONE);
        }
        if(!SharedUtils.getIrisOpen()){
            loginBtnIrisVerify.setVisibility(View.GONE);
        }
        if(!SharedUtils.getFaceOpen()){
            loginBtnFaceVerify.setVisibility(View.GONE);
        }

        viewList.add(loginBtnFingerVerify);
        viewList.add(loginBtnFaceVerify);
        viewList.add(loginBtnIrisVerify);
        viewList.add(loginBtnPasswordVerify);

        verifyFingerFragment = new VerifyFingerFragment();
        verifyFaceFragment = new VerifyFaceFragment();
        verifyIrisFragment = new VerifyIrisFragment();
        verifyPwdFragment = new VerifyPwdFragment();

        setBackgroundColorById(R.id.login_btn_finger_verify);
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        transaction.replace(R.id.login_content, verifyFingerFragment);
        transaction.commit();

        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getPolices();
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
    }

    /**
     * 获取所有用户
     */
    private void getPolices() {
        HttpClient.getInstance().getPoliceList(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getPolice onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    String msg = dataBean.getMsg();
                    String body = dataBean.getBody();
                    policeList = JSON.parseArray(body, MembersBean.class);
                    Log.i(TAG, "onSucceed policeList size: " + policeList.size());
                    verifyFingerFragment.setPoliceData(policeList);
                    verifyFaceFragment.setPoliceData(policeList);
                    verifyIrisFragment.setPoliceData(policeList);
                    verifyPwdFragment.setPoliceData(policeList);
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
        HttpClient.getInstance().getCurrentDuty(this, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                Log.i(TAG, "get leaders onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<MembersBean> leaders = JSON.parseArray(body, MembersBean.class);
                            if (leaders != null && !leaders.isEmpty()) {
                                currentLeader = leaders.get(0);
                                verifyFingerFragment.setLeaderData(currentLeader);
                                verifyFaceFragment.setLeaderData(currentLeader);
                                verifyIrisFragment.setLeaderData(currentLeader);
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
        HttpClient.getInstance().getCurrentDuty(this, 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                Log.i(TAG, "getManagers onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            currentManagers = JSON.parseArray(body, MembersBean.class);
                            verifyFingerFragment.setManagerData(currentManagers);
                            verifyFaceFragment.setManagerData(currentManagers);
                            verifyIrisFragment.setManagerData(currentManagers);
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

    /**
     * 设置item背景颜色
     * * @param btnId
     */
    private void setBackgroundColorById(int btnId) {
        for (View view : viewList) {
            if (view.getId() == btnId) {
                view.setBackgroundResource(R.color.bg_color);
            } else {
                view.setBackgroundResource(R.color.bg_blue);
            }
        }
    }

    @OnClick({R.id.login_btn_finger_verify, R.id.login_btn_vein_verify, R.id.login_btn_iris_verify,
            R.id.login_btn_face_verify, R.id.login_btn_password_verify, R.id.ac_top_back})
    public void onViewClicked(View view) {
        setBackgroundColorById(view.getId());
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        switch (view.getId()) {
            case R.id.login_btn_finger_verify://指纹验证
                transaction.replace(R.id.login_content, verifyFingerFragment);
                break;
            case R.id.login_btn_face_verify: //人脸验证
                transaction.replace(R.id.login_content, verifyFaceFragment);
                break;
            case R.id.login_btn_iris_verify: //虹膜验证
                transaction.replace(R.id.login_content, verifyIrisFragment);
                break;
            case R.id.login_btn_password_verify: //用户名密码验证
                transaction.replace(R.id.login_content, verifyPwdFragment);
                break;
            case R.id.ac_top_back: //返回
                finish();
                break;
        }
        transaction.commit();
    }

    public List<MembersBean> getCurrentManagers() {
        LogUtil.i(TAG, "getCurrentManagers currentManagers: " + JSON.toJSONString(currentManagers));
        return currentManagers;
    }

    public MembersBean getCurrentLeader() {
        LogUtil.i(TAG, "getCurrentLeader currentLeader: " + JSON.toJSONString(currentLeader));
        return currentLeader;
    }

    public List<MembersBean> getMembersBeanList() {
        LogUtil.i(TAG, "getMembersBeanList policeList: " + JSON.toJSONString(policeList));
        return policeList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isFirstVerify = true;
    }
}
