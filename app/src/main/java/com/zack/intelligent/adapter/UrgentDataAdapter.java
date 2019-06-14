package com.zack.intelligent.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.AmmosBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UrgentDataAdapter extends RecyclerView.Adapter <UrgentDataAdapter.UrgentDataViewHolder>{
    private static final String TAG = "UrgentDataAdapter";

    private List<SubCabsBean> subCabsBeanList;
    private List<SubCabsBean> checkedList =new ArrayList<>();
    private int index;
    private int pageCount;
    private Map<Integer, Boolean> checkStatus;//用来记录所有checkbox的状态

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public UrgentDataAdapter(List<SubCabsBean> subCabsBeanList, int index, int pageCount) {
        this.subCabsBeanList = subCabsBeanList;
        this.index =index;
        this.pageCount =pageCount;
        checkStatus = new HashMap<>();//用来记录所有checkbox的状态
    }

    @Override
    public UrgentDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_urgent_get,
                parent, false);
        return new UrgentDataViewHolder(view);
    }

    public List<SubCabsBean> getCheckedList() {
        return checkedList;
    }

    public void setSubCabsBeanList(List<SubCabsBean> subCabsBeanList) {
        this.subCabsBeanList = subCabsBeanList;
        for (int i = 0; i < subCabsBeanList.size(); i++) {
            checkStatus.put(i, false);// 默认所有的checkbox都是没选中
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final UrgentDataViewHolder holder, int position) {
        final int pos = position + index * pageCount;
        final SubCabsBean subCabsBean = subCabsBeanList.get(pos);
//        Log.i(TAG, "onBindViewHolder  subCabBean: "+ JSON.toJSONString(subCabsBean));

        holder.listItemCbGet.setOnCheckedChangeListener(null);
        if(checkStatus.containsKey(pos)){
            holder.listItemCbGet.setChecked(checkStatus.get(pos));
        }
        if(subCabsBean !=null){
            final GunsBean guns = subCabsBean.getGuns();
            final AmmosBean ammos = subCabsBean.getAmmos();
            if(guns !=null){
                int objectTypeId = guns.getObjectTypeId();
                String objectType = RTool.convertObjectType(objectTypeId);
                int objectStatus = guns.getObjectStatus();
                String objStatus = RTool.convertObjectStatus(objectStatus);
                String subCabNo = guns.getSubCabNo();
                holder.listItemTvType.setText(objectType);
                holder.listItemTvStatus.setText(objStatus);
                holder.listItemTvPosition.setText(subCabNo);
                holder.listItemEdtCount.setInputType(InputType.TYPE_NULL);
                holder.listItemEdtCount.setText(String.valueOf(1));
                holder.listItemRlGetNum.setVisibility(View.GONE);
                holder.listItemEdtGetNum.setText(String.valueOf(1));
            }else if(ammos !=null){
                holder.listItemRlGetNum.setVisibility(View.VISIBLE);
                final int objectNumber = ammos.getObjectNumber();
                int objectTypeId = ammos.getObjectTypeId();
                String subCabNo = ammos.getSubCabNo();
                String objectType = RTool.convertObjectType(objectTypeId);
                holder.listItemTvType.setText(objectType);
                holder.listItemTvStatus.setText("正常在库");
                holder.listItemTvPosition.setText(subCabNo);
                holder.listItemEdtCount.setInputType(InputType.TYPE_NULL);
                holder.listItemEdtCount.setText(String.valueOf(objectNumber));
                final String objectNum = holder.listItemEdtGetNum.getText().toString();
                if(!TextUtils.isEmpty(objectNum)){
                    ammos.setObjectNumber(Integer.valueOf(objectNum));
                }else{
                    ammos.setObjectNumber(0);
                }
                holder.listItemEdtGetNum.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.i(TAG, "onTextChanged s: "+s);
                        if(!TextUtils.isEmpty(s)){
                            int num = Integer.parseInt(s.toString());
                            if(num > objectNumber){//输入数量大于库存数量
                                ToastUtil.showShort("超出可领取子弹数量");
                                holder.listItemEdtGetNum.setText("");
                                return;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
            holder.listItemCbGet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkStatus.put(pos, isChecked);//check状态一旦改变，保存的check值也要发生相应的变化
                    if(isChecked){
                        if(ammos !=null){
                            String num = holder.listItemEdtGetNum.getText().toString();
                            if(TextUtils.isEmpty(num)){
                                ToastUtil.showShort("请输入领取数量");
                                holder.listItemCbGet.setChecked(false);
                                return;
                            }else{
                                int getNum = Integer.parseInt(num);
                                if(getNum >0){
                                    ammos.setObjectNumber(getNum); //领取数量
                                    checkedList.add(subCabsBean);
                                    holder.listItemEdtGetNum.setEnabled(false);
                                    Log.i(TAG, "onCheckedChanged subCabsBean: "+JSON.toJSONString(subCabsBean) );
                                }else{
                                    ToastUtil.showShort("输入的领取数量有误");
                                    holder.listItemCbGet.setChecked(false);
                                    return;
                                }
                            }
                        }else if(guns !=null){
                            checkedList.add(subCabsBean);
                        }
                    }else{
                        holder.listItemEdtGetNum.setEnabled(true);
                        checkedList.remove(subCabsBean);
                    }
                }
            });
        }

        if(position%2 ==0){
            holder.listItemLlRoot.setBackgroundResource(R.color.task_item_bg);
        }
    }

    @Override
    public int getItemCount() {
        int current = index * pageCount;
        return subCabsBeanList.size() - current < pageCount ? subCabsBeanList.size() - current : pageCount;
    }

    static class UrgentDataViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.list_item_tv_type)
        TextView listItemTvType;
        @BindView(R.id.list_item_tv_status)
        TextView listItemTvStatus;
        @BindView(R.id.list_item_tv_position)
        TextView listItemTvPosition;
        @BindView(R.id.list_item_edt_count)
        EditText listItemEdtCount;
        @BindView(R.id.list_item_edt_get_num)
        EditText listItemEdtGetNum;
        @BindView(R.id.list_item_cb_get)
        CheckBox listItemCbGet;
        @BindView(R.id.list_item_ll_root)
        LinearLayout listItemLlRoot;
        @BindView(R.id.list_item_rl_get_num)
        RelativeLayout listItemRlGetNum;

        UrgentDataViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
