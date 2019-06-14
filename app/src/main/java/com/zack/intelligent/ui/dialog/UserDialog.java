package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.BackActivity;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.MainActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户名和密码登录窗
 */

public class UserDialog extends Dialog {
    private static final String TAG = "UserDialog";
    private static final int TASK_GO = 1;
    private static final int TASK_BACK = 2;
    private static final int TASK_URGENT = 3;
    private static final int TASK_KEEP = 4;
    private static final int TASK_SCRAP = 5;
    private static final int TASK_TEMPSTORE = 6;
    private static final int TASK_EXCHANGE = 7;
    private static final int TASK_APPLY = 8;
    private static final int TASK_FINGER = 9;
    private static final int TASK_VEIN = 10;
    private static final int TASK_IRIS = 11;
    private static final int TASK_MAIN = 12;
    private static final int TASK_USER = 12;
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
    private Context mContext;
    private Class<?> toClass;
    private int classId;
    private int streamId;

    public UserDialog(@NonNull Context context, Class<?> clazz) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.toClass = clazz;
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.super_user_dialog);
        ButterKnife.bind(this);
        if (toClass.equals(BackActivity.class)) { //出警
            LogUtil.i(TAG, "还枪: ");
            classId = TASK_BACK;
        } else if (toClass.equals(UrgentGoActivity.class)) { //紧急出警
            LogUtil.i(TAG, "紧急出警: ");
            classId = TASK_URGENT;
        } else if (toClass.equals(KeepActivity.class)) { //枪弹保养
            LogUtil.i(TAG, "枪弹保养: ");
            classId = TASK_KEEP;
        } else if (toClass.equals(ScrapActivity.class)) { //枪弹报废
            LogUtil.i(TAG, "枪弹报废: ");
            classId = TASK_SCRAP;
        } else if (toClass.equals(TempStoreActivity.class)) { //临时存放
            LogUtil.i(TAG, "临时存放: ");
            classId = TASK_TEMPSTORE;
        } else if (toClass.equals(MainActivity.class)) {
            LogUtil.i(TAG, "主界面: ");
            classId = TASK_MAIN;
        } else if (toClass.equals(ExchangeActivity.class)) {
            LogUtil.i(TAG, "值班交接: ");
            classId = TASK_EXCHANGE;
        } else if (toClass.equals(UserActivity.class)) {
            LogUtil.i(TAG, "用户管理: ");
            classId = TASK_USER;
        }

        streamId = SoundPlayUtil.getInstance().play(R.raw.user_pwd_login);
//        edtUserName.setText("admin");
//        edtUserPwd.setText("123456");
    }

    /**
     * 登入确认
     */
    private void loginVerify() {
        final String name = edtUserName.getText().toString();
        String password = edtUserPwd.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Log.i(TAG, "onClick 用户名为空: ");
            streamId = SoundPlayUtil.getInstance().play(R.raw.user_name_null);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Log.i(TAG, "onClick 密码为空: ");
            streamId = SoundPlayUtil.getInstance().play(R.raw.password_null);
            return;
        }
        HttpClient.getInstance().userLogin(getContext(), name, password,
                new HttpListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) throws JSONException {
                        Log.i(TAG, "userLogin onSucceed response: " + response.get());
                        try {
                            DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                            boolean success = dataBean.isSuccess();
                            if (success) {
                                ToastUtil.showShort("登录成功");
                                Intent intent = new Intent(mContext, toClass);
                                intent.putExtra("user_name", name);
                                mContext.startActivity(intent);
                                UserDialog.this.dismiss();
                            } else {
                                ToastUtil.showShort("登录失败");
                            }
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

    @Override
    public void dismiss() {
        super.dismiss();
        SoundPlayUtil.getInstance().stop(streamId);
    }

    @OnClick({R.id.open_btn_login, R.id.open_btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_btn_login:
                loginVerify();
                break;
            case R.id.open_btn_cancel:
                UserDialog.this.dismiss();
                break;
        }
    }
}
