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
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ui.dialog.InitDialog;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeepBackFragment extends Fragment {
    private static final String TAG = "KeepBackFragment";
    @BindView(R.id.keep_back_ll_title)
    LinearLayout keepBackLlTitle;
    @BindView(R.id.keep_back_recycler_view)
    RecyclerView keepBackRecyclerView;
    @BindView(R.id.keep_back_btn_unlock)
    Button keepBackBtnUnlock;
    @BindView(R.id.keep_back_btn_finish)
    Button keepBackBtnFinish;
    @BindView(R.id.keep_back_ll_bottom)
    LinearLayout keepBackLlBottom;
    Unbinder unbinder;
    @BindView(R.id.keep_back_btn_pre_page)
    Button keepBackBtnPrePage;
    @BindView(R.id.keep_back_tv_cur_page)
    TextView keepBackTvCurPage;
    @BindView(R.id.keep_back_btn_next_page)
    Button keepBackBtnNextPage;
    private List<OperBean> operBeanList = new ArrayList<>();
//    private BackDataAdapter adapter;
    private int index = 0;
    private int pageCount = 7;
    private KeepGetDataAdapter adapter;
    private InitDialog initDialog;
    private MembersBean manager1, manager2;
    private List<TaskItemsBean> taskItemsBeans;

    public KeepBackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keep_back, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        taskItemsBeans =new ArrayList<>();
        keepBackBtnPrePage.setVisibility(View.INVISIBLE);
        keepBackBtnNextPage.setVisibility(View.INVISIBLE);

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
                            + manager1.getName() + "】和【" + manager2.getName() + "】进入保养归还枪支");
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        keepBackRecyclerView.setLayoutManager(llm);
        adapter = new KeepGetDataAdapter(operBeanList, index, pageCount,2);
        keepBackRecyclerView.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getKeepBackData();
            }
        }).start();
        initDialog =new InitDialog(getContext());
    }

    private void initPreNextBtn() {
        if (operBeanList.isEmpty()) {
            keepBackTvCurPage.setText(index + 1 + "/1");
        } else {
            if (operBeanList.size() <= pageCount) {
                keepBackBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                keepBackBtnNextPage.setVisibility(View.VISIBLE);
            }
            keepBackTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        }
    }

    /**
     * 获取枪支保养归还数据
     */
    private void getKeepBackData() {
        HttpClient.getInstance().getKeepBackData(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "getKeepBackData onSucceed response: " + response.get());
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
                                operBeanList.addAll(outGunOpers);
                            }
                        }
//                        adapter.notifyDataSetChanged();
                        adapter.setOperList(operBeanList);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            unbinder.unbind();
            GreendaoMg.addNormalLog(manager1,
                    2, 7, "【"
                            + manager1.getName() + "】和【" + manager2.getName() + "】退出保养归还枪支");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.keep_back_btn_unlock, R.id.keep_back_btn_finish,
            R.id.keep_back_btn_pre_page, R.id.keep_back_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.keep_back_btn_unlock:
                List<OperBean> checkedList = adapter.getCheckedList();
                if (checkedList.isEmpty()) {
                    Log.i(TAG, "onViewClicked checked list is null: ");
                    return;
                }

                String jsonString = JSON.toJSONString(checkedList);
                Log.i(TAG, "onViewClicked jsonString: " + jsonString);

                if (!Utils.isNetworkAvailable()) {
                    Log.i(TAG, "onViewClicked 网络连接异常: ");
                    return;
                }
                try {
                    postKeepBackData(jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.keep_back_btn_finish:
                getActivity().finish();
                break;
            case R.id.keep_back_btn_pre_page:
                prePager();
                break;
            case R.id.keep_back_btn_next_page:
                nexPager();
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

    private void postKeepBackData(final String jsonString) {
        if (!getActivity().isFinishing() && !initDialog.isShowing()) {
            initDialog.show();
        }
        initDialog.setTip("正在提交归还数据。。。");
        HttpClient.getInstance().postKeepBackData(getContext(), jsonString, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postKeepGetData onSucceed response : " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    Log.i(TAG, "onSucceed 枪支数据提交成功: ");
                    initDialog.setTip("提交还枪数据成功，正在打开枪柜门。。。");
                    for (int i = 0; i < 2; i++) {
                        SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    initDialog.setTip("打开枪柜门成功，正在打开枪锁。。。");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<OperBean> checkedList = adapter.getCheckedList();
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
                                if(!taskItemsBeans.isEmpty()){
                                    for (TaskItemsBean taskItemsBean:taskItemsBeans) {
                                        String id = taskItemsBean.getId();
                                        if(id.equals(taskItemId)){
                                            int positionType = operBean.getPositionType();
                                            if(positionType ==1){//子弹
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manager1.getId(),
                                                        manager2.getId(),
                                                        Constants.TASK_TYPE_KEEP,
                                                        Constants.OPER_TYPE_BACK_GUN,
                                                        operBean.getObjectId(),
                                                        Constants.OBJECT_TYPE_BULLET);

                                            }else if(positionType ==2){//弹匣
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manager1.getId(),
                                                        manager2.getId(),
                                                        Constants.TASK_TYPE_KEEP,
                                                        Constants.OPER_TYPE_BACK_GUN,
                                                        operBean.getObjectId(),
                                                        Constants.OBJECT_TYPE_CLIP);
                                            }else if(positionType ==3){//枪支
                                                GreendaoMg.addOperGunsLog(
                                                        taskItemsBean,
                                                        manager1.getId(),
                                                        manager2.getId(),
                                                        Constants.TASK_TYPE_KEEP,
                                                        Constants.OPER_TYPE_BACK_GUN,
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
                                    initDialog.setTip("枪锁打开完成！");
                                    initDialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                        }
                    }).start();
                } else {
                    Log.i(TAG, "onSucceed 枪支数据提交失败: ");
                    initDialog.setTip("提交还枪数据失败！");
                    initDialog.dismiss();
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                initDialog.setTip("提交还枪数据失败！");
                initDialog.dismiss();
            }
        });
    }

    private void prePager() {
        index--;
        adapter.setIndex(index);
        keepBackTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        adapter.setIndex(index);
        keepBackTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operBeanList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
//        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            keepBackBtnPrePage.setVisibility(View.INVISIBLE);
            keepBackBtnNextPage.setVisibility(View.VISIBLE);
        } else if (operBeanList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            keepBackBtnPrePage.setVisibility(View.VISIBLE);
            keepBackBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            keepBackBtnNextPage.setVisibility(View.VISIBLE);
            keepBackBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

}
