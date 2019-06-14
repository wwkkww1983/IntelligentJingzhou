package com.zack.intelligent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.utils.RTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeepGetDataAdapter extends RecyclerView.Adapter<KeepGetDataAdapter.BackDataViewHolder> {
    private static final String TAG = "BackDataAdapter";
    private List<OperBean> operList;
    private List<OperBean> checkedList;
    private int index;
    private int pageCount;
    private Map<Integer, Boolean> checkStatus;
    private Context context;
    private int fragment;

    public KeepGetDataAdapter(List<OperBean> operList, int index, int pageCount, int fragment) {
        this.operList = operList;
        this.index = index;
        this.pageCount = pageCount;
        this.fragment =fragment;
        checkedList = new ArrayList<>();
        checkStatus =new HashMap<>();
    }

    public List<OperBean> getCheckedList() {
        return checkedList;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public void setOperList(List<OperBean> operList) {
        this.operList = operList;
        for (int i = 0; i < operList.size(); i++) {
            checkStatus.put(i,false);
        }
        notifyDataSetChanged();
    }

    @Override
    public BackDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context =parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_keep_get,
                parent, false);
        return new BackDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BackDataViewHolder holder, int position) {
        final int pos = position + index * pageCount;
        holder.taskItemCbBack.setOnCheckedChangeListener(null);
        if(checkStatus.containsKey(pos)){
            holder.taskItemCbBack.setChecked(checkStatus.get(pos));
        }else{
            Log.i(TAG, "onBindViewHolder checkStatus is not containskey position: "+pos);
        }
        final OperBean operBean = operList.get(pos);
        String subCabNo = operBean.getSubCabNo(); //所在位置编号
        int objectTypeId = operBean.getObjectTypeId(); //物件类型
        String objType = RTool.convertObjectType(objectTypeId);
        final int operNumber = operBean.getOperNumber(); //物件数量
        int operType = operBean.getOperType();//任务状态
        final int positionType = operBean.getPositionType();
        holder.taskItemTvObjectType.setText(objType);
        holder.taskItemTvObjectNum.setText("" + operNumber);
        holder.taskItemTvPosition.setText(subCabNo + "");

        if(fragment ==1){
            holder.taskItemTvTaskStatus.setText("领取");
            holder.taskItemCbBack.setText("领取");
        }else if(fragment ==2){
            holder.taskItemTvTaskStatus.setText("归还");
            holder.taskItemCbBack.setText("归还");
        }

        if (position % 2 == 0) {
            holder.taskItemRoot.setBackgroundResource(R.color.task_item_bg);
        }

        holder.taskItemCbBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus.put(pos, isChecked);
                if (isChecked) {
                    //选中添加到集合
                    Log.i(TAG, "onCheckedChanged operBean: " + JSON.toJSONString(operBean));
                    checkedList.add(operBean);
                } else {
                    //取消选中从集合中移除
                    checkedList.remove(operBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int current = index * pageCount;
        return operList.size() - current < pageCount ? operList.size() - current : pageCount;
    }

    static class BackDataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_item_tv_object_type)
        TextView taskItemTvObjectType;
        @BindView(R.id.task_item_tv_object_num)
        TextView taskItemTvObjectNum;
        @BindView(R.id.task_item_tv_position)
        TextView taskItemTvPosition;
        @BindView(R.id.task_item_tv_task_status)
        TextView taskItemTvTaskStatus;
        @BindView(R.id.task_item_cb_back)
        CheckBox taskItemCbBack;
        @BindView(R.id.task_item_root)
        LinearLayout taskItemRoot;

        BackDataViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
