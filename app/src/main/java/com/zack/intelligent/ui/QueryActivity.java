package com.zack.intelligent.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.R;
import com.zack.intelligent.fragment.CaptureFragment;
import com.zack.intelligent.fragment.AlarmInfoFragment;
import com.zack.intelligent.fragment.CabsInfoFragment;
import com.zack.intelligent.fragment.NormalLogFragment;
import com.zack.intelligent.fragment.OperateInfoFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 信息查询
 */
public class QueryActivity extends BaseActivity {

    private static final String TAG = "QueryActivity";
    @BindView(R.id.txt_query_gun_info)
    TextView txtQueryGunInfo;
    @BindView(R.id.txt_query_warning_log)
    TextView txtQueryWarningLog;
    @BindView(R.id.txt_query_get_back_gun_log)
    TextView txtQueryGetBackGunLog;
    @BindView(R.id.txt_query_normal_opration_log)
    TextView txtQueryNormalOprationLog;
    @BindView(R.id.img_query_gun_info)
    ImageView imgQueryGunInfo;
    @BindView(R.id.query_gun_info)
    LinearLayout queryGunInfo;
    @BindView(R.id.img_query_warning_log)
    ImageView imgQueryWarningLog;
    @BindView(R.id.query_warning_log)
    LinearLayout queryWarningLog;
    @BindView(R.id.img_query_get_back_gun_log)
    ImageView imgQueryGetBackGunLog;
    @BindView(R.id.query_get_back_gun_log)
    LinearLayout queryGetBackGunLog;
    @BindView(R.id.img_query_normal_opration_log)
    ImageView imgQueryNormalOprationLog;
    @BindView(R.id.query_normal_opration_log)
    LinearLayout queryNormalOprationLog;
    @BindView(R.id.iv_capture_image)
    ImageView ivCaptureImage;
    @BindView(R.id.tv_capture_image)
    TextView tvCaptureImage;
    @BindView(R.id.ll_capture_image)
    LinearLayout llCaptureImage;
    @BindView(R.id.query_tabs)
    LinearLayout queryTabs;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.activity_query)
    LinearLayout activityQuery;
    private List<View> viewList = new ArrayList<>();
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Unbinder bind;
    public Set<Integer> positionSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        bind = ButterKnife.bind(this);
        viewList.add(queryGunInfo);
        viewList.add(queryWarningLog);
        viewList.add(queryGetBackGunLog);
        viewList.add(queryNormalOprationLog);
        viewList.add(llCaptureImage);

        setBackgroundColorById(R.id.query_gun_info); //添加背景色

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment_content, new CabsInfoFragment());
        ft.commit();
    }

       private void setBackgroundColorById(int btnId) {
        for (View view : viewList) {
            if (view.getId() == btnId) {
                view.setBackgroundResource(R.color.bg_color);
            } else {
                view.setBackgroundResource(R.color.transparent);
            }
        }
    }

    @OnClick({R.id.query_gun_info, R.id.query_warning_log, R.id.query_get_back_gun_log,
            R.id.query_normal_opration_log, R.id.ll_capture_image, R.id.ac_top_back})
    public void onViewClicked(View view) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(view.getId());
        switch (view.getId()) {
            case R.id.query_gun_info: //枪弹信息
                ft.replace(R.id.fragment_content, new CabsInfoFragment());
                break;
            case R.id.query_warning_log: //报警日志
                ft.replace(R.id.fragment_content, new AlarmInfoFragment());
                break;
            case R.id.query_get_back_gun_log: //领还枪日志
                ft.replace(R.id.fragment_content, new OperateInfoFragment());
                break;
            case R.id.query_normal_opration_log://操作日志
                ft.replace(R.id.fragment_content, new NormalLogFragment());
                break;
            case R.id.ll_capture_image://抓拍记录
                ft.replace(R.id.fragment_content, new CaptureFragment());
                break;
            case R.id.ac_top_back:
                finish();
                break;
        }
        ft.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.e(TAG, "onRestart: " );
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.e(TAG, "onStart: " );
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.e(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.e(TAG, "onDestroy: ");
        bind.unbind();
    }

}
