package com.zack.intelligent.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.ui.dialog.InitDialog;
import com.zack.intelligent.ui.fragment.KeepGetFragment;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.ui.fragment.KeepBackFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 枪支保养
 */

public class KeepActivity extends BaseActivity {

    private static final String TAG = KeepActivity.class.getSimpleName();
    @BindView(R.id.keep_btn_get_gun)
    TextView keepBtnGetGun;
    @BindView(R.id.keep_btn_back_gun)
    TextView keepBtnBackGun;
    @BindView(R.id.keep_ll_tittle)
    LinearLayout keepLlTittle;
    @BindView(R.id.keep_content)
    FrameLayout keepContent;
    private MembersBean firstManage;
    private MembersBean secondManage;
    private List<GunsBean> gunsBeanList = new ArrayList<>();
    private List<OperBean> operList = new ArrayList<>();

    private List<View> viewList = new ArrayList<>();
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_keep);
        ButterKnife.bind(this);

        String firstPoliceInfo = getIntent().getStringExtra("firstPoliceInfo");
        String secondPoliceInfo = getIntent().getStringExtra("secondPoliceInfo");
        if (!TextUtils.isEmpty(firstPoliceInfo)) {
            firstManage = JSON.parseObject(firstPoliceInfo, MembersBean.class);
        }

        if (!TextUtils.isEmpty(secondPoliceInfo)) {
            secondManage = JSON.parseObject(secondPoliceInfo, MembersBean.class);
        }

        try {
            GreendaoMg.addNormalOperateLog(firstManage, 4, 7,
                    "【" + firstManage.getName() + "】进入【枪支保养】");
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewList.add(keepBtnGetGun);
        viewList.add(keepBtnBackGun);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(R.id.keep_btn_get_gun);
        ft.replace(R.id.keep_content, new KeepGetFragment());
        ft.commit();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GreendaoMg.addNormalOperateLog(firstManage, 4, 8,
                    "【" + firstManage.getName() + "】退出【枪支保养】");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.keep_btn_get_gun, R.id.keep_btn_back_gun, R.id.ac_top_back})
    public void onViewClicked(View view) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(view.getId());
        switch (view.getId()) {
            case R.id.keep_btn_get_gun: //领取枪支
                ft.replace(R.id.keep_content, new KeepGetFragment());
                break;
            case R.id.keep_btn_back_gun://归还枪支
                ft.replace(R.id.keep_content, new KeepBackFragment());
                break;
            case R.id.ac_top_back:
                finish();
                break;
        }
        ft.commit();
    }
}
