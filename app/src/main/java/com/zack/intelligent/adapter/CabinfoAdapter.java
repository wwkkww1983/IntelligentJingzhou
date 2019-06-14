package com.zack.intelligent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zack.intelligent.R;
import com.zack.intelligent.bean.ObjectType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/20.
 */

public class CabinfoAdapter extends BaseAdapter {

    private List<ObjectType> gunTypeList;

    public CabinfoAdapter(List<ObjectType> gunTypeList) {
        this.gunTypeList = gunTypeList;
    }

    @Override
    public int getCount() {
        return gunTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return gunTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       CabinfoAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gun_cate_list_item, parent, false);
            holder = new CabinfoAdapter.ViewHolder(convertView);
        } else {
            holder = (CabinfoAdapter.ViewHolder) convertView.getTag();
        }

        ObjectType gunType = gunTypeList.get(position);
        int typeId = gunType.getTypeId();
        if(String.valueOf(typeId).charAt(0) == '1'){
            holder.listItemTvGunNum.setText(gunType.getTypeNum()+"支");
        }else if(String.valueOf(typeId).charAt(0) == '2'){
            holder.listItemTvGunNum.setText(gunType.getTypeNum()+"发");
        }else if(String.valueOf(typeId).charAt(0) == '3'){
            holder.listItemTvGunNum.setText(gunType.getTypeNum()+"发");
        }
        holder.listItemTvGunType.setText(gunType.getTypeName());
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.list_item_tv_gun_type)
        TextView listItemTvGunType;
        @BindView(R.id.list_item_tv_gun_num)
        TextView listItemTvGunNum;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
