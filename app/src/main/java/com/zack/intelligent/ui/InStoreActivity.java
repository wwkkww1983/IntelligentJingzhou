package com.zack.intelligent.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.InStoreAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.StoreBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 枪弹入库
 */
public class InStoreActivity extends BaseActivity {
    private static final String TAG = "InStoreActivity";
    @BindView(R.id.store_in_recycler_view)
    RecyclerView storeInRecyclerView;
    @BindView(R.id.store_in_btn_open_door)
    Button storeInBtnOpenDoor;
    @BindView(R.id.store_in_btn_open_lock)
    Button storeInBtnOpenLock;
    @BindView(R.id.store_in_btn_ok)
    Button storeInBtnOk;
    @BindView(R.id.store_in_ll)
    LinearLayout storeInLl;
    @BindView(R.id.in_store_btn_pre_page)
    Button inStoreBtnPrePage;
    @BindView(R.id.in_store_tv_cur_page)
    TextView inStoreTvCurPage;
    @BindView(R.id.in_store_btn_next_page)
    Button inStoreBtnNextPage;
    private List<StoreBean> storeBeanList;
    private InStoreAdapter inStoreAdapter;
    private int index = 0;
    private int pageCount = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_store);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        inStoreBtnPrePage.setVisibility(View.INVISIBLE);
        inStoreBtnNextPage.setVisibility(View.INVISIBLE);
        storeBeanList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayout.VERTICAL, false);
        storeInRecyclerView.setLayoutManager(llm);
        inStoreAdapter = new InStoreAdapter(storeBeanList, index, pageCount);
        storeInRecyclerView.setAdapter(inStoreAdapter);
    }

    private void initData() {
        HttpClient.getInstance().getStoreTask(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "onSucceed  response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                if (dataBean != null) {
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<StoreBean> storeBeans = JSON.parseArray(body, StoreBean.class);
                            for (StoreBean storeBean : storeBeans) {
                                String robarkId = storeBean.getRobarkId();
                                String gunCabId = SharedUtils.getGunCabId();
                                if (robarkId.equals(gunCabId)) {
                                    storeBeanList.add(storeBean);
                                }
                            }
                            inStoreAdapter.setList(storeBeanList);
                            initPreNextBtn();
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    private void initPreNextBtn() {
        if (storeBeanList.isEmpty()) {
            inStoreTvCurPage.setText(index + 1 + "/1");
        } else {
            if (storeBeanList.size() <= pageCount) {
                inStoreBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                inStoreBtnNextPage.setVisibility(View.VISIBLE);
            }
            inStoreTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) storeBeanList.size() / pageCount));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.store_in_btn_open_door, R.id.store_in_btn_open_lock, R.id.store_in_btn_ok,
            R.id.ac_top_back, R.id.in_store_btn_pre_page, R.id.in_store_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.store_in_btn_open_door:
                switch (SharedUtils.getGunCabType()) {
                    case 0:
                    case 1:
                    case 2:
                        SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                        break;
                    case 3:  //综合柜
                        boolean isGun = false;
                        boolean isAmmo = false;
                        List<StoreBean> checkedList = inStoreAdapter.getCheckedList();
                        if (!checkedList.isEmpty()) {
                            for (StoreBean storeBean : checkedList) {
                                String type = storeBean.getType();
                                if (type.equals("3")) {//枪支
                                    isGun = true;
                                } else {//弹药
                                    isAmmo = true;
                                }
                            }
                        }

                        if (isGun) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                        }

                        if (isAmmo) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getRightCabNo());
                        }

                        break;
                        default:
                            SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                            break;
                }
                break;
            case R.id.store_in_btn_open_lock:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<StoreBean> checkedList = inStoreAdapter.getCheckedList();
                        if (!checkedList.isEmpty()) {
                            for (StoreBean storeBean : checkedList) {
                                String numbered = storeBean.getNumbered();
                                for (int i = 0; i < 5; i++) {
                                    SerialPortUtil.getInstance().openLock(numbered);
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }).start();
                break;
            case R.id.store_in_btn_ok:
                uploadStoreData();
                break;
            case R.id.ac_top_back:
                finish();
                break;
            case R.id.in_store_btn_pre_page: //上一页
                prePager();
                break;
            case R.id.in_store_btn_next_page: //下一页
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        inStoreAdapter.setIndex(index);
        inStoreTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) storeBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        inStoreAdapter.setIndex(index);
        inStoreTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) storeBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            inStoreBtnPrePage.setVisibility(View.INVISIBLE);
            inStoreBtnNextPage.setVisibility(View.VISIBLE);
        } else if (storeBeanList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            inStoreBtnPrePage.setVisibility(View.VISIBLE);
            inStoreBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            inStoreBtnNextPage.setVisibility(View.VISIBLE);
            inStoreBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    private void uploadStoreData() {
        List<StoreBean> checkedList = inStoreAdapter.getCheckedList();
        String jsonString = JSON.toJSONString(checkedList);
        HttpClient.getInstance().uploadStoreData(this, jsonString, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "uploadStoreData onSucceed response data: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                if (dataBean != null) {
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        finish();
                        InStoreActivity.this.finish();
                    } else {
                        ToastUtil.showShort("提交失败!请重试");
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }


}
