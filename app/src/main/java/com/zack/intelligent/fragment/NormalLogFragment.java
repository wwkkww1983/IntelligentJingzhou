package com.zack.intelligent.fragment;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.AlarmLog;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.NormalOperLog;
import com.zack.intelligent.db.DBManager;
import com.zack.intelligent.db.gen.NormalOperLogDao;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;

/**
 * 操作日志.
 */
public class NormalLogFragment extends Fragment {

    private static final String TAG = "NormalLogFragment";
    @BindView(R.id.normal_operate_info_rv)
    RecyclerView normalOperateInfoRv;
    Unbinder unbinder;
    @BindView(R.id.normal_oper_log_line)
    LinearLayout normalOperLogLine;
    @BindView(R.id.operate_btn_pre_page)
    Button operateBtnPrePage;
    @BindView(R.id.operate_tv_cur_page)
    TextView operateTvCurPage;
    @BindView(R.id.operate_btn_next_page)
    Button operateBtnNextPage;
    @BindView(R.id.ll_page)
    LinearLayout llPage;
    private List<NormalOperLog> normalOperLogList =new ArrayList<>();
    private NormalLogAdapter normalLogAdapter;
    private PopupWindow pop;
    private int index = 0;
    private int pageCount = 10;

    public NormalLogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_log, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNormalLog();//获取操作日志
            }
        }).start();
        return view;
    }

    /**
     * 获取操作日志
     */
    private void getNormalLog() {
        HttpClient.getInstance().getLogByCabId(getContext(), 3, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getNormalLog onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if(success){
                        String body = dataBean.getBody();
                        if(!TextUtils.isEmpty(body)){
                            List<NormalOperLog> normalLogList = JSON.parseArray(body, NormalOperLog.class);
                            normalLogAdapter.setList(normalLogList);
                            LogUtil.i(TAG, "onSucceed normalOperLogList: "+JSON.toJSONString(normalLogList));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error: "+response.getException().getMessage());
            }
        });
    }

    private void initView() {
//        normalOperLogList = DBManager.getInstance().getNormalOperLogDao().loadAll();
//        LogUtil.i(TAG, "initView normalOperLogList: " +JSON.toJSONString(normalOperLogList));
//        if (!normalOperLogList.isEmpty()) {
//            Collections.reverse(normalOperLogList);
//        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        normalOperateInfoRv.setLayoutManager(layoutManager);
        normalLogAdapter = new NormalLogAdapter();
        normalOperateInfoRv.setAdapter(normalLogAdapter);

        operateBtnNextPage.setVisibility(View.INVISIBLE);
        operateBtnPrePage.setVisibility(View.INVISIBLE);
        if (normalOperLogList.isEmpty()) {
            operateTvCurPage.setText(index + 1 + "/1");
        } else {
            if(normalOperLogList.size() <=pageCount){
                operateBtnNextPage.setVisibility(View.INVISIBLE);
            }else{
                operateBtnNextPage.setVisibility(View.VISIBLE);
            }
            operateTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) normalOperLogList.size() / pageCount));
        }
    }

    @OnClick({R.id.operate_btn_pre_page, R.id.operate_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.operate_btn_pre_page:
                prePager(); //上一页
                break;
            case R.id.operate_btn_next_page:
                nexPager();//下一页
                break;
        }
    }

    private void prePager() {
        index--;
        normalLogAdapter.notifyDataSetChanged();
        operateTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) normalOperLogList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        normalLogAdapter.notifyDataSetChanged();
        operateTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) normalOperLogList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            operateBtnPrePage.setVisibility(View.INVISIBLE);
            operateBtnNextPage.setVisibility(View.VISIBLE);
        } else if (normalOperLogList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            operateBtnPrePage.setVisibility(View.VISIBLE);
            operateBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            operateBtnNextPage.setVisibility(View.VISIBLE);
            operateBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    class NormalLogAdapter extends RecyclerView.Adapter<NormalLogAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_log, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int pos = position + index * pageCount;
            NormalOperLog normalOperLog = normalOperLogList.get(pos);
//            holder.operInfoItemId.setText(String.valueOf(normalOperLog.getId())); //id
            holder.operInfoItemId.setText(String.valueOf(position + 1)); //id
            holder.operInfoItemPoliceName.setText(normalOperLog.getPoliceName());  //警员姓名
//            String policeType = normalOperLog.getPoliceType();
//            if (!TextUtils.isEmpty(policeType)) {
//                holder.operInfoItemPoliceType.setText(
//                        RTool.convertPoliceType(policeType)); //警员类型
//            }
//            holder.operInfoItemTaskType.setText(
//                    RTool.convertOperTaskType(normalOperLog.getOperTaskType())); //任务类型
//            holder.operInfoItemOperType.setText(
//                    RTool.convertLogSubType(normalOperLog.getLogSubType())); //操作类型
            holder.operInfoItemAddTime.setText(Utils.longTime2String(normalOperLog.getAddTime())); //生成时间
            holder.operInfoItemRemark.setText(normalOperLog.getLogContent()); //日志内容
        }

        @Override
        public int getItemCount() {
            int current = index * pageCount;
            return normalOperLogList.size() - current < pageCount ? normalOperLogList.size() - current : pageCount;
        }

        public void setList(List<NormalOperLog> normalLogList) {
            normalOperLogList.clear();
            normalOperLogList.addAll(normalLogList);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.oper_info_item_id)
            TextView operInfoItemId;
            @BindView(R.id.oper_info_item_police_name)
            TextView operInfoItemPoliceName;
            @BindView(R.id.oper_info_item_police_type)
            TextView operInfoItemPoliceType;
            @BindView(R.id.oper_info_item_oper_type)
            TextView operInfoItemOperType;
            @BindView(R.id.oper_info_item_add_time)
            TextView operInfoItemAddTime;
            @BindView(R.id.oper_info_item_remark)
            TextView operInfoItemRemark;
            @BindView(R.id.oper_info_item_task_type)
            TextView operInfoItemTaskType;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
