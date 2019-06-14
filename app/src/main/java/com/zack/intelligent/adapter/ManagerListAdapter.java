package com.zack.intelligent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.intelligent.R;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.utils.RTool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */

public class ManagerListAdapter extends BaseAdapter{

    private int selectedPosition;
    private List<MembersBean> dutyManagerList;

    public ManagerListAdapter(List<MembersBean> dutyManagerList) {
        this.dutyManagerList = dutyManagerList;
    }

    @Override
    public int getCount() {
        return dutyManagerList.size();
    }

    @Override
    public Object getItem(int position) {
        return dutyManagerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public void setList(List<MembersBean > dutyManagers){
        this.dutyManagerList =dutyManagers;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.police_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (dutyManagerList != null) { //填充数据
            MembersBean dutyManager = dutyManagerList.get(position);
            holder.itemPoliceName.setText(dutyManager.getName());
            holder.itemPoliceNumber.setText(dutyManager.getNo());
            holder.itemPoliceType.setText(RTool.convertPoliceType(dutyManager.getPoliceType()));
        }
//
        if (selectedPosition == position) {
            holder.itemRootView.setBackgroundResource(R.color.bg_blue);
        } else {
            holder.itemRootView.setBackgroundDrawable(null);
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.item_police_name)
        TextView itemPoliceName;
        @BindView(R.id.item_police_number)
        TextView itemPoliceNumber;
        @BindView(R.id.item_police_type)
        TextView itemPoliceType;
        @BindView(R.id.item_root_view)
        LinearLayout itemRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
