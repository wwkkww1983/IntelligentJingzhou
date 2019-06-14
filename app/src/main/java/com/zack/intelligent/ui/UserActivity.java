package com.zack.intelligent.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.UserListAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.dialog.FaceDialog;
import com.zack.intelligent.ui.dialog.FingerDialog;
import com.zack.intelligent.ui.dialog.IrisDialog;
import com.zack.intelligent.utils.SharedUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserActivity extends BaseActivity {
    private static final String TAG = "UserActivity";

    @BindView(R.id.user_lv_list)
    ListView userLvList;
    @BindView(R.id.user_btn_finger)
    Button userBtnFinger;
    @BindView(R.id.user_btn_vein)
    Button userBtnVein;
    @BindView(R.id.user_btn_iris)
    Button userBtnIris;
    @BindView(R.id.user_btn_face)
    Button userBtnFace;

    public List<MembersBean> policeList = new ArrayList<>();
    public MembersBean curPolice;
    public UserListAdapter adapter;
    public int selectedPosition;
    @BindView(R.id.user_btn_pre_page)
    Button userBtnPrePage;
    @BindView(R.id.user_tv_cur_page)
    TextView userTvCurPage;
    @BindView(R.id.user_btn_next_page)
    Button userBtnNextPage;
    private List<PoliceBiosBean> policeBiosBeanList = new ArrayList<>();
    private int index = 0;
    private int pageCount = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        userBtnVein.setVisibility(View.GONE);
        if(!SharedUtils.getIrisOpen()){
            userBtnIris.setVisibility(View.GONE);
        }

        if (!SharedUtils.getFingerOpen()) {
            userBtnFinger.setVisibility(View.GONE);
        }
        if (!SharedUtils.getFaceOpen()) {
            userBtnFace.setVisibility(View.GONE);
        }

        userBtnPrePage.setVisibility(View.INVISIBLE);
        userBtnNextPage.setVisibility(View.INVISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getPoliceList();
            }
        }).start();

        userLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "onItemClick current police: " + JSON.toJSONString(policeList.get(position)));
                //设置警员信息和指纹
                selectedPosition = position;
                curPolice = policeList.get(position);
            }
        });
    }

    private void initPreNextBtn() {
        if (policeList.isEmpty()) {
            userTvCurPage.setText(index + 1 + "/1");
        } else {
            if (policeList.size() <= pageCount) {
                userBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                userBtnNextPage.setVisibility(View.VISIBLE);
            }
            userTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) policeList.size() / pageCount));
        }
    }

    private void getPoliceList() {
        HttpClient.getInstance().getPoliceList(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getPoliceList onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    if (dataBean != null) {
                        boolean success = dataBean.isSuccess();
                        if (success) {
                            String body = dataBean.getBody();
                            policeList = JSON.parseArray(body, MembersBean.class);
                            adapter = new UserListAdapter(policeList, index, pageCount);
                            userLvList.setAdapter(adapter);
                            initPreNextBtn();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error: " + response.getException().getMessage());
            }
        });
    }

    @OnClick({R.id.user_btn_finger, R.id.user_btn_vein, R.id.user_btn_iris, R.id.user_btn_face,
            R.id.ac_top_back, R.id.user_btn_pre_page, R.id.user_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_btn_finger://指纹
                if (curPolice == null) {
                    return;
                }
                getPoliceBiosList();
                FingerDialog fingerDialog = new FingerDialog(UserActivity.this, curPolice, policeBiosBeanList);
                fingerDialog.show();
                break;
            case R.id.user_btn_iris://虹膜
                if (curPolice == null) {
                    return;
                }
                getPoliceBiosList();
                IrisDialog irisDialog = new IrisDialog(UserActivity.this, curPolice, policeBiosBeanList);
                irisDialog.show();
                break;
            case R.id.user_btn_face://人脸
                if (curPolice == null) {
                    return;
                }
                getPoliceBiosList();
                FaceDialog faceDialog = new FaceDialog(UserActivity.this, curPolice, policeBiosBeanList);
                faceDialog.show();
                break;
            case R.id.ac_top_back:
                finish();
                break;
            case R.id.user_btn_pre_page://上一页
                prePager();
                break;
            case R.id.user_btn_next_page://下一页
                nexPager();
                break;
        }
    }

    /**
     * 上一页
     */
    private void prePager() {
        userLvList.setAdapter(adapter);
        userLvList.setSelection(0);
        curPolice = null;
        index--;
        adapter.setIndex(index);
        userTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) policeList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
    }

    /**
     * 下一页
     */
    private void nexPager() {
        userLvList.setAdapter(adapter);
        userLvList.setSelection(0);
        curPolice = null;
        index++;
        adapter.setIndex(index);
        userTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) policeList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
    }

    /**
     * button显示和隐藏
     */
    private void checkButton() {
        if (index <= 0) {
            userBtnPrePage.setVisibility(View.INVISIBLE);
            userBtnNextPage.setVisibility(View.VISIBLE);
        } else if (policeList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            userBtnPrePage.setVisibility(View.VISIBLE);
            userBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            userBtnNextPage.setVisibility(View.VISIBLE);
            userBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取用户生物特征数据
     */
    public void getPoliceBiosList() {
        policeBiosBeanList.clear();
        if (!policeList.isEmpty()) {
            for (int i = 0; i < policeList.size(); i++) {
                MembersBean membersBean = policeList.get(i);
                List<PoliceBiosBean> policeBios = membersBean.getPoliceBios();
                policeBiosBeanList.addAll(policeBios);
            }
        }
    }

}
