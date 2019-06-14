package com.zack.intelligent.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.KeepGetDataAdapter;
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
import com.zack.intelligent.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 保养领取枪支
 */
public class KeepGetFragment extends Fragment {
    private static final String TAG = "KeepGetFragment";
    @BindView(R.id.keep_ll_title)
    LinearLayout keepLlTitle;
    @BindView(R.id.keep_get_recycler_view)
    RecyclerView keepGetRecyclerView;
    @BindView(R.id.keep_get_btn_unlock)
    Button keepGetBtnUnlock;
    @BindView(R.id.keep_get_btn_finish)
    Button keepGetBtnFinish;
    @BindView(R.id.keep_get_ll_bottom)
    LinearLayout keepGetLlBottom;
    Unbinder unbinder;
    @BindView(R.id.keep_get_btn_pre_page)
    Button keepGetBtnPrePage;
    @BindView(R.id.keep_get_tv_cur_page)
    TextView keepGetTvCurPage;
    @BindView(R.id.keep_get_btn_next_page)
    Button keepGetBtnNextPage;
    private List<OperBean> operList = new ArrayList<>();
    private List<GunsBean> gunsBeanList = new ArrayList<>();
    private KeepGetDataAdapter adapter;
    private int index = 0;
    private int pageCount = 7;
    private InitDialog initDialog;
    private List<TaskItemsBean> taskItemsBeanList;
    private MembersBean manager1, manager2;

    public KeepGetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keep_get, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * 初始化数据
     */
    private void initView() {
        taskItemsBeanList =new ArrayList<>();
        keepGetBtnPrePage.setVisibility(View.INVISIBLE);
        keepGetBtnNextPage.setVisibility(View.INVISIBLE);

        try {
            String firstPoliceInfo = getActivity().getIntent().getStringExtra("firstPoliceInfo"); //管理员id
            String secondPoliceInfo = getActivity().getIntent().getStringExtra("secondPoliceInfo"); //领导id

            if (!TextUtils.isEmpty(firstPoliceInfo)) {
                manager1 = JSON.parseObject(firstPoliceInfo, MembersBean.class);
            }

            if (!TextUtils.isEmpty(secondPoliceInfo)) {
                manager2 = JSON.parseObject(secondPoliceInfo, MembersBean.class);
            }

            GreendaoMg.addNormalLog(manager1,
                    2, 7, "【"
                            + manager1.getName() + "】和【" + manager2.getName() + "】进入保养领取枪支");
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        keepGetRecyclerView.setLayoutManager(llm);
        adapter = new KeepGetDataAdapter(operList, index, pageCount,1);
        keepGetRecyclerView.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getKeepTask(); //获取保养任务数据
                getCabById();//获取枪支数据
            }
        }).start();
        initDialog =new InitDialog(getContext());
    }

    private void initPreNextBtn() {
        if (operList.isEmpty()) {
            keepGetTvCurPage.setText(index + 1 + "/1");
        } else {
            if (operList.size() <= pageCount) {
                keepGetBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                keepGetBtnNextPage.setVisibility(View.VISIBLE);
            }
            keepGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operList.size() / pageCount));
        }
    }

    /**
     * 获取保养任务
     */
    private void getKeepTask() {
        HttpClient.getInstance().getKeepTask(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getKeepTask onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        taskItemsBeanList = JSON.parseArray(body, TaskItemsBean.class);
                        if (!taskItemsBeanList.isEmpty()) {
                            for (int i = 0; i < taskItemsBeanList.size(); i++) {
                                TaskItemsBean taskItemsBean = taskItemsBeanList.get(i);
                                List<OperBean> outGunOpers = taskItemsBean.getOutGunOpers();
                                operList.addAll(outGunOpers);
                            }
                        }
                        adapter.setOperList(operList);
//                        adapter.notifyDataSetChanged();
                        initPreNextBtn();
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
     * 获取枪柜数据
     */
    private void getCabById() {
        HttpClient.getInstance().getCabById(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getCabById onSucceed response: " + response.get());
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
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (initDialog != null && !getActivity().isFinishing()) {
            initDialog.dismiss();
        }
    }

    /**
     * 提交保养任务
     *
     * @param jsonString
     */
    private void postKeepData(String jsonString) {
        if (!getActivity().isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("正在提交领枪数据。。。");
        HttpClient.getInstance().postKeepGetData(getContext(), jsonString, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postKeepData onSucceed data: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    Log.i(TAG, "onSucceed 提交成功: ");
                    initDialog.setTip("保养领枪数据提交成功，正在打开枪柜门。。。");
                    //打开枪柜
                    for (int i = 0; i < 2; i++) {
                        SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    initDialog.setTip("打开枪柜门成功， 正在打开枪锁。。。");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<OperBean> checkedList = adapter.getCheckedList();
                            //打开枪锁
                            for (OperBean operBean: checkedList) {
                                String subCabNo = operBean.getSubCabNo();
                                String taskItemId = operBean.getTaskItemId();
                                for (int i = 0; i < 5; i++) {
                                    SerialPortUtil.getInstance().openLock(subCabNo);
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(!taskItemsBeanList.isEmpty()){
                                    for (TaskItemsBean taskItemsBean:taskItemsBeanList) {
                                        String id = taskItemsBean.getId();
                                        if(id.equals(taskItemId)){
                                            int positionType = operBean.getPositionType();
                                            if(positionType ==1){//子弹
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manager1.getId(),
                                                        manager2.getId(),
                                                        Constants.TASK_TYPE_KEEP,
                                                        Constants.OPER_TYPE_GET_GUN,
                                                        operBean.getObjectId(),
                                                        Constants.OBJECT_TYPE_BULLET);

                                            }else if(positionType ==2){//弹匣
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manager1.getId(),
                                                        manager2.getId(),
                                                        Constants.TASK_TYPE_KEEP,
                                                        Constants.OPER_TYPE_GET_GUN,
                                                        operBean.getObjectId(),
                                                        Constants.OBJECT_TYPE_CLIP);
                                            }else if(positionType ==3){//枪支
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manager1.getId(),
                                                        manager2.getId(),
                                                        Constants.TASK_TYPE_KEEP,
                                                        Constants.OPER_TYPE_GET_GUN,
                                                        operBean.getObjectId(),
                                                        Constants.OBJECT_TYPE_GUN);
                                            }
                                        }
                                    }
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog.setTip("打开枪锁完成！");
                                    initDialog.dismiss();
                                    //返回首页
                                    getActivity().finish();
                                }

                            });
                        }
                    }).start();

                } else {
                    Log.i(TAG, "onSucceed 提交失败: ");
                    initDialog.setTip("提交数据失败！");
                    initDialog.dismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "postKeepData onFailed: " + response.getException().getMessage());
                initDialog.setTip("提交数据失败！");
                initDialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            unbinder.unbind();
            GreendaoMg.addNormalLog(manager1,
                    2, 7, "【"
                            + manager1.getName() + "】和【" + manager2.getName() + "】退出保养领取枪支");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.keep_get_btn_unlock, R.id.keep_get_btn_finish, R.id.keep_get_btn_pre_page,
            R.id.keep_get_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.keep_get_btn_unlock: //开锁 提交数据
                List<OperBean> checkedList = adapter.getCheckedList();
                if (checkedList.isEmpty()) {
                    Log.i(TAG, "onViewClicked checked list is null: ");
                    return;
                }
                String jsonString = JSON.toJSONString(checkedList);
                Log.i(TAG, "onViewClicked jsonString: " + jsonString);

                if (!Utils.isNetworkAvailable()) { //检查网路连接
                    Log.i(TAG, "onViewClicked 网络连接断开: ");
                    return;
                }
                postKeepData(jsonString);//提交数据
                break;
            case R.id.keep_get_btn_finish://返回
                getActivity().finish();
                break;
            case R.id.keep_get_btn_pre_page:    //上一页
                prePager();
                break;
            case R.id.keep_get_btn_next_page:   //下一页
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        adapter.setIndex(index);
        keepGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        adapter.setIndex(index);
        keepGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            keepGetBtnPrePage.setVisibility(View.INVISIBLE);
            keepGetBtnNextPage.setVisibility(View.VISIBLE);
        } else if (operList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            keepGetBtnPrePage.setVisibility(View.VISIBLE);
            keepGetBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            keepGetBtnNextPage.setVisibility(View.VISIBLE);
            keepGetBtnPrePage.setVisibility(View.VISIBLE);
        }
    }
}
