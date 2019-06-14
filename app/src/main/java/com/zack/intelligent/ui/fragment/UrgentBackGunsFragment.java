package com.zack.intelligent.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zack.intelligent.adapter.BackDataAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.dialog.InitDialog;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.ToastUtil;

import org.winplus.serial.utils.SerialPort;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 紧急用枪归还
 */
public class UrgentBackGunsFragment extends Fragment {
    private static final String TAG = "UrgentBackGunsFragment";

    @BindView(R.id.urgent_back_guns_ll_tittle)
    LinearLayout urgentBackGunsLlTittle;
    @BindView(R.id.urgent_back_recycler_view)
    RecyclerView urgentBackRecyclerView;
    @BindView(R.id.urgent_back_btn_pre_page)
    Button urgentBackBtnPrePage;
    @BindView(R.id.urgent_back_tv_cur_page)
    TextView urgentBackTvCurPage;
    @BindView(R.id.urgent_back_btn_next_page)
    Button urgentBackBtnNextPage;
    @BindView(R.id.urgent_back_btn_open_lock)
    Button urgentBackBtnOpenLock;
    @BindView(R.id.urgent_back_btn_finish)
    Button urgentBackBtnFinish;
    @BindView(R.id.urgent_back_bottom_view)
    LinearLayout urgentBackBottomView;
    Unbinder unbinder;
    private BackDataAdapter backDataAdapter;
    private List<OperBean> operBeanList;
    private MembersBean manage, leader;
    private int index = 0;
    private int pageCount = 7;
    private InitDialog initDialog;
    private List<TaskItemsBean> taskItemsBeans;

    public UrgentBackGunsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UrgentGoActivity) {
            UrgentGoActivity uga = (UrgentGoActivity) context;
            leader = uga.getLeaderData();
            manage = uga.getManageData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urgent_back_guns, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        initDialog = new InitDialog(getContext());
        operBeanList = new ArrayList<>();
        taskItemsBeans = new ArrayList<>();
        urgentBackBtnPrePage.setVisibility(View.INVISIBLE);
        urgentBackBtnNextPage.setVisibility(View.INVISIBLE);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        urgentBackRecyclerView.setLayoutManager(llm);
        backDataAdapter = new BackDataAdapter(operBeanList, index, pageCount);
        urgentBackRecyclerView.setAdapter(backDataAdapter);
        backDataAdapter.setParent(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUrgentBackData();
            }
        }).start();
    }

    private void initPreNextBtn() {
        if (operBeanList.isEmpty()) {
            urgentBackTvCurPage.setText(index + 1 + "/1");
        } else {
            if (operBeanList.size() <= pageCount) {
                urgentBackBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                urgentBackBtnNextPage.setVisibility(View.VISIBLE);
            }
            urgentBackTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        }
    }

    //获取紧急领取的枪弹数据
    private void getUrgentBackData() {
        HttpClient.getInstance().getUrgentBackData(getContext(), leader.getId(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getUrgentBackData onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        taskItemsBeans = JSON.parseArray(body, TaskItemsBean.class);
                        if (!taskItemsBeans.isEmpty()) {
                            for (int i = 0; i < taskItemsBeans.size(); i++) {
                                TaskItemsBean taskItemsBean = taskItemsBeans.get(i);
                                List<OperBean> outGunOpers = taskItemsBean.getOutGunOpers();
                                if (!outGunOpers.isEmpty()) {
                                    for (int j = 0; j < outGunOpers.size(); j++) {
                                        OperBean operBean = outGunOpers.get(j);
                                        String cabId = operBean.getCabId();
                                        int positionType = operBean.getPositionType();
                                        if (cabId.equals(SharedUtils.getGunCabId()) && positionType == 3) {
                                            operBeanList.add(operBean);
                                        }
                                    }
                                }
                            }
                            Log.i(TAG, "onSucceed operBeanList size: " + operBeanList.size());
                            backDataAdapter.setOperList(operBeanList);
                            initPreNextBtn();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed error: " + response.getException().getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.urgent_back_btn_pre_page, R.id.urgent_back_btn_next_page,
            R.id.urgent_back_btn_open_lock, R.id.urgent_back_btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.urgent_back_btn_pre_page:
                prePager();
                break;
            case R.id.urgent_back_btn_next_page:
                nexPager();
                break;
            case R.id.urgent_back_btn_open_lock:
                //开锁并提交归还枪弹数据
                List<OperBean> checkedList = backDataAdapter.getCheckedList();
                if (checkedList.isEmpty()) {
                    Log.i(TAG, "onViewClicked checked list null: ");
                    return;
                }
                postBackData(checkedList);
                break;
            case R.id.urgent_back_btn_finish:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (initDialog != null && !getActivity().isFinishing()) {
            initDialog.dismiss();
        }
    }

    private void postBackData(List<OperBean> list) {
        if (!getActivity().isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("正在提交归还枪支数据。。。");
        Log.i(TAG, "postBackData operBeanList size: " + list.size());
        String jsonStr = JSON.toJSONString(list);
        HttpClient.getInstance().postUrgentBack(getContext(), jsonStr, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postGoTask onSucceed 提交紧急归还枪支数据: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    Log.i(TAG, "onSucceed 提交成功: ");
//                    ToastUtil.showShort("提交成功");
                    initDialog.setTip("提交归还枪支数据成功，正在打开枪柜门。。。");
                    for (int i = 0; i < 2; i++) {
                        SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<OperBean> checkedList = backDataAdapter.getCheckedList();
                            if (!checkedList.isEmpty()) {
                                for (OperBean operBean : checkedList) {
                                    String subCabNo = operBean.getSubCabNo();
                                    for (int i = 0; i < 5; i++) {
                                        SerialPortUtil.getInstance().openLock(subCabNo);
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!taskItemsBeans.isEmpty()) {
                                        for (TaskItemsBean taskItemsBean : taskItemsBeans) {
                                            String id = taskItemsBean.getId();
                                            String taskItemId = operBean.getTaskItemId();
                                            if (id.equals(taskItemId)) {
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manage.getId(),
                                                        leader.getId(),
                                                        Constants.TASK_TYPE_URGENT,
                                                        Constants.OPER_TYPE_BACK_GUN,
                                                        operBean.getObjectId(),
                                                        3);
                                            }
                                        }
                                    }
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog.setTip("所有枪锁打开成功!");
                                    initDialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                        }
                    }).start();
                } else {
                    Log.i(TAG, "onSucceed 提交失败: ");
//                    ToastUtil.showShort("提交失败");
                    initDialog.setTip("提交归还枪支数据失败!");
                    initDialog.dismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LogUtil.i(TAG, "postGoTask onFailed: " + response.getException().getMessage());
                initDialog.setTip("提交归还枪支数据失败!");
                initDialog.dismiss();
            }
        });
    }

    private void prePager() {
        index--;
        backDataAdapter.setIndex(index);
        urgentBackTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        backDataAdapter.setIndex(index);
        urgentBackTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            urgentBackBtnPrePage.setVisibility(View.INVISIBLE);
            urgentBackBtnNextPage.setVisibility(View.VISIBLE);
        } else if (operBeanList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            urgentBackBtnPrePage.setVisibility(View.VISIBLE);
            urgentBackBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            urgentBackBtnNextPage.setVisibility(View.VISIBLE);
            urgentBackBtnPrePage.setVisibility(View.VISIBLE);
        }
    }
}
