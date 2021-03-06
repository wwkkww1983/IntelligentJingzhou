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
import com.zack.intelligent.adapter.TaskItemAdapter;
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
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetActivity extends BaseActivity {
    private static final String TAG = "GetActivity";
    @BindView(R.id.get_gun_recycler_view)
    RecyclerView getGunRecyclerView;
    @BindView(R.id.get_btn_open_lock)
    Button getBtnOpenLock;
    @BindView(R.id.get_btn_finish)
    Button getBtnFinish;
    @BindView(R.id.get_gun_ll_nav)
    LinearLayout getGunLlNav;
    protected List<OperBean> operBeanList = new ArrayList<>();
    @BindView(R.id.get_btn_pre_page)
    Button getBtnPrePage;
    @BindView(R.id.get_tv_cur_page)
    TextView getTvCurPage;
    @BindView(R.id.get_btn_next_page)
    Button getBtnNextPage;
    private List<TaskItemsBean> taskItems = new ArrayList<>();
    private TaskItemAdapter taskItemAdapter;
    private List<SubCabsBean> subCabs = new ArrayList<>();
    private MembersBean manage1, manage2;
    private InitDialog initDialog;
    private int index = 0;
    private int pageCount = 7;
    private Map<Integer, Integer> objTypeNum = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);
        ButterKnife.bind(this);
        //??????????????????id
        String firstPoliceInfo = getIntent().getStringExtra("firstPoliceInfo");
        //??????????????????id
        String secondPoliceInfo = getIntent().getStringExtra("secondPoliceInfo");

        getBtnPrePage.setVisibility(View.INVISIBLE);
        getBtnNextPage.setVisibility(View.INVISIBLE);

        if (!TextUtils.isEmpty(firstPoliceInfo)) {
            manage1 = JSON.parseObject(firstPoliceInfo, MembersBean.class);
        }

        if (!TextUtils.isEmpty(secondPoliceInfo)) {
            manage2 = JSON.parseObject(secondPoliceInfo, MembersBean.class);
        }

        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayout.VERTICAL, false);
        getGunRecyclerView.setLayoutManager(llm);
        taskItemAdapter = new TaskItemAdapter(taskItems, index, pageCount);
        getGunRecyclerView.setAdapter(taskItemAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCabInfo();
            }
        }).start();
        initDialog = new InitDialog(this);
    }

    private void initPreNextBtn() {
        if (taskItems.isEmpty()) {
            getTvCurPage.setText(index + 1 + "/1");
        } else {
            if (taskItems.size() <= pageCount) {
                getBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                getBtnNextPage.setVisibility(View.VISIBLE);
            }
            getTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) taskItems.size() / pageCount));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (initDialog != null && !this.isFinishing()) {
            initDialog.dismiss();
        }
    }

    /**
     * ??????????????????????????????
     */
    private void getCurrentTask() {
        Log.i(TAG, "getCurrentTask ??????????????????????????????: ");
        HttpClient.getInstance().getCurrentGetTask(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getCurrentTask onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        Map<Integer, Integer> taskObjNum = new HashMap<>();
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<TaskItemsBean> taskItemsBeanList = JSON.parseArray(body, TaskItemsBean.class);
                            if (!taskItemsBeanList.isEmpty()) {
                                LogUtil.i(TAG, "onSucceed taskItems size: " + taskItemsBeanList.size());
                                for (int i = 0; i < taskItemsBeanList.size(); i++) {
                                    TaskItemsBean taskItemsBean = taskItemsBeanList.get(i);
                                    int objectTypeId = taskItemsBean.getObjectTypeId();
                                    if (objTypeNum.containsKey(objectTypeId)) { //???????????????????????????
                                        //?????????????????????????????????????????????
                                        if (taskObjNum.containsKey(objectTypeId)) {
                                            taskObjNum.put(objectTypeId, taskObjNum.get(objectTypeId));
                                        } else {
                                            taskObjNum.put(objectTypeId, 1);
                                        }
                                        //?????????????????????????????????????????????????????????????????????
                                        if (taskObjNum.get(objectTypeId) <= objTypeNum.get(objectTypeId)) {
                                            taskItems.add(taskItemsBean);
                                        }
                                    }
                                }
                            } else {
                                Log.i(TAG, "????????????????????????: ");
                                ToastUtil.showShort("????????????????????????");
                            }
                            taskItemAdapter.notifyDataSetChanged();
                            initPreNextBtn();
                        } else {
                            Log.i(TAG, "????????????????????????: ");
                            ToastUtil.showShort("????????????????????????");
                        }
                    } else {
                        Log.i(TAG, "??????????????????????????????: ");
                        ToastUtil.showShort("??????????????????????????????");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error:  " + response.getException().getMessage());
            }
        });
    }

    /**
     * ??????????????????
     */
    private void getCabInfo() {
        HttpClient.getInstance().getCabById(this, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getCabData onSucceed: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                            subCabs = gunCabsBean.getSubCabs();
                            if (!subCabs.isEmpty()) {
                                for (SubCabsBean subCabsBean : subCabs) {
                                    GunsBean guns = subCabsBean.getGuns();
                                    AmmosBean ammos = subCabsBean.getAmmos();
                                    if (guns != null && guns.getObjectStatus() == 1) {
                                        int objectTypeId = guns.getObjectTypeId();
                                        if (objTypeNum.containsKey(objectTypeId)) {
                                            objTypeNum.put(objectTypeId, objTypeNum.get(objectTypeId));
                                        } else {
                                            objTypeNum.put(objectTypeId, 1);
                                        }
                                    } else if (ammos != null && ammos.getObjectNumber() > 0) {
                                        int objectTypeId = ammos.getObjectTypeId();
                                        if (objTypeNum.containsKey(objectTypeId)) {
                                            objTypeNum.put(objectTypeId, objTypeNum.get(objectTypeId));
                                        } else {
                                            objTypeNum.put(objectTypeId, 1);
                                        }
                                    }
                                }
                                getCurrentTask();
                            } else {
                                ToastUtil.showShort("??????????????????");
                                Log.i(TAG, "onSucceed ??????????????????: ");
                            }
                        } else {
                            ToastUtil.showShort("??????????????????");
                            Log.i(TAG, "onSucceed ??????????????????: ");
                        }
                    } else {
                        ToastUtil.showShort("????????????????????????");
                        Log.i(TAG, "onSucceed ????????????????????????: ");
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

    @OnClick({R.id.get_btn_open_lock, R.id.get_btn_finish, R.id.ac_top_back, R.id.get_btn_pre_page,
            R.id.get_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.get_btn_open_lock://?????????
                try {
                    GreendaoMg.addNormalLog(manage1, 1, 1,
                            "?????????" + manage1.getName() + "?????????" + manage2.getName() + "?????????????????????");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uploadData();
                break;
            case R.id.get_btn_finish://??????
                finish();
                break;
            case R.id.ac_top_back: //??????
                finish();
                break;
            case R.id.get_btn_pre_page: //?????????
                prePager();
                break;
            case R.id.get_btn_next_page: //?????????
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        taskItemAdapter.setIndex(index);
        getTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) taskItems.size() / pageCount));
        //?????????????????????????????????
        checkButton();
//        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        taskItemAdapter.setIndex(index);
        getTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) taskItems.size() / pageCount));
        //?????????????????????????????????
        checkButton();
//        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            getBtnPrePage.setVisibility(View.INVISIBLE);
            getBtnNextPage.setVisibility(View.VISIBLE);
        } else if (taskItems.size() - index * pageCount <= pageCount) {    //??????????????????????????????????????????????????????????????????????????????
            getBtnPrePage.setVisibility(View.VISIBLE);
            getBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            getBtnNextPage.setVisibility(View.VISIBLE);
            getBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    private boolean haveGun;
    private boolean haveAmmo;

    /**
     * ?????????????????? ?????????????????????
     */
    private void uploadData() {
        haveGun = false;
        haveAmmo = false;
        try {
            //????????????
            List<TaskItemsBean> checkedList = taskItemAdapter.getCheckedList();
            Log.i(TAG, "onViewClicked checkedList : " + JSON.toJSONString(checkedList));
            Map<SubCabsBean, Boolean> isSubcabAdded = new HashMap<>();
            if (!checkedList.isEmpty()) {
                for (TaskItemsBean taskItemsBean : checkedList) {
                    int objectTypeId = taskItemsBean.getObjectTypeId();
                    if (!subCabs.isEmpty()) { //????????????????????????
                        Map<Integer, Boolean> isAdded = new HashMap<>();
                        for (SubCabsBean subCabsBean : subCabs) {
                            AmmosBean ammos = subCabsBean.getAmmos(); //??????????????????
                            GunsBean guns = subCabsBean.getGuns();//??????????????????
                            if (ammos != null) {
                                int ammoTypeId = ammos.getObjectTypeId();
                                if (objectTypeId == ammoTypeId) {
                                    if (!isAdded.containsKey(ammoTypeId)) {
                                        haveAmmo = true;
                                        isAdded.put(ammoTypeId, true);
                                        jointToOperList(subCabsBean, taskItemsBean);
                                    }
                                }
                            } else if (guns != null) {
                                int gunTypeId = guns.getObjectTypeId();
                                if (objectTypeId == gunTypeId) { //???????????????????????????????????????
                                    if (!isAdded.containsKey(gunTypeId)
                                            && !isSubcabAdded.containsKey(subCabsBean)) {
                                        haveGun = true;
                                        isSubcabAdded.put(subCabsBean, true);
                                        isAdded.put(gunTypeId, true);
                                        jointToOperList(subCabsBean, taskItemsBean);
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


    private List<SubCabsBean> postListData = new ArrayList<>();
    private Map<SubCabsBean, TaskItemsBean> getDataMap = new HashMap<>();

    /**
     * ???????????????????????????????????????
     *
     * @param subCabsBean
     * @param taskItemsBean
     */
    private void jointToOperList(SubCabsBean subCabsBean, TaskItemsBean taskItemsBean) {
        postListData.add(subCabsBean);
        getDataMap.put(subCabsBean, taskItemsBean);
        Log.i(TAG, "jointToOperList : ");
        OperBean operBean = new OperBean();
        operBean.setTaskId(taskItemsBean.getTaskId());
        operBean.setTaskItemId(taskItemsBean.getId());
        operBean.setManageId(manage1.getId());
        operBean.setCabId(subCabsBean.getCabId());
        operBean.setSubCabId(subCabsBean.getId());
        operBean.setSubCabNo(subCabsBean.getNo());

        int objectNumber = taskItemsBean.getObjectNumber();
        AmmosBean ammosBean = subCabsBean.getAmmos();
        GunsBean gunsBean = subCabsBean.getGuns();
        if (ammosBean != null) {
            if (ammosBean.isBox()) { //??????
                operBean.setPositionType(2);
            } else { //??????
                operBean.setPositionType(1);
            }
            operBean.setObjectTypeId(ammosBean.getObjectTypeId());
            operBean.setObjectId(ammosBean.getId());
            operBean.setOperNumber(objectNumber);
        } else if (gunsBean != null) {
            operBean.setGunNo(gunsBean.getNo());
            operBean.setObjectTypeId(gunsBean.getObjectTypeId());
            operBean.setObjectId(gunsBean.getId());
            operBean.setPositionType(3);
            operBean.setOperNumber(1);
        }
        int taskItemStatus = taskItemsBean.getTaskItemStatus();
        if (taskItemStatus == 1) { //??????
            operBean.setOperType(1);
        } else if (taskItemStatus == 2) {//??????
            operBean.setOperType(2);
        }
        operBeanList.add(operBean); //??????????????????
        Log.i(TAG, "jointToOperList  operBeanList: " + JSON.toJSONString(operBeanList));
    }

    private void postGoTask() {
        if (!isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("????????????????????????");
        Log.i(TAG, "postGoTask operBeanList size: " + operBeanList.size());
        String jsonStr = JSON.toJSONString(operBeanList);
        LogUtil.i(TAG, "postGoTask  jsonStr: " + jsonStr);
        HttpClient.getInstance().postGetGun(this, jsonStr, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postGoTask onSucceed data: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    Log.i(TAG, "onSucceed ????????????: ");
                    initDialog.setTip("????????????????????????, ???????????????????????????");
                    //???????????? ??????????????????????????????
                    if (SharedUtils.getGunCabType() == 3) {
                        if (haveGun) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());//?????????
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (haveAmmo) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getRightCabNo());//?????????
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());//?????????
                    }

                    initDialog.setTip("??????????????????, ???????????????????????????");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!postListData.isEmpty()) {
                                for (SubCabsBean subCab : postListData) {
                                    String no = subCab.getNo();
                                    for (int i = 0; i < 5; i++) {
                                        SerialPortUtil.getInstance().openLock(no);
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    GunsBean guns = subCab.getGuns();
                                    AmmosBean ammos = subCab.getAmmos();
                                    if (getDataMap.containsKey(subCab)) {
                                        TaskItemsBean taskItemsBean = getDataMap.get(subCab);
                                        if (guns != null) {
                                            GreendaoMg.addOperGunsLog(
                                                    taskItemsBean,
                                                    manage1.getId(),
                                                    manage2.getId(),
                                                    Constants.TASK_TYPE_GET,
                                                    Constants.OPER_TYPE_GET_GUN,
                                                    guns.getId(),
                                                    3);
                                        } else if (ammos != null) {
                                            if (ammos.isBox()) {//??????
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manage1.getId(),
                                                        manage2.getId(),
                                                        Constants.TASK_TYPE_GET,
                                                        Constants.OPER_TYPE_GET_AMMO,
                                                        ammos.getId(), 2);
                                            } else {//??????
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manage1.getId(),
                                                        manage2.getId(),
                                                        Constants.TASK_TYPE_GET,
                                                        Constants.OPER_TYPE_GET_AMMO,
                                                        ammos.getId(),
                                                        1);
                                            }
                                        }
                                    }
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog.setTip("????????????????????????????????????????????????");
                                    initDialog.dismiss();
                                    finish();
                                }
                            });

                        }
                    }).start();
                } else {
                    initDialog.setTip("????????????????????????");
                    initDialog.dismiss();
                    Log.i(TAG, "onSucceed ????????????: ");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "postGoTask onFailed: " + response.getException().getMessage());
                initDialog.setTip("????????????????????????");
                initDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GreendaoMg.addNormalOperateLog(manage1,
                    1, 8,
                    "???" + manage1.getName()
                            + "?????????" + manage2.getName() + "???+????????????????????????");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
