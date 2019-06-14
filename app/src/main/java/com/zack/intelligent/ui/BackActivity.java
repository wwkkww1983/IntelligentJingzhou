package com.zack.intelligent.ui;

import android.app.Activity;
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
import com.zack.intelligent.bean.AmmosBean;
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
 * 还枪
 */
public class BackActivity extends BaseActivity {
    private static final String TAG = "BackActivity";
    @BindView(R.id.back_gun_recycler_view)
    RecyclerView backGunRecyclerView;
    @BindView(R.id.back_gun_btn_open_lock)
    Button backGunBtnOpenLock;
    @BindView(R.id.back_gun_btn_finish)
    Button backGunBtnFinish;
    @BindView(R.id.back_gun_bottom_view)
    LinearLayout backGunBottomView;
    @BindView(R.id.back_btn_pre_page)
    Button backBtnPrePage;
    @BindView(R.id.back_tv_cur_page)
    TextView backTvCurPage;
    @BindView(R.id.back_btn_next_page)
    Button backBtnNextPage;
    private BackDataAdapter backDataAdapter;
    private List<OperBean> operList = new ArrayList<>();
    private List<SubCabsBean> subCabs;
    private MembersBean manage1, manage2;
    private List<TaskItemsBean> taskItems;
    private InitDialog initDialog;
    private int index = 0;
    private int pageCount = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_gun);
        ButterKnife.bind(this);
        backBtnPrePage.setVisibility(View.INVISIBLE);
        backBtnNextPage.setVisibility(View.INVISIBLE);
        initDialog = new InitDialog(this);
        String firstPoliceInfo = getIntent().getStringExtra("firstPoliceInfo");
        String secondPoliceInfo = getIntent().getStringExtra("secondPoliceInfo");

        if (!TextUtils.isEmpty(firstPoliceInfo)) {
            manage1 = JSON.parseObject(firstPoliceInfo, MembersBean.class);
        }

        if (!TextUtils.isEmpty(secondPoliceInfo)) {
            manage2 = JSON.parseObject(secondPoliceInfo, MembersBean.class);
        }

        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        backGunRecyclerView.setLayoutManager(llm);
        backDataAdapter = new BackDataAdapter(operList, index, pageCount);
        backGunRecyclerView.setAdapter(backDataAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getBackData();
                getCab();
            }
        }).start();

        try {
            if (manage1 != null && manage2 != null) {
                GreendaoMg.addNormalOperateLog(manage1,
                        1, 7,
                        "【" + manage1.getName()
                                + "】和【" + manage2.getName() + "】+进入【归还枪弹】");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPreNextBtn() {
        if (operList.isEmpty()) {
            backTvCurPage.setText(index + 1 + "/1");
        } else {
            if (operList.size() <= pageCount) {
                backBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                backBtnNextPage.setVisibility(View.VISIBLE);
            }
            backTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operList.size() / pageCount));
        }
    }

    private void getBackData() {
        HttpClient.getInstance().getCurrentBackTask(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getBackData onSucceed response : " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            taskItems = JSON.parseArray(body, TaskItemsBean.class);
                            if (!taskItems.isEmpty()) {
                                for (int i = 0; i < taskItems.size(); i++) {
                                    TaskItemsBean taskItemsBean = taskItems.get(i);
                                    List<OperBean> outGunOpers = taskItemsBean.getOutGunOpers();
                                    if (!outGunOpers.isEmpty()) {
                                        for (int j = 0; j < outGunOpers.size(); j++) {
                                            OperBean operBean = outGunOpers.get(j);
                                            String cabId = operBean.getCabId();
                                            String gunCabId = SharedUtils.getGunCabId();
                                            Log.i(TAG, "onSucceed gunCabId: " + SharedUtils.getGunCabId());
                                            if (cabId.equals(gunCabId)) {
                                                operList.add(operBean);
                                            }
                                        }
                                    }
                                }
                            }
                            backDataAdapter.setOperList(operList);
                            initPreNextBtn();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getBackData onFailed: " + response.getException().getMessage());
            }
        });
    }

    /**
     * 获取枪柜数据
     */
    private void getCab() {
        HttpClient.getInstance().getCabById(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, " getCabData onSucceed: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                            Log.i(TAG, "onSucceed cab no: " + gunCabsBean.getNo());
                            subCabs = gunCabsBean.getSubCabs();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getCabData onFailed error: " + response.getException().getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GreendaoMg.addNormalOperateLog(manage1,
                    1, 8, "【" + manage1.getName() + "】退出【还枪】");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.back_gun_btn_open_lock, R.id.back_gun_btn_finish, R.id.ac_top_back,
            R.id.back_btn_pre_page, R.id.back_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_gun_btn_open_lock: //开锁
                try {
                    GreendaoMg.addNormalLog(manage1,
                            1, 7,
                            "【" + manage1.getName()
                                    + "】和【" + manage2.getName() + "】执行【确认归还枪弹】");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uploadBackData();
                break;
            case R.id.back_gun_btn_finish:  //完成
                finish();
                break;
            case R.id.ac_top_back:
                finish();
                break;
            case R.id.back_btn_pre_page:
                prePager();
                break;
            case R.id.back_btn_next_page:
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        backDataAdapter.setIndex(index);
        backTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        backDataAdapter.setIndex(index);
        backTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            backBtnPrePage.setVisibility(View.INVISIBLE);
            backBtnNextPage.setVisibility(View.VISIBLE);
        } else if (operList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            backBtnPrePage.setVisibility(View.VISIBLE);
            backBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            backBtnNextPage.setVisibility(View.VISIBLE);
            backBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    private boolean haveGun;
    private boolean haveAmmo;

    /**
     * 提交归还数据 打开枪柜和枪锁
     */
    private void uploadBackData() {
        haveGun = false;
        haveAmmo = false;
        try {
            List<OperBean> checkedList = backDataAdapter.getCheckedList();
            Log.i(TAG, "onViewClicked checked list size: " + checkedList.size());
//            Log.i(TAG, "onViewClicked checked list: " + JSON.toJSONString(checkedList));
            if (!checkedList.isEmpty()) {
                for (OperBean operBean : checkedList) {
                    String taskItemId = operBean.getTaskItemId();
                    String objectId = operBean.getObjectId();
                    if (!subCabs.isEmpty()) {
                        for (SubCabsBean subCabsBean : subCabs) {
                            GunsBean guns = subCabsBean.getGuns();
                            AmmosBean ammos = subCabsBean.getAmmos();
                            if (guns != null) {
                                if (guns.getId().equals(objectId)) { //枪支的id相同
                                    jointToOperList(subCabsBean, operBean);
                                    haveGun = true;
                                    if (!taskItems.isEmpty()) {
                                        for (TaskItemsBean taskItemsBean : taskItems) {
                                            //上传还枪日志
                                            if (taskItemId.equals(taskItemsBean.getId())) {
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manage1.getId(),
                                                        manage2.getId(),
                                                        Constants.TASK_TYPE_GET,
                                                        Constants.OPER_TYPE_BACK_GUN,
                                                        guns.getId(),
                                                        3);
                                            }
                                        }
                                    }
                                }
                            } else if (ammos != null) {
                                if (ammos.getId().equals(objectId)) {//弹药id相同
                                    jointToOperList(subCabsBean, operBean);
                                    haveAmmo = true;
                                    if (!taskItems.isEmpty()) {
                                        for (TaskItemsBean taskItemsBean : taskItems) {
                                            //上传还弹日志
                                            if (taskItemId.equals(taskItemsBean.getId())) {
                                                if (ammos.isBox()) {
                                                    GreendaoMg.addOperGunsLog(
                                                            taskItemsBean,
                                                            manage1.getId(),
                                                            manage2.getId(),
                                                            Constants.TASK_TYPE_GET,
                                                            Constants.OPER_TYPE_BACK_AMMO,
                                                            ammos.getId(),
                                                            2);
                                                } else {
                                                    GreendaoMg.addOperGunsLog(
                                                            taskItemsBean,
                                                            manage1.getId(),
                                                            manage2.getId(),
                                                            Constants.TASK_TYPE_GET,
                                                            Constants.OPER_TYPE_BACK_AMMO,
                                                            ammos.getId(),
                                                            1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                postGoTask();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<SubCabsBean> postGetData = new ArrayList<>();
    private List<OperBean> taskBackList = new ArrayList<>();

    private void jointToOperList(SubCabsBean subCabsBean, OperBean operBean1) {
        Log.i(TAG, "jointToOperList : ");
        postGetData.add(subCabsBean);
        OperBean operBean = new OperBean();
        operBean.setTaskItemId(operBean1.getTaskItemId());
        operBean.setManageId(manage1.getId());
        operBean.setCabId(subCabsBean.getCabId());
        operBean.setSubCabId(subCabsBean.getId());

        AmmosBean ammosBean = subCabsBean.getAmmos();
        GunsBean gunsBean = subCabsBean.getGuns();
        if (ammosBean != null) {
            if (ammosBean.isBox()) { //弹夹
                operBean.setPositionType(2);
            } else { //子弹
                operBean.setPositionType(1);
            }
            operBean.setObjectTypeId(ammosBean.getObjectTypeId());
            operBean.setObjectId(ammosBean.getId());
            operBean.setOperNumber(operBean1.getOperNumber());
        } else if (gunsBean != null) {
            operBean.setObjectTypeId(gunsBean.getObjectTypeId());
            operBean.setGunNo(gunsBean.getNo());
            operBean.setObjectId(gunsBean.getId());
            operBean.setPositionType(3);
            operBean.setOperNumber(1);
        }
        operBean.setOperType(2);
        taskBackList.add(operBean); //增加一组数据
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (initDialog != null && !this.isFinishing()) {
            initDialog.dismiss();
        }
    }

    private void postGoTask() {
        if (!isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("正在提交数据");
        Log.i(TAG, "postGoTask operBeanList size: " + taskBackList.size());
        String jsonStr = JSON.toJSONString(taskBackList);
        LogUtil.i(TAG, "postGoTask  jsonStr: " + jsonStr);
        HttpClient.getInstance().postGetGun(this, jsonStr, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postGoTask onSucceed data: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    Log.i(TAG, "onSucceed 提交成功: ");
                    initDialog.setTip("提交归还枪弹数据成功，正在打开枪柜。。。");
                    //提交成功 依次打开枪柜门和枪锁
                    if (SharedUtils.getGunCabType() == Constants.CAB_TYPE_MIX) {//一体柜
                        if (haveGun) {
                            for (int i = 0; i < 2; i++) {
                                SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());//开弹柜
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (haveAmmo) {
                            for (int i = 0; i < 2; i++) {
                                SerialPortUtil.getInstance().openLock(SharedUtils.getRightCabNo());//开弹柜
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < 2; i++) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());//开枪柜
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    initDialog.setTip("枪柜门已打开, 正在打开枪锁。。。");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!postGetData.isEmpty()) {
                                for (SubCabsBean subCab : postGetData) {
                                    String no = subCab.getNo();
                                    for (int i = 0; i < 5; i++) {
                                        SerialPortUtil.getInstance().openLock(no);
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog.setTip("枪锁已全部打开完成 请取出枪支或弹药");
                                    initDialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }).start();


                } else {
                    Log.i(TAG, "onSucceed 提交归还枪支数据失败: ");
                    initDialog.setTip("提交归还数据失败");
                    initDialog.dismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LogUtil.i(TAG, "postGoTask onFailed: " + response.getException().getMessage());
                initDialog.setTip("提交归还数据失败");
                initDialog.dismiss();
            }
        });
    }

}
