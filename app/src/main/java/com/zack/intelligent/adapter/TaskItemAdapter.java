package com.zack.intelligent.adapter;

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
import com.zack.intelligent.bean.AmmosBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.TaskItemViewHolder> {
    private static final String TAG = "TaskItemAdapter";

    private List<TaskItemsBean> list;
    private List<TaskItemsBean> checkedList;
    private int index;
    private int pageCount;
    private Map<Integer, Boolean> checkStatus;

    public TaskItemAdapter(List<TaskItemsBean> list, int index, int pageCount) {
        this.list = list;
        this.index =index;
        this.pageCount =pageCount;
        checkedList = new ArrayList<>();
        checkStatus =new HashMap<>();
    }

    public List<TaskItemsBean> getCheckedList() {
        return checkedList;
    }

    public void setList(List<TaskItemsBean> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            checkStatus.put(i,false);
        }
        notifyDataSetChanged();
    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.task_item_list, parent, false);
        return new TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskItemViewHolder holder, int position) {
        final int pos = position + index * pageCount;
        Log.i(TAG, "onBindViewHolder pos: "+pos +" position: "+position+"  index: "+index);
        holder.taskItemCbGetBack.setOnCheckedChangeListener(null);
        if(checkStatus.containsKey(pos)){
            holder.taskItemCbGetBack.setChecked(checkStatus.get(pos));
        }else{
            Log.i(TAG, "onBindViewHolder checkStatus is not containskey position: "+pos);
        }
        final TaskItemsBean taskItemsBean = list.get(pos);
        Log.i(TAG, "onBindViewHolder taskItemsBean: " + JSON.toJSONString(taskItemsBean));
        String taskPoliceName = taskItemsBean.getTaskPoliceName();
        int taskItemType = taskItemsBean.getTaskItemType();
        int taskItemStatus = taskItemsBean.getTaskItemStatus();
        final int objectTypeId = taskItemsBean.getObjectTypeId();
        final int objectNumber = taskItemsBean.getObjectNumber();

        holder.taskItemTvName.setText(taskPoliceName);
        if (taskItemType == 1) {
            holder.taskItemTvTaskType.setText("领取枪支");
        } else if (taskItemType == 2) {
            holder.taskItemTvTaskType.setText("领取弹药");
        }

        if (taskItemStatus == 1) {
            holder.taskItemTvTaskStatus.setText("领取");
        }

        String objectType = RTool.convertObjectType(objectTypeId);
        holder.taskItemTvObjectType.setText(objectType);
        holder.taskItemTvObjectNum.setText("" + objectNumber);

        holder.taskItemCbGetBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus.put(pos, isChecked);
                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged isChecked: ");
                    checkedList.add(taskItemsBean);
                } else {
                    Log.i(TAG, "onCheckedChanged 移除: ");
                    checkedList.remove(taskItemsBean);
                }
            }
        });

        if (position % 2 == 0) {
            holder.taskItemTvRoot.setBackgroundResource(R.color.task_item_bg);
        }
    }

    @Override
    public int getItemCount() {
        int current = index * pageCount;
        return list.size() - current < pageCount ? list.size() - current : pageCount;
    }

    public void setIndex(int index) {
        this.index =index;
        notifyDataSetChanged();
    }

    static class TaskItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_item_root)
        LinearLayout taskItemTvRoot;
        @BindView(R.id.task_item_tv_name)
        TextView taskItemTvName;
        @BindView(R.id.task_item_tv_task_type)
        TextView taskItemTvTaskType;
        @BindView(R.id.task_item_tv_task_status)
        TextView taskItemTvTaskStatus;
        @BindView(R.id.task_item_tv_object_type)
        TextView taskItemTvObjectType;
        @BindView(R.id.task_item_tv_object_num)
        TextView taskItemTvObjectNum;
        @BindView(R.id.task_item_cb_get_back)
        CheckBox taskItemCbGetBack;

        TaskItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
