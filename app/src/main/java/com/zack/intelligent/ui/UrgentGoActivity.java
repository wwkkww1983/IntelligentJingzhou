package com.zack.intelligent.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.ui.fragment.UrgentBackFragment;
import com.zack.intelligent.ui.fragment.UrgentGetFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 紧急出警
 */

public class UrgentGoActivity extends BaseActivity {

    private static final String TAG = UrgentGoActivity.class.getSimpleName();
    @BindView(R.id.urgent_btn_get)
    TextView urgentBtnGet;
    @BindView(R.id.urgent_btn_back)
    TextView urgentBtnBack;
    @BindView(R.id.urgent_content)
    FrameLayout urgentContent;
    private List<View> viewList = new ArrayList<>();
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MembersBean manage, leader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent);
        ButterKnife.bind(this);
        ac_top_back.setVisibility(View.GONE);
        try {
            String firstPoliceInfo = getIntent().getStringExtra("firstPoliceInfo"); //管理员id
            String secondPoliceInfo = getIntent().getStringExtra("secondPoliceInfo"); //领导id

            if (!TextUtils.isEmpty(firstPoliceInfo)) {
                manage = JSON.parseObject(firstPoliceInfo, MembersBean.class);
            }

            if (!TextUtils.isEmpty(secondPoliceInfo)) {
                leader = JSON.parseObject(secondPoliceInfo, MembersBean.class);
            }

            GreendaoMg.addNormalOperateLog(manage,
                    2, 7, "【"
                            + manage.getName() + "和" + leader.getName() + "】进入紧急领枪");
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewList.add(urgentBtnGet);
        viewList.add(urgentBtnBack);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(R.id.urgent_btn_get);
        ft.replace(R.id.urgent_content, new UrgentGetFragment());
        ft.commit();
    }

    private void setBackgroundColorById(int btnId) {
        for (View view : viewList) {
            if (view.getId() == btnId) {
                view.setBackgroundResource(R.color.simple_blue);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GreendaoMg.addNormalOperateLog(manage,
                    2, 8, "【"
                            + manage.getName() + "和" + leader.getName() + "】退出紧急领枪");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.urgent_btn_get, R.id.urgent_btn_back})
    public void onViewClicked(View view) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(view.getId());
        switch (view.getId()) {
            case R.id.urgent_btn_get: //紧急领枪弹
                ft.replace(R.id.urgent_content, new UrgentGetFragment());
                break;
            case R.id.urgent_btn_back://归还枪弹
                ft.replace(R.id.urgent_content, new UrgentBackFragment());
                break;
        }
        ft.commit();
    }

    public MembersBean getManageData() {
        return manage;
    }

    public MembersBean getLeaderData() {
        return leader;
    }
}
