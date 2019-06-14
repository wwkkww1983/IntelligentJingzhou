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
import com.zack.intelligent.bean.StoreBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.utils.RTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InStoreAdapter extends RecyclerView.Adapter<InStoreAdapter.StoreItemViewHolder> {
    private static final String TAG = "InStoreAdapter";

    private List<StoreBean> list;
    private List<StoreBean> checkedList;
    private int index;
    private int pageCount;
    private Map<Integer, Boolean> checkStatus;
    private boolean isGun =false;
    private boolean isAmmo =false;

    public InStoreAdapter(List<StoreBean> list, int index, int pageCount) {
        this.list = list;
        this.index =index;
        this.pageCount =pageCount;
        checkedList = new ArrayList<>();
        checkStatus =new HashMap<>();
    }

    public List<StoreBean> getCheckedList() {
        return checkedList;
    }

    public void setList(List<StoreBean> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            checkStatus.put(i,false);
        }
        notifyDataSetChanged();
    }

    @Override
    public StoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.store_item_list, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StoreItemViewHolder holder, int position) {
        final int pos = position + index * pageCount;
        Log.i(TAG, "onBindViewHolder pos: "+pos +" position: "+position+"  index: "+index);
        holder.taskItemCbSelect.setOnCheckedChangeListener(null);
        if(checkStatus.containsKey(pos)){
            holder.taskItemCbSelect.setChecked(checkStatus.get(pos));
        }else{
            Log.i(TAG, "onBindViewHolder checkStatus is not containskey position: "+pos);
        }
        final StoreBean storeBean = list.get(pos);
        Log.i(TAG, "onBindViewHolder taskItemsBean: " + JSON.toJSONString(storeBean));
        String type = storeBean.getType();
        switch (type){
            case "1": //子弹
                holder.taskItemType.setText("子弹");
                break;
            case "2"://弹夹
                holder.taskItemType.setText("弹夹");
                break;
            case "3"://枪支
                holder.taskItemType.setText("枪支");
                break;
        }

        String typeName = storeBean.getTypeName();
        holder.taskItemTvName.setText(typeName);
        String amount = storeBean.getAmount();
        holder.taskItemTvNum.setText(amount);

        String numbered = storeBean.getNumbered();
        holder.taskItemTvPosition.setText(numbered);

        holder.taskItemCbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus.put(pos, isChecked);
                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged isChecked: ");
                    checkedList.add(storeBean);
                } else {
                    Log.i(TAG, "onCheckedChanged 移除: ");
                    checkedList.remove(storeBean);
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

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_item_root)
        LinearLayout taskItemTvRoot;
        @BindView(R.id.task_item_tv_name)
        TextView taskItemTvName;
        @BindView(R.id.task_item_type)
        TextView taskItemType;
        @BindView(R.id.task_item_tv_num)
        TextView taskItemTvNum;
        @BindView(R.id.task_item_tv_position)
        TextView taskItemTvPosition;
        @BindView(R.id.task_item_cb_select)
        CheckBox taskItemCbSelect;

        StoreItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
