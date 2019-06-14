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
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.BackDataAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ui.dialog.InitDialog;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SharedUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报废任务
 */
public class ScrapActivity extends BaseActivity {
    private static final String TAG = ScrapActivity.class.getSimpleName();
    @BindView(R.id.scrap_tv_tittle)
    TextView scrapTvTittle;
    @BindView(R.id.scrap_recycler_view)
    RecyclerView scrapRecyclerView;
    @BindView(R.id.scrap_btn_post)
    Button scrapBtnPost;
    @BindView(R.id.scrap_btn_back)
    Button scrapBtnBack;
    @BindView(R.id.scrap_ll_title)
    LinearLayout scrapLlTitle;
    @BindView(R.id.scrap_btn_pre_page)
    Button scrapBtnPrePage;
    @BindView(R.id.scrap_tv_cur_page)
    TextView scrapTvCurPage;
    @BindView(R.id.scrap_btn_next_page)
    Button scrapBtnNextPage;
    @BindView(R.id.scrap_ll_bottom)
    LinearLayout scrapLlBottom;

    private MembersBean leader;
    private MembersBean manage;
    private List<OperBean> operBeanList = new ArrayList<>();
    private List<GunsBean> gunsBeanList = new ArrayList<>();
    private BackDataAdapter adapter;
    private int index = 0;
    private int pageCount = 7;
    private InitDialog initDialog;
    private List<TaskItemsBean> taskItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scrap);
        ButterKnife.bind(this);
        SerialPortUtil.getInstance().onCreate();
        taskItemsList = new ArrayList<>();
        try {
            scrapBtnPrePage.setVisibility(View.INVISIBLE);
            scrapBtnNextPage.setVisibility(View.INVISIBLE);

            String firstPoliceInfo = getIntent().getStringExtra("firstPoliceInfo");//管理员id
            String secondPoliceInfo = getIntent().getStringExtra("secondPoliceInfo");//领导id
            if (!TextUtils.isEmpty(firstPoliceInfo)) {
                manage = JSON.parseObject(firstPoliceInfo, MembersBean.class);
            }
            if (!TextUtils.isEmpty(secondPoliceInfo)) {
                leader = JSON.parseObject(secondPoliceInfo, MembersBean.class);
            }
            GreendaoMg.addNormalLog(manage, 5, 7,
                    "【" + manage.getName() + "】和【" + leader.getName() + "】进入【枪支报废】");

            LinearLayoutManager llm = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false);
            scrapRecyclerView.setLayoutManager(llm);
            adapter = new BackDataAdapter(operBeanList, index, pageCount);
            scrapRecyclerView.setAdapter(adapter);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getScarpTask();
                    getCabById();
                }
            }).start();
            initDialog = new InitDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedUtils.setOpenCapture(true);
        Constants.isCapture = true;

        adapter.setParent(1);
    }

    /**
     * 获取枪柜数据
     */
    private void getCabById() {
        HttpClient.getInstance().getCabById(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getCabById onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                        List<SubCabsBean> subCabsList = gunCabsBean.getSubCabs();
                        if (!subCabsList.isEmpty()) {
                            for (int i = 0; i < subCabsList.size(); i++) {
                                SubCabsBean subCabsBean = subCabsList.get(i);
                                GunsBean gunsBean = subCabsBean.getGuns();
                                if (gunsBean != null) {
                                    gunsBeanList.add(gunsBean);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    /**
     * 获取枪支报废任务
     */
    private void getScarpTask() {
        HttpClient.getInstance().getScrapTask(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getScrapTask onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    String body = dataBean.getBody();
                    taskItemsList = JSON.parseArray(body, TaskItemsBean.class);
                    if (!taskItemsList.isEmpty()) {
                        for (int i = 0; i < taskItemsList.size(); i++) {
                            TaskItemsBean taskItemsBean = taskItemsList.get(i);
                            List<OperBean> outGunOperList = taskItemsBean.getOutGunOpers();
                            operBeanList.addAll(outGunOperList);
                        }
                    }
                    initPreNextBtn();
                    adapter.setOperList(operBeanList);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    private void initPreNextBtn() {
        if (operBeanList.isEmpty()) {
            scrapTvCurPage.setText(index + 1 + "/1");
        } else {
            if (operBeanList.size() <= pageCount) {
                scrapBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                scrapBtnNextPage.setVisibility(View.VISIBLE);
            }
            scrapTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (initDialog != null && !this.isFinishing()) {
            initDialog.dismiss();
        }
    }

    /**
     * 提交报废数据
     */
    private void postScrapData(List<OperBean> checkedList) {
        if (!isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("正在提交报废数据。。。");
        String toJSONString = JSON.toJSONString(checkedList);
        Log.i(TAG, "onViewClicked jsonString: " + toJSONString);
        HttpClient.getInstance().postScrapData(ScrapActivity.this,
                toJSONString, new HttpListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) throws JSONException {
                        Log.i(TAG, "postScrapData onSucceed response: " + response.get());
                        try {
                            DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                            boolean success = dataBean.isSuccess();
                            if (success) {
                                initDialog.setTip("提交报废数据成功， 正在打开枪柜门。。。");
                                for (int i = 0; i < 2; i++) {
                                    SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                                    Thread.sleep(500);
                                }
                                initDialog.setTip("打开枪柜门成功，正在打开枪锁。。。");
                                List<OperBean> checkedList = adapter.getCheckedList();
                                for (OperBean operBean : checkedList) {
                                    String subCabNo = operBean.getSubCabNo();
                                    String taskItemId = operBean.getTaskItemId();
                                    for (int i = 0; i < 5; i++) {
                                        SerialPortUtil.getInstance().openLock(subCabNo);
                                        Thread.sleep(200);
                                    }
                                    if (!taskItemsList.isEmpty()) {
                                        for (TaskItemsBean taskItemsBean : taskItemsList) {
                                            String id = taskItemsBean.getId();
                                            if (id.equals(taskItemId)) {
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manage.getId(),
                                                        leader.getId(),
                                                        Constants.TASK_TYPE_SCRAP,
                                                        Constants.OPER_TYPE_GET_GUN,
                                                        operBean.getObjectId(),
                                                        Constants.OBJECT_TYPE_GUN);
                                            }
                                        }
                                    }
                                }
                                Log.i(TAG, "onSucceed 提交枪支报废成功: ");
                                initDialog.setTip("打开枪锁成功！");
                                initDialog.dismiss();
                                finish();
                            } else {
                                Log.i(TAG, "onSucceed 提交枪支报废失败: ");
                                initDialog.setTip("提交报废数据失败！");
                                initDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int what, Response<String> response) {
                        initDialog.setTip("提交报废数据失败！");
                        initDialog.dismiss();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GreendaoMg.addNormalLog(manage, 5, 8,
                    "【" + manage.getName() + "】和【" + leader.getName() + "】退出【枪支报废】");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedUtils.setOpenCapture(false);
        Constants.isCapture = false;
    }

    @OnClick({R.id.scrap_btn_post, R.id.scrap_btn_back, R.id.ac_top_back,
            R.id.scrap_btn_pre_page, R.id.scrap_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scrap_btn_post:
                final List<OperBean> checkedList = adapter.getCheckedList();
                if (checkedList.isEmpty()) {
                    Log.i(TAG, "onViewClicked checked list is null: ");
                    return;
                }
                try {
                    postScrapData(checkedList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.scrap_btn_back:
                finish();
                break;
            case R.id.ac_top_back:
                finish();
                break;
            case R.id.scrap_btn_pre_page://上一页
                prePager();
                break;
            case R.id.scrap_btn_next_page://下一页
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        adapter.setIndex(index);
        scrapTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        adapter.setIndex(index);
        scrapTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            scrapBtnPrePage.setVisibility(View.INVISIBLE);
            scrapBtnNextPage.setVisibility(View.VISIBLE);
        } else if (operBeanList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            scrapBtnPrePage.setVisibility(View.VISIBLE);
            scrapBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            scrapBtnNextPage.setVisibility(View.VISIBLE);
            scrapBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

}
