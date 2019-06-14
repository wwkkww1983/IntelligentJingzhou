package com.zack.intelligent.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.zack.intelligent.adapter.UrgentDataAdapter;
import com.zack.intelligent.bean.AmmosBean;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.bean.TaskBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.bean.TaskPolicesBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.dialog.InitDialog;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UrgentGetGunsFragment extends Fragment {
    private static final String TAG = "UrgentGetGunsFragment";

    @BindView(R.id.urgent_get_ll_list_tittle)
    LinearLayout urgentGetLlListTittle;
    @BindView(R.id.urgent_get_recycler_view)
    RecyclerView urgentGetRecyclerView;
    @BindView(R.id.urgent_get_btn_pre_page)
    Button urgentGetBtnPrePage;
    @BindView(R.id.urgent_get_tv_cur_page)
    TextView urgentGetTvCurPage;
    @BindView(R.id.urgent_get_btn_next_page)
    Button urgentGetBtnNextPage;
    @BindView(R.id.urgent_get_btn_open_lock)
    Button urgentGetBtnOpenLock;
    @BindView(R.id.urgent_get_btn_finish)
    Button urgentGetBtnFinish;
    @BindView(R.id.urgent_get_ll_bottom)
    LinearLayout urgentGetLlBottom;
    Unbinder unbinder;
    private UrgentDataAdapter urgentDataAdapter;
    private List<SubCabsBean> subCabsList = new ArrayList<>();
    private List<TaskItemsBean> taskItemList = new ArrayList<>();
    private int index = 0;
    private int pageCount = 7;
    private MembersBean manage, leader;
    private InitDialog initDialog;

    public UrgentGetGunsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urgent_get_guns, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        initDialog = new InitDialog(getContext());
        urgentGetBtnPrePage.setVisibility(View.INVISIBLE);
        urgentGetBtnNextPage.setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        urgentGetRecyclerView.setLayoutManager(llm);
        urgentDataAdapter = new UrgentDataAdapter(subCabsList, index, pageCount);
        urgentGetRecyclerView.setAdapter(urgentDataAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取枪柜数据
                getCabData();
            }
        }).start();
    }

    private void getCabData() {
        HttpClient.getInstance().getCabById(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getCabById onSucceed response : " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                            List<SubCabsBean> subCabList = gunCabsBean.getSubCabs();
                            if (!subCabList.isEmpty()) {
                                for (int i = 0; i < subCabList.size(); i++) {
                                    SubCabsBean subCabsBean = subCabList.get(i);
                                    GunsBean guns = subCabsBean.getGuns();
                                    if (guns != null) {
                                        if (guns.getObjectStatus() == 1) {//枪支在库才显示可领取
                                            subCabsList.add(subCabsBean);//添加到列表中
                                        }
                                    }
                                }
                            }
                            Log.i(TAG, "onSucceed size: " + subCabsList.size());
                            urgentDataAdapter.setSubCabsBeanList(subCabsList);
                            initPreNextBtn();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, " getCabById onFailed: ");
            }
        });
    }

    private void initPreNextBtn() {
        if (subCabsList.isEmpty()) {
            urgentGetTvCurPage.setText(index + 1 + "/1");
        } else {
            if (subCabsList.size() <= pageCount) {
                urgentGetBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                urgentGetBtnNextPage.setVisibility(View.VISIBLE);
            }
            urgentGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof UrgentGoActivity) {
                UrgentGoActivity uga = (UrgentGoActivity) context;
                manage = uga.getManageData();
                leader = uga.getLeaderData();
                Log.i(TAG, "onAttach manage: " + manage.getName() + " leader:" + leader.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (initDialog != null && !getActivity().isFinishing()) {
            initDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.urgent_get_btn_pre_page, R.id.urgent_get_btn_next_page,
            R.id.urgent_get_btn_open_lock, R.id.urgent_get_btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.urgent_get_btn_pre_page://上一页
                prePager();
                break;
            case R.id.urgent_get_btn_next_page://下一页
                nexPager();
                break;
            case R.id.urgent_get_btn_open_lock://提交领枪数据 开锁
                List<SubCabsBean> checkedList = urgentDataAdapter.getCheckedList();
                Log.i(TAG, "onViewClicked checked list size: " + checkedList.size());
                if (checkedList.isEmpty()) {
                    ToastUtil.showShort("没有选择枪支或弹药");
                    return;
                }
                openLockAndPostTask(checkedList);
                break;
            case R.id.urgent_get_btn_finish://返回
                getActivity().finish();
                break;
        }
    }

    private void prePager() {
        index--;
        urgentDataAdapter.setIndex(index);
        urgentGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        urgentDataAdapter.setIndex(index);
        urgentGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            urgentGetBtnPrePage.setVisibility(View.INVISIBLE);
            urgentGetBtnNextPage.setVisibility(View.VISIBLE);
        } else if (subCabsList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            urgentGetBtnPrePage.setVisibility(View.VISIBLE);
            urgentGetBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            urgentGetBtnNextPage.setVisibility(View.VISIBLE);
            urgentGetBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    //开锁并提交数据
    protected void openLockAndPostTask(List<SubCabsBean> subCabsBeenList) {
        Log.i(TAG, "openLockAndPostTask size: " + subCabsBeenList.size());
        if (!subCabsBeenList.isEmpty()) {
            for (int i = 0; i < subCabsBeenList.size(); i++) {
                SubCabsBean subCabsBean = subCabsBeenList.get(i);
                if (subCabsBean != null) {
                    GunsBean gunsBean = subCabsBean.getGuns();
                    if (gunsBean != null) {
                        OperBean operBean = new OperBean();
                        operBean.setManageId(manage.getId());
                        operBean.setCabId(subCabsBean.getCabId());
                        operBean.setObjectId(gunsBean.getId());
                        operBean.setGunNo(gunsBean.getNo());
                        operBean.setGunEno(gunsBean.getEno());
                        operBean.setSubCabId(subCabsBean.getId());
                        operBean.setSubCabNo(subCabsBean.getNo());
                        operBean.setObjectTypeId(gunsBean.getObjectTypeId());
                        operBean.setOperType(1);
                        operBean.setOperNumber(1);
                        operBean.setAddTime(System.currentTimeMillis());
                        operBean.setPositionType(3);
                        List<OperBean> outGunOperList = new ArrayList<>();
                        outGunOperList.add(operBean);

                        TaskItemsBean taskItemsBean = new TaskItemsBean();
                        taskItemsBean.setTaskPoliceId(leader.getId());
                        taskItemsBean.setTaskPoliceName(leader.getName());
                        taskItemsBean.setTaskItemType(1);
                        taskItemsBean.setTaskItemStatus(2);
                        taskItemsBean.setObjectTypeId(gunsBean.getObjectTypeId());
                        taskItemsBean.setObjectNumber(1);
                        taskItemsBean.setOneOperNumber(1);
                        taskItemsBean.setTwoOperNumber(0);
                        taskItemsBean.setOutGunOpers(outGunOperList);
                        taskItemList.add(taskItemsBean);
                        try {
                            GreendaoMg.addOperGunsLog(
                                    taskItemsBean,
                                    manage.getId(),
                                    leader.getId(),
                                    Constants.TASK_TYPE_URGENT,
                                    Constants.OPER_TYPE_GET_GUN,
                                    gunsBean.getId(),
                                    3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            UrgentOper();
            taskItemList.clear();
        }
    }

    //提交紧急出警领取枪弹
    private void UrgentOper() {
        //提交紧急出警数据
        List<TaskPolicesBean> taskPoliceList = new ArrayList<>();
        TaskPolicesBean taskPolicesBean = new TaskPolicesBean();
        taskPolicesBean.setPoliceId(leader.getId());
        taskPolicesBean.setName(leader.getName());
        taskPolicesBean.setIsComplete(false);
        taskPolicesBean.setTaskItems(taskItemList);
        taskPoliceList.add(taskPolicesBean);

        TaskBean taskBean = new TaskBean();
        taskBean.setRoomId(SharedUtils.getRoomId());
        taskBean.setTaskType(5); //紧急出警
        taskBean.setApproveLeadId(leader.getId());
        taskBean.setTaskSubType("其它任务");
        taskBean.setTaskStatus(4); //已审批
        taskBean.setPoliceId(leader.getId()); //申请任务警员id
        taskBean.setStartTime(System.currentTimeMillis()); //开始时间
        taskBean.setEndTime(System.currentTimeMillis() + 84000000L); //结束时间  默认一天时间
        taskBean.setAddTime(System.currentTimeMillis()); //添加时间
        taskBean.setIsReport(false);
        taskBean.setRealEndTime(0); //实际完成时间
        taskBean.setPolicesBeen(taskPoliceList);
        String jsonBody = JSON.toJSONString(taskBean);
        LogUtil.i(TAG, "submitOper  jsonBody: " + jsonBody);
        submitUrgentTask(jsonBody);
    }

    /**
     * 提交紧急出警任务
     *
     * @param jsonBody
     */
    private void submitUrgentTask(String jsonBody) {
        if (!getActivity().isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("正在提交数据");
        HttpClient.getInstance().postUrgentGetGun(getContext(), jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "提交紧急领枪 onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    initDialog.setTip("提交紧急领枪数据成功，正在打开枪柜门。。。");
                    for (int i = 0; i < 2; i++) {
                        SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    initDialog.setTip("枪柜门已打开，正在打开枪锁。。。");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<SubCabsBean> checkedList = urgentDataAdapter.getCheckedList();
                            if (!checkedList.isEmpty()) {
                                for (SubCabsBean subCabsBean : checkedList) {
                                    String no = subCabsBean.getNo();
                                    for (int i = 0; i < 5; i++) {
                                        SerialPortUtil.getInstance().openLock(no);
                                        try {
                                            Thread.sleep(300);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog.setTip("枪锁已全部打开, 请尽快取出枪支");
                                    initDialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                        }
                    }).start();
                } else {
                    initDialog.setTip("提交紧急领枪数据失败！");
                    initDialog.dismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed: " + response.getException().getMessage());
                initDialog.setTip("提交紧急领枪数据失败！");
                initDialog.dismiss();
            }
        });
    }
}
