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
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.ui.fragment.TempGetFragment;
import com.zack.intelligent.ui.fragment.TempInFragment;
import com.zack.intelligent.utils.SharedUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 临时存放任务
 */
public class TempStoreActivity extends BaseActivity {

    private static final String TAG = TempStoreActivity.class.getSimpleName();
    @BindView(R.id.temp_btn_in_gun)
    TextView tempBtnInGun;
    @BindView(R.id.temp_btn_get_gun)
    TextView tempBtnGetGun;
    @BindView(R.id.temp_store_content)
    FrameLayout tempStoreContent;
    @BindView(R.id.temp_store_ll_tittle)
    LinearLayout tempStoreLlTittle;
    private boolean isStore = false;
    private MembersBean manager1, manager2;
    private List<View> viewList = new ArrayList<>();
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_store);
        ButterKnife.bind(this);
        try {
            String firstPoliceInfo = getIntent().getStringExtra("firstPoliceInfo");//获取值班管理员id
            String secondPoliceInfo = getIntent().getStringExtra("secondPoliceInfo");//获取值班管理员id
            //获取管理员1信息
            if (!TextUtils.isEmpty(firstPoliceInfo)) {
                manager1 = JSON.parseObject(firstPoliceInfo, MembersBean.class);
            }
            //获取管理员2信息
            if (!TextUtils.isEmpty(secondPoliceInfo)) {
                manager2 = JSON.parseObject(secondPoliceInfo, MembersBean.class);
            }

            //添加操作日志
            if(manager1 !=null && manager2!=null){
                GreendaoMg.addNormalLog(manager1,
                        6, 7, "【" + manager1.getName()
                                + "】和【" + manager2.getName() + "】进入【临时存放枪支】");
            }

            viewList.add(tempBtnInGun);
            viewList.add(tempBtnGetGun);

            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            setBackgroundColorById(R.id.temp_btn_in_gun);
            ft.replace(R.id.temp_store_content, new TempInFragment());
            ft.commit();

            SharedUtils.setOpenCapture(true);
            Constants.isCapture = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MembersBean getManager1(){
        return manager1;
    }

    public MembersBean getManager2(){
        return manager2;
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

    @OnClick({R.id.ac_top_back, R.id.temp_btn_in_gun, R.id.temp_btn_get_gun})
    public void onViewClicked(View view) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(view.getId());
        switch (view.getId()) {
            case R.id.ac_top_back:
                finish();
                break;
            case R.id.temp_btn_in_gun: //临时存放枪支
                ft.replace(R.id.temp_store_content, new TempInFragment());
                break;
            case R.id.temp_btn_get_gun://存放枪支领取
                ft.replace(R.id.temp_store_content, new TempGetFragment());
                break;
        }
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GreendaoMg.addNormalLog(manager1,
                    6, 8, "【" + manager1.getName()
                            + "】和【" + manager2.getName() + "】退出【临时存放枪支】");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
