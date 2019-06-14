package com.zack.intelligent.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.zack.intelligent.bean.GunTypeBean;
import com.zack.intelligent.bean.OperGunsLog;
import com.zack.intelligent.db.DBManager;
import com.zack.intelligent.db.gen.OperGunsLogDao;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 领还枪日志
 */

public class OperateInfoFragment extends Fragment {

    private static final String TAG = "OperateInfoFragment";
    @BindView(R.id.operate_info_rv)
    RecyclerView operateInfoRv;
    Unbinder unbinder;
    @BindView(R.id.oper_gun_info_line)
    LinearLayout operGunInfoLine;
    @BindView(R.id.get_gun_btn_pre_page)
    Button getGunBtnPrePage;
    @BindView(R.id.get_gun_tv_cur_page)
    TextView getGunTvCurPage;
    @BindView(R.id.get_gun_btn_next_page)
    Button getGunBtnNextPage;
    @BindView(R.id.ll_page)
    LinearLayout llPage;
    private View view;
    private List<OperGunsLog> operGunsLogList;
    private OperateInfoAdapter operateInfoAdapter;
    private int index =0;
    private int pageCount =10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operation, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getOperGunLog();
            }
        }).start();
        return view;
    }

    /**
     * 获取领还枪日志
     */
    private void getOperGunLog() {
        HttpClient.getInstance().getLogByCabId(getContext(), 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getOperGunLog onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if(success){
                        String body = dataBean.getBody();
                        if(!TextUtils.isEmpty(body)){
                            List<OperGunsLog> operGunsLog = JSON.parseArray(body, OperGunsLog.class);
                            Log.i(TAG, "onSucceed operGunsLogList size: "+operGunsLog.size());
                            operGunsLogList.clear();
                            operGunsLogList.addAll(operGunsLog);
                            operateInfoAdapter.notifyDataSetChanged();
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
        operGunsLogList = new ArrayList<>();
//        long current = System.currentTimeMillis();
//        operGunsLogList = DBManager.getInstance().getOperGunsLogDao().queryBuilder()
//                .where(OperGunsLogDao.Properties.AddTime
//                        .between((current - 86400000), current)).list();
//        Log.i(TAG, "initView list size: " + operGunsLogList.size());
//        if (!operGunsLogList.isEmpty()) {
//            Collections.reverse(operGunsLogList);
//        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        operateInfoRv.setLayoutManager(llm);
        operateInfoAdapter = new OperateInfoAdapter();
        operateInfoRv.setAdapter(operateInfoAdapter);
        getGunBtnNextPage.setVisibility(View.INVISIBLE);
        getGunBtnPrePage.setVisibility(View.INVISIBLE);
        if (operGunsLogList.isEmpty()) {
            getGunTvCurPage.setText(index + 1 + "/1");
        } else {
            if(operGunsLogList.size() <=pageCount){
                getGunBtnNextPage.setVisibility(View.INVISIBLE);
            }else{
                getGunBtnNextPage.setVisibility(View.VISIBLE);
            }
            getGunTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operGunsLogList.size() / pageCount));
        }
    }

    @OnClick({R.id.get_gun_btn_pre_page, R.id.get_gun_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.get_gun_btn_pre_page: //上一页
                prePager();
                break;
            case R.id.get_gun_btn_next_page://下一页
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        operateInfoAdapter.notifyDataSetChanged();
        getGunTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operGunsLogList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: "+index);
    }

    private void nexPager() {
        index++;
        getGunTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) operGunsLogList.size() / pageCount));
        operateInfoAdapter.notifyDataSetChanged();
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: "+index);
    }

    private void checkButton() {
        if (index <= 0) {
            getGunBtnPrePage.setVisibility(View.INVISIBLE);
            getGunBtnNextPage.setVisibility(View.VISIBLE);
        } else if (operGunsLogList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            getGunBtnPrePage.setVisibility(View.VISIBLE);
            getGunBtnNextPage.setVisibility(View.INVISIBLE);
        }else {
            getGunBtnNextPage.setVisibility(View.VISIBLE);
            getGunBtnPrePage.setVisibility(View.VISIBLE);
        }
    }

    class OperateInfoAdapter extends RecyclerView.Adapter<OperateInfoAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_oper_info, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int pos = position + index * pageCount;
            OperGunsLog log = operGunsLogList.get(pos);
            try {
                holder.operInfoItemId.setText(String.valueOf(position + 1));
                holder.operInfoItemPoliceName.setText(log.getPoliceName());
                holder.operInfoItemAddTime.setText(Utils.longTime2String(log.getAddTime()));
                holder.operInfoItemTaskType.setText(RTool.convertTaskType(log.getTaskType())); //任务类型
                holder.operInfoItemObjType.setText(RTool.convertObjectType(log.getObjectTypeId()));
                holder.operInfoItemObjNum.setText(String.valueOf(log.getObjectNum()));
                RTool.convertOperType(log.getOperType());
                holder.operInfoItemOperType.setText(RTool.convertOperType(log.getOperType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            int current = index * pageCount;
            return operGunsLogList.size() - current < pageCount ? operGunsLogList.size() - current : pageCount;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.oper_info_item_id)
            TextView operInfoItemId;
            @BindView(R.id.oper_info_item_police_name)
            TextView operInfoItemPoliceName;
            @BindView(R.id.oper_info_item_add_time)
            TextView operInfoItemAddTime;
            @BindView(R.id.oper_info_item_task_type)
            TextView operInfoItemTaskType;
            @BindView(R.id.oper_info_item_obj_type)
            TextView operInfoItemObjType;
            @BindView(R.id.oper_info_item_obj_num)
            TextView operInfoItemObjNum;
            @BindView(R.id.oper_info_item_oper_type)
            TextView operInfoItemOperType;

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
