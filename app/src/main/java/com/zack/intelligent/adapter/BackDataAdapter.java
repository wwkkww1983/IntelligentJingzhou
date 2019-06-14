package com.zack.intelligent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.ui.KeepActivity;
import com.zack.intelligent.ui.ScrapActivity;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackDataAdapter extends RecyclerView.Adapter<BackDataAdapter.BackDataViewHolder> {
    private static final String TAG = "BackDataAdapter";
    private List<OperBean> operList;
    private List<OperBean> checkedList;
    private Context mContext;
    private int index;
    private int pageCount;
    private Map<Integer, Boolean> checkStatus;
    private int parent;

    public BackDataAdapter(List<OperBean> operList, int index, int pageCount) {
        this.operList = operList;
        this.index = index;
        this.pageCount = pageCount;
        checkedList = new ArrayList<>();
        checkStatus = new HashMap<>();
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
            checkStatus.put(i, false);
        }
        notifyDataSetChanged();
    }

    @Override
    public BackDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_back_gun,
                parent, false);
        return new BackDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BackDataViewHolder holder, int position) {
        final int pos = position + index * pageCount;
        Log.i(TAG, "onBindViewHolder pos: " + pos + " position: " + position + "  index: " + index);
        holder.taskItemCbBack.setOnCheckedChangeListener(null);

        if (checkStatus.containsKey(pos)) {
            holder.taskItemCbBack.setChecked(checkStatus.get(pos));
        } else {
            Log.i(TAG, "onBindViewHolder checkStatus is not containskey position: " + pos);
        }
        final OperBean operBean = operList.get(pos);
        String subCabNo = operBean.getSubCabNo(); //所在位置编号
        int objectTypeId = operBean.getObjectTypeId(); //物件类型
        String objType = RTool.convertObjectType(objectTypeId);
        final int operNumber = operBean.getOperNumber(); //物件数量
        int operType = operBean.getOperType();//任务状态
        final int positionType = operBean.getPositionType();
        if (positionType == 1) { //枪支归还隐藏 填写归还数量
            holder.taskItemRlBackNum.setVisibility(View.VISIBLE);
        } else {
            if (parent == 2) {
                holder.taskItemRlBackNum.setVisibility(View.GONE);
            } else {
                holder.taskItemRlBackNum.setVisibility(View.INVISIBLE);
            }
        }

        holder.taskItemTvObjectType.setText(objType);
        holder.taskItemTvObjectNum.setText(String.valueOf(operNumber));
        holder.taskItemTvPosition.setText(String.valueOf(subCabNo));

        if (mContext instanceof ScrapActivity) {
            holder.taskItemTvTaskStatus.setText("报废");
            holder.taskItemCbBack.setText("报废");
            holder.taskItemTvObjectNum.setText("1");
            holder.taskItemRlBackNum.setVisibility(View.GONE);
        } else {
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
                    if (positionType == 3 || positionType == 2) {
                        Log.i(TAG, "onCheckedChanged operBean: " + JSON.toJSONString(operBean));
                        checkedList.add(operBean);
                    } else {
                        String backNum = holder.taskItemEdtBackNum.getText().toString();
                        Log.i("backdataadapter", "onCheckedChanged backNum: " + backNum);
                        if (TextUtils.isEmpty(backNum)) {
                            ToastUtil.showShort("请输入归还数量");
                            holder.taskItemCbBack.setChecked(false);
                            return;
                        }
                        int intBackNum = Integer.parseInt(backNum);
                        if (intBackNum < 1) {
                            ToastUtil.showShort("请输入正确的归还数量");
                            holder.taskItemCbBack.setChecked(false);
                            return;
                        }
                        operBean.setOperNumber(intBackNum);
                        checkedList.add(operBean);
                        holder.taskItemEdtBackNum.setEnabled(false);
                        Log.i(TAG, "onCheckedChanged operBean: " + JSON.toJSONString(operBean));
                    }
                } else {
                    //取消选中从集合中移除
                    checkedList.remove(operBean);
                    holder.taskItemEdtBackNum.setEnabled(true);
                }
            }
        });
        holder.taskItemEdtBackNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("urgentBackData", "onTextChanged  s: " + s.toString());
                try {
                    if (!TextUtils.isEmpty(s)) {
                        int num = Integer.parseInt(s.toString());
                        Log.i(TAG, "onTextChanged  num: "+num+"  operNumber:"+operNumber);
                        if (num > operNumber) {//输入数量大于库存数量
                            ToastUtil.showShort("超出领出子弹数量");
                            holder.taskItemEdtBackNum.setText("");
                            return;
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        int current = index * pageCount;
        return operList.size() - current < pageCount ? operList.size() - current : pageCount;
    }

    public void setParent(int i) {
        this.parent = i;
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
        @BindView(R.id.task_item_rl_back_num)
        RelativeLayout taskItemRlBackNum;
        @BindView(R.id.task_item_edt_back_num)
        EditText taskItemEdtBackNum;

        BackDataViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
