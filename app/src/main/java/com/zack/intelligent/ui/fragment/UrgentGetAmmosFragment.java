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

public class UrgentGetAmmosFragment extends Fragment {
    private static final String TAG = "UrgentGetAmmosFragment";

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

    public UrgentGetAmmosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urgent_get_ammos, container, false);
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
                //??????????????????
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
                                    AmmosBean ammos = subCabsBean.getAmmos();
                                    if (ammos != null) {
                                        if (ammos.getObjectNumber() > 0) {
                                            subCabsList.add(subCabsBean);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.urgent_get_btn_pre_page, R.id.urgent_get_btn_next_page,
            R.id.urgent_get_btn_open_lock, R.id.urgent_get_btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.urgent_get_btn_pre_page://?????????
                prePager();
                break;
            case R.id.urgent_get_btn_next_page://?????????
                nexPager();
                break;
            case R.id.urgent_get_btn_open_lock://??????????????????????????????
                List<SubCabsBean> checkedList = urgentDataAdapter.getCheckedList();
                Log.i(TAG, "onViewClicked checked list size: " + checkedList.size());
                if (checkedList.isEmpty()) {
                    ToastUtil.showShort("???????????????????????????");
                    return;
                }
                openLockAndPostTask(checkedList);
                break;
            case R.id.urgent_get_btn_finish://??????
                getActivity().finish();
                break;
        }
    }

    private void prePager() {
        index--;
        urgentDataAdapter.setIndex(index);
        urgentGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        //?????????????????????????????????
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        urgentDataAdapter.setIndex(index);
        urgentGetTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        //?????????????????????????????????
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            urgentGetBtnPrePage.setVisibility(View.INVISIBLE);
            urgentGetBtnNextPage.setVisibility(View.VISIBLE);
        } else if (subCabsList.size() - index * pageCount <= pageCount) {    //??????????????????????????????????????????????????????????????????????????????
            urgentGetBtnPrePage.setVisibility(View.VISIBLE);
            urgentGetBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            urgentGetBtnNextPage.setVisibility(View.VISIBLE);
            urgentGetBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    //?????????????????????
    protected void openLockAndPostTask(List<SubCabsBean> subCabsBeenList) {
        Log.i(TAG, "openLockAndPostTask size: " + subCabsBeenList.size());
        if (!subCabsBeenList.isEmpty()) {
            for (int i = 0; i < subCabsBeenList.size(); i++) {
                SubCabsBean subCabsBean = subCabsBeenList.get(i);
                if (subCabsBean != null) {
                    AmmosBean ammosBean = subCabsBean.getAmmos();
                    if (ammosBean != null) {
                        OperBean operBean = new OperBean();
                        operBean.setManageId(manage.getId());
                        operBean.setCabId(subCabsBean.getCabId());
                        operBean.setSubCabId(subCabsBean.getId());
                        operBean.setSubCabNo(subCabsBean.getNo());
                        operBean.setObjectId(ammosBean.getId());
                        operBean.setObjectTypeId(ammosBean.getObjectTypeId());
                        operBean.setOperType(1);
                        operBean.setOperNumber(ammosBean.getObjectNumber());
                        operBean.setAddTime(System.currentTimeMillis());
                        if (ammosBean.isBox()) {
                            operBean.setPositionType(2);
                        } else {
                            operBean.setPositionType(1);
                        }
                        List<OperBean> outAmmoOperList = new ArrayList<>();
                        outAmmoOperList.add(operBean);

                        //????????????????????????
                        TaskItemsBean taskItemsBean = new TaskItemsBean();
                        taskItemsBean.setTaskPoliceId(leader.getId());
                        taskItemsBean.setTaskPoliceName(leader.getName());
                        taskItemsBean.setTaskItemType(2);
                        taskItemsBean.setTaskItemStatus(2);
                        taskItemsBean.setObjectTypeId(ammosBean.getObjectTypeId());
                        taskItemsBean.setObjectNumber(ammosBean.getObjectNumber());
                        taskItemsBean.setOneOperNumber(ammosBean.getObjectNumber());
                        taskItemsBean.setTwoOperNumber(0);
                        taskItemsBean.setOutGunOpers(outAmmoOperList);
                        taskItemList.add(taskItemsBean);
                        try {
                            if (ammosBean.isBox()) {
                                GreendaoMg.addOperGunsLog(
                                        taskItemsBean,
                                        manage.getId(),
                                        leader.getId(),
                                        Constants.TASK_TYPE_URGENT,
                                        Constants.OPER_TYPE_GET_AMMO,
                                        ammosBean.getId(),
                                        2);
                            } else {
                                GreendaoMg.addOperGunsLog(
                                        taskItemsBean,
                                        manage.getId(),
                                        leader.getId(),
                                        Constants.TASK_TYPE_URGENT,
                                        Constants.OPER_TYPE_GET_AMMO,
                                        ammosBean.getId(),
                                        1);
                            }
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

    //??????????????????????????????
    private void UrgentOper() {
        //????????????????????????
        List<TaskPolicesBean> taskPoliceList = new ArrayList<>();
        TaskPolicesBean taskPolicesBean = new TaskPolicesBean();
        taskPolicesBean.setPoliceId(leader.getId());
        taskPolicesBean.setName(leader.getName());
        taskPolicesBean.setIsComplete(false);
        taskPolicesBean.setTaskItems(taskItemList);
        taskPoliceList.add(taskPolicesBean);

        TaskBean taskBean = new TaskBean();
        taskBean.setRoomId(SharedUtils.getRoomId());
        taskBean.setTaskType(5); //????????????
        taskBean.setApproveLeadId(leader.getId());
        taskBean.setTaskSubType("????????????"); //????????????
        taskBean.setTaskStatus(4); //?????????
        taskBean.setPoliceId(leader.getId()); //??????????????????id
        taskBean.setStartTime(System.currentTimeMillis()); //????????????
        taskBean.setEndTime(System.currentTimeMillis() + 84000000L); //????????????  ??????????????????
        taskBean.setAddTime(System.currentTimeMillis()); //????????????
        taskBean.setIsReport(false);
        taskBean.setRealEndTime(0);
        taskBean.setPolicesBeen(taskPoliceList);
        String jsonBody = JSON.toJSONString(taskBean);
        LogUtil.i(TAG, "submitOper  jsonBody: " + jsonBody);
        submitUrgentTask(jsonBody);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (initDialog != null && !getActivity().isFinishing()) {
            initDialog.dismiss();
        }
    }

    /**
     * ????????????????????????
     *
     * @param jsonBody
     */
    private void submitUrgentTask(String jsonBody) {
        if (!getActivity().isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("???????????????????????????????????????");
        HttpClient.getInstance().postUrgentGetGun(getContext(), jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "?????????????????? onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
//                    ToastUtil.showShort("????????????");
                    initDialog.setTip("???????????????????????????????????????????????????????????????");
                    if (SharedUtils.getGunCabType() == Constants.CAB_TYPE_MIX) {
                        //??????????????????????????????
                        for (int i = 0; i < 2; i++) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getRightCabNo());
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else { //???????????????????????????????????????
                        for (int i = 0; i < 2; i++) {
                            SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    initDialog.setTip("???????????????????????????????????????????????????");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<SubCabsBean> checkedList = urgentDataAdapter.getCheckedList();
                            for (SubCabsBean subCabsBean : checkedList) {
                                String no = subCabsBean.getNo();
                                for (int i = 0; i < 5; i++) {
                                    SerialPortUtil.getInstance().openLock(no);
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog.setTip("?????????????????????");
                                    initDialog.dismiss();
                                    getActivity().finish();
                                }
                            });

                        }
                    }).start();
                } else {
//                    ToastUtil.showShort("????????????");
                    initDialog.setTip("?????????????????????????????????");
                    initDialog.dismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed: " + response.getException().getMessage());
                initDialog.setTip("?????????????????????????????????");
                initDialog.dismiss();
            }
        });
    }
}
