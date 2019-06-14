package com.zack.intelligent.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.intelligent.Constants;
import com.zack.intelligent.ui.fragment.AboutFragment;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.R;
import com.zack.intelligent.fragment.BasicFragment;
import com.zack.intelligent.fragment.LockFragment;
import com.zack.intelligent.fragment.MusicFragment;
import com.zack.intelligent.fragment.OtherFragment;
import com.zack.intelligent.fragment.SerialFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 系统配置
 * 1.
 */

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";
    @BindView(R.id.txt_setup_music)
    LinearLayout txtSetupMusic;
    @BindView(R.id.txt_setup_lock)
    LinearLayout txtSetupLock;
    @BindView(R.id.txt_setup_serial)
    LinearLayout txtSetupSerial;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.txt_setup_item_basic)
    LinearLayout txtSetupItemBasic;
    @BindView(R.id.txt_setup_system_info)
    LinearLayout txtSetupSystemInfo;
    @BindView(R.id.txt_setup_other)
    LinearLayout txtSetupOther;

    private List<View> viewList = new ArrayList<>();
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        ButterKnife.bind(this);
        viewList.add(txtSetupItemBasic);
        viewList.add(txtSetupMusic);
        viewList.add(txtSetupLock);
        viewList.add(txtSetupSerial);
        viewList.add(txtSetupSystemInfo);
        viewList.add(txtSetupOther);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(R.id.txt_setup_item_basic);
        ft.replace(R.id.fragment_content, new BasicFragment());
        ft.commit();
    }

    private void setBackgroundColorById(int btnId) {
        for (View view : viewList) {
            if (view.getId() == btnId) {
                view.setBackgroundResource(R.color.bg_blue);
            } else {
                view.setBackgroundResource(R.color.transparent);
            }
        }
    }

    @OnClick({R.id.txt_setup_item_basic, R.id.txt_setup_music, R.id.txt_setup_lock,
            R.id.txt_setup_serial, R.id.txt_setup_system_info, R.id.txt_setup_other,
            R.id.ac_top_back})
    public void onViewClicked(View view) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        setBackgroundColorById(view.getId());
        switch (view.getId()) {
            case R.id.txt_setup_item_basic:
                ft.replace(R.id.fragment_content, new BasicFragment());
                break;
            case R.id.txt_setup_music:
                ft.replace(R.id.fragment_content, new MusicFragment());
                break;
            case R.id.txt_setup_lock:
                if (Constants.isDebug) {
                    ft.replace(R.id.fragment_content, new LockFragment());
                }
                break;
            case R.id.txt_setup_serial:
                ft.replace(R.id.fragment_content, new SerialFragment());
                break;
            case R.id.txt_setup_other:
                ft.replace(R.id.fragment_content, new OtherFragment());
                break;
            case R.id.txt_setup_system_info:
                ft.replace(R.id.fragment_content, new AboutFragment());
                break;
            case R.id.ac_top_back:
                finish();
                break;
            default:
                break;
        }
        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.isDebug = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
