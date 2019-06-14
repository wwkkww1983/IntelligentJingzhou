package com.zack.intelligent.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
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
import com.zack.intelligent.R;
import com.zack.intelligent.bean.AlarmLog;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 报警日志
 */

public class AlarmInfoFragment extends Fragment {

    private static final String TAG = "AlarmInfoFragment";
    Unbinder unbinder;
    @BindView(R.id.alarm_log_list)
    RecyclerView alarmLogRecyclerview;
    @BindView(R.id.alarm_log_line)
    LinearLayout alarmLogLine;
    @BindView(R.id.alarm_log_btn_pre_page)
    Button alarmLogBtnPrePage;
    @BindView(R.id.alarm_log_tv_cur_page)
    TextView alarmLogTvCurPage;
    @BindView(R.id.alarm_log_btn_next_page)
    Button alarmLogBtnNextPage;
    @BindView(R.id.ll_page)
    LinearLayout llPage;
    private View view;
    private List<AlarmLog> alarmLogList;
    private AlarminfoAdapter alarminfoAdapter;
    private int index = 0;
    private int pageCount = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.warnings_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        getAlarmLog();
        return view;
    }

    /**
     * 获取报警日志
     */
    private void getAlarmLog() {
        HttpClient.getInstance().getLogByCabId(getContext(), 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getAlarmLog onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if(success){
                        String body = dataBean.getBody();
                        if(!TextUtils.isEmpty(body)){
                            List<AlarmLog> alarmLog = JSON.parseArray(body, AlarmLog.class);
                            Log.i(TAG, "onSucceed alarmLogList: "+alarmLog.size());
                            alarmLogList.clear();
                            alarmLogList.addAll(alarmLog);
                            alarminfoAdapter.notifyDataSetChanged();
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
        alarmLogList =new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        alarmLogRecyclerview.setLayoutManager(llm);
        alarminfoAdapter = new AlarminfoAdapter();
        alarmLogRecyclerview.setAdapter(alarminfoAdapter);

        alarmLogBtnNextPage.setVisibility(View.INVISIBLE);
        alarmLogBtnPrePage.setVisibility(View.INVISIBLE);
        if (alarmLogList.isEmpty()) {
            alarmLogTvCurPage.setText(index + 1 + "/1");
        } else {
            if(alarmLogList.size() <=pageCount){
                alarmLogBtnNextPage.setVisibility(View.INVISIBLE);
            }else{
                alarmLogBtnNextPage.setVisibility(View.VISIBLE);
            }
            alarmLogTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) alarmLogList.size() / pageCount));
        }
    }

    @OnClick({ R.id.alarm_log_btn_pre_page, R.id.alarm_log_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.alarm_log_btn_pre_page:
                prePager();
                break;
            case R.id.alarm_log_btn_next_page:
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        alarminfoAdapter.notifyDataSetChanged();
        alarmLogTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) alarmLogList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        alarminfoAdapter.notifyDataSetChanged();
        alarmLogTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) alarmLogList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            alarmLogBtnPrePage.setVisibility(View.INVISIBLE);
            alarmLogBtnNextPage.setVisibility(View.VISIBLE);
        } else if (alarmLogList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            alarmLogBtnPrePage.setVisibility(View.VISIBLE);
            alarmLogBtnNextPage.setVisibility(View.INVISIBLE);
        }else {
            alarmLogBtnNextPage.setVisibility(View.VISIBLE);
            alarmLogBtnPrePage.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 报警数据适配器
     */
    class AlarminfoAdapter extends RecyclerView.Adapter<AlarminfoAdapter.AlarmViewHoloder> {

        @Override
        public AlarmViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_alarm_info, parent, false);
            AlarmViewHoloder holoder = new AlarmViewHoloder(view);
            return holoder;
        }

        @Override
        public void onBindViewHolder(AlarmViewHoloder holder, int position) {
            int pos = position + index * pageCount;
            AlarmLog alarmLog = alarmLogList.get(pos);
//            holder.alarmInfoItemTxtNo.setText(String.valueOf(alarmLog.getId())); //日志Id
            holder.alarmInfoItemTxtNo.setText(String.valueOf(position + 1)); //日志Id
            holder.alarmInfoItemTxtStatus.setText(
                    RTool.convertLogStatus(alarmLog.getLogStatus())); //日志状态
            CharSequence time = DateFormat.format("yyyy-MM-dd HH:mm:ss", alarmLog.getAddTime());
            holder.alarmInfoItemTxtTime.setText(time); //日志时间
            holder.alarmInfoItemTxtType.setText(
                    RTool.convertAlarmLogSubType(alarmLog.getLogSubType())); //子日志类型
            holder.alarmInfoItemTxtContent.setText(alarmLog.getLogContent()); //日志内容
            holder.alarmInfoItemRelievePolice.setText(alarmLog.getDisPoliceName()); //解除警员姓名
        }

        @Override
        public int getItemCount() {
            int current = index * pageCount;
            return alarmLogList.size() - current < pageCount ? alarmLogList.size() - current : pageCount;
        }

        class AlarmViewHoloder extends RecyclerView.ViewHolder {
            @BindView(R.id.alarm_info_item_txt_no)
            TextView alarmInfoItemTxtNo;
            @BindView(R.id.alarm_info_item_txt_type)
            TextView alarmInfoItemTxtType;
            @BindView(R.id.alarm_info_item_txt_time)
            TextView alarmInfoItemTxtTime;
            @BindView(R.id.alarm_info_item_txt_status)
            TextView alarmInfoItemTxtStatus;
            @BindView(R.id.alarm_info_item_txt_content)
            TextView alarmInfoItemTxtContent;
            @BindView(R.id.alarm_info_item_relieve_police)
            TextView alarmInfoItemRelievePolice;

            AlarmViewHoloder(View view) {
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
