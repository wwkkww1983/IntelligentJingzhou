package com.zack.intelligent.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class IntegratedActivity extends BaseActivity {
    private static final String TAG = "IntegratedActivity";

    @BindView(R.id.ac_main_query_btn)
    TextView acMainQueryBtn;
    @BindView(R.id.ll_root_view)
    LinearLayout llRootView;
    @BindView(R.id.ac_main_keep_btn)
    TextView acMainKeepBtn;
    @BindView(R.id.ac_main_scrap_btn)
    TextView acMainScrapBtn;
    @BindView(R.id.ac_main_temp_btn)
    TextView acMainTempBtn;
    @BindView(R.id.ac_main_user_btn)
    TextView acMainUserBtn;
    @BindView(R.id.ac_main_btn_in_store)
    TextView acMainBtnInStore;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrated);
        bind = ButterKnife.bind(this);

        acTopSetting.setVisibility(View.GONE);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageSubscrib(MessageEvent event) {
        Log.i(TAG, "messageSubscrib message: " + event.getMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.ac_main_query_btn, R.id.ac_main_keep_btn, R.id.ac_main_scrap_btn,
            R.id.ac_main_temp_btn, R.id.ac_main_user_btn, R.id.ac_top_back, R.id.ac_main_btn_in_store})
    public void onViewClicked(View view) {
        millisInFuture = 60;
        Intent intent;
        switch (view.getId()) {
            case R.id.ac_top_back:
                finish();
                break;
            case R.id.ac_main_query_btn: //查询
                intent = new Intent(IntegratedActivity.this, QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.ac_main_keep_btn: //枪支保养
                if (!Constants.isDebug) {
                    intent = new Intent(IntegratedActivity.this, LoginActivity.class);
                    intent.putExtra("activity", Constants.ACTIVITY_KEEP);
                    startActivity(intent);
                } else {
                    intent = new Intent(IntegratedActivity.this, KeepActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ac_main_scrap_btn://枪支报废
                intent = new Intent(IntegratedActivity.this, LoginActivity.class);
                intent.putExtra("activity", Constants.ACTIVITY_SCRAP);
                startActivity(intent);
                break;
            case R.id.ac_main_temp_btn:
                intent = new Intent(IntegratedActivity.this, LoginActivity.class);
                intent.putExtra("activity", Constants.ACTIVITY_TEMP_STORE);
                startActivity(intent);
                break;
            case R.id.ac_main_user_btn: //用户管理
                intent = new Intent(IntegratedActivity.this, LoginActivity.class);
                intent.putExtra("activity", Constants.ACTIVITY_USER);
                startActivity(intent);
                break;
            case R.id.ac_main_btn_in_store: //枪弹入库
                intent = new Intent(IntegratedActivity.this, LoginActivity.class);
                intent.putExtra("activity", Constants.ACTIVITY_IN_STORE);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }

}
