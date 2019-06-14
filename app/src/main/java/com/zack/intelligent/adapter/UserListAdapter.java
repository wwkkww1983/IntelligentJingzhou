package com.zack.intelligent.adapter;

import android.util.Log;
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

public class UserListAdapter extends BaseAdapter {
    private static final String TAG = "UserListAdapter";
    private List<MembersBean> policeList;
    private int defPosition = -1;
    private int index;
    private int pageCount;

    public UserListAdapter(List<MembersBean> policeList, int index, int pageCount) {
        this.policeList = policeList;
        this.index = index;
        this.pageCount = pageCount;
    }

    @Override
    public int getCount() {
        int current = index * pageCount;
        return policeList.size() - current < pageCount ? policeList.size() - current : pageCount;
    }

    @Override
    public Object getItem(int position) {
        return policeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int pos = position + index * pageCount;
        MembersBean membersBean = policeList.get(pos);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_user, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemPoliceName.setText(membersBean.getName());
        holder.itemPoliceNumber.setText(membersBean.getNo());
        holder.itemPoliceType.setText(RTool.convertPoliceType(membersBean.getPoliceType()));
        holder.itemPoliceUnit.setText(membersBean.getUnitName());
        holder.itemPolicePhone.setText(membersBean.getPhone());

        return convertView;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    protected class ViewHolder {
        @BindView(R.id.item_police_name)
        TextView itemPoliceName;
        @BindView(R.id.item_police_number)
        TextView itemPoliceNumber;
        @BindView(R.id.item_police_type)
        TextView itemPoliceType;
        @BindView(R.id.item_police_unit)
        TextView itemPoliceUnit;
        @BindView(R.id.item_police_phone)
        TextView itemPolicePhone;
        @BindView(R.id.item_root_view)
        LinearLayout itemRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
