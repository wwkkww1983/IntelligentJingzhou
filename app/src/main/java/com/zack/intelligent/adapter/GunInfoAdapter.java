package com.zack.intelligent.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zack.intelligent.R;
import com.zack.intelligent.bean.AmmosBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.ui.QueryActivity;
import com.zack.intelligent.utils.BitmapUtils;
import com.zack.intelligent.utils.RTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zack.intelligent.utils.RTool.convertSubCabType;

/**
 * 枪支在库列表信息
 */

public class GunInfoAdapter extends RecyclerView.Adapter<GunInfoAdapter.GunInfoViewHolder> {
    private static final String TAG = "GunInfoAdapter";
    private List<SubCabsBean> list;
    private SparseBooleanArray sba;
    private Context context;
    private Bitmap bitmap;
    private int index;
    private int pageCount;
    private boolean flag;
    private List<SubCabsBean> selectedList = new ArrayList<>();

    public GunInfoAdapter(List<SubCabsBean> list, int index, int pageCount) {
        this.list = list;
        this.index = index;
        this.pageCount = pageCount;
        flag = true;
        sba = new SparseBooleanArray(list.size());
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                sba.put(i, false);
            }
        }
    }

    public GunInfoAdapter(List<SubCabsBean> list) {
        flag = false;
        this.list = list;
        sba = new SparseBooleanArray(list.size());
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                sba.put(i, false);
            }
        }
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public List<SubCabsBean> getSelectedList() {
        return selectedList;
    }

    @Override
    public GunInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(
                R.layout.recyclerview_gun_item, parent, false);
        return new GunInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GunInfoViewHolder holder, final int position) {
        final int pos = position + index * pageCount;
        if (flag) {
            SubCabsBean subCabsBean = list.get(pos);
            initData(holder, subCabsBean, true);
        } else {
            final SubCabsBean subCabsBean = list.get(position);
            if (sba.get(position)) { //默认可以选择
                initData(holder, subCabsBean, true);
            } else {//无法选择
                initData(holder, subCabsBean, false);
            }
        }
        if (context instanceof QueryActivity) {
            QueryActivity qa = (QueryActivity) context;
            Set<Integer> positionSet = qa.positionSet;
            if (positionSet.contains(position)) {
                holder.ivItemGunCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivItemGunCheck.setVisibility(View.GONE);
            }
        }
        holder.recyclerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + pos);
                //判断是否被选中 如果选中取消选中 未选中则显示选中图标
//                if (holder.ivItemGunCheck.getVisibility() == View.VISIBLE) {
//                    holder.ivItemGunCheck.setVisibility(View.GONE);
//                    selectedList.remove(list.get(position)); //取消选中从list中移除数据
//                } else {
//                    holder.ivItemGunCheck.setVisibility(View.VISIBLE);
//                    selectedList.add(list.get(position)); //添加到list集合
//                }
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, pos);
                }
            }
        });
    }

    private void initData(GunInfoViewHolder holder, SubCabsBean subCabsBean, boolean enabled) {
        this.holder = holder;
        GunsBean gunsBean = subCabsBean.getGuns();
        AmmosBean ammosBean = subCabsBean.getAmmos();
        if (gunsBean != null) {
//            Log.i(TAG, "initData gunsBean: " + JSON.toJSONString(gunsBean));
            holder.tvItemGunStatus.setText(RTool.convertObjectStatus(gunsBean.getObjectStatus()));
            holder.tvItemCabNo.setText(subCabsBean.getNo());
            holder.recyclerLayout.setEnabled(enabled);
            //设置图片
            holder.tvItemGunType.setText(RTool.convertObjectType(gunsBean.getObjectTypeId()));
            holder.tvItemGunCount.setText("编号:" + gunsBean.getNo());
            if (!enabled) {
                setBitmap(holder, RTool.convertGunToGreyImageId(gunsBean.getObjectTypeId()));
            } else {
                setBitmap(holder, RTool.convertobjTypeToImageId(gunsBean.getObjectTypeId()));
            }
        } else if (ammosBean != null) {
//            Log.i(TAG, "initData ammoBean: " + JSON.toJSONString(ammosBean));
            holder.tvItemGunStatus.setText(RTool.convertObjectStatus(ammosBean.getObjectStatus()));
            holder.tvItemCabNo.setText(subCabsBean.getNo());
            if (ammosBean.isBox()) { //是弹夹
                if (enabled) {
                    setBitmap(holder, R.drawable.ic_clip);
                } else {
                    setBitmap(holder, R.drawable.ic_clip_grey);
                }
            } else {//不是弹夹
                if (!enabled) {
                    setBitmap(holder, R.drawable.bullet_null);
                } else {
                    setBitmap(holder, R.drawable.bullet);
                }
            }
            holder.recyclerLayout.setEnabled(enabled);
            holder.tvItemGunType.setText(RTool.convertObjectType(ammosBean.getObjectTypeId()));
            holder.tvItemGunCount.setText("数量:" + ammosBean.getObjectNumber());
        } else {
            String cabNo = subCabsBean.getCabNo();
            int subCabType = subCabsBean.getSubCabType();//位置类型
            String subType = convertSubCabType(subCabType);
            holder.tvItemGunStatus.setText("空位");
            holder.tvItemCabNo.setText(subCabsBean.getNo());
            holder.ivItemGunIcon.setImageDrawable(null);

            if (cabNo.equals("1")) {//长枪柜
                holder.tvItemGunType.setText("枪支位置");
            } else if (cabNo.equals("2")) {//短枪柜
                holder.tvItemGunType.setText("枪支位置");
            } else if (cabNo.equals("3")) {//子弹柜
                holder.tvItemGunType.setText("弹药位置");
            }
            holder.tvItemGunCount.setText("");
            holder.recyclerLayout.setEnabled(enabled);
        }

        if (holder.tvItemGunStatus.getText().equals("正常在库")) {
            holder.tvItemGunStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else if (holder.tvItemGunStatus.getText().equals("出警领出")) {
            holder.tvItemGunStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        } else if (holder.tvItemGunStatus.getText().equals("保养领出")) {
            holder.tvItemGunStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        } else if (holder.tvItemGunStatus.getText().equals("紧急出警领出")) {
            holder.tvItemGunStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        } else if (holder.tvItemGunStatus.getText().equals("异常不在位")) {
            holder.tvItemGunStatus.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.tvItemGunStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    private GunInfoViewHolder holder;

    private GunInfoViewHolder getHolder() {
        return holder;
    }

    public void setDefaultImg(int id) {
        holder.ivItemGunIcon.setImageResource(id);
    }

    public void setBitmap(GunInfoAdapter.GunInfoViewHolder holder, int resId) {
        try {
            bitmap = BitmapUtils.readBitMap(context, resId);
        } catch (Exception e) {
            Log.e(TAG, "GunInfoAdapter setBitmap: " + e.getMessage());
//            e.printStackTrace();
        }
        holder.ivItemGunIcon.setImageBitmap(bitmap);
    }

    public void setSparseBooleanArray(int key, boolean val) {
        if (sba != null) {
            sba.put(key, val);
        }
    }

    @Override
    public int getItemCount() {
        if (flag) {
            int current = index * pageCount;
            return list.size() - current < pageCount ? list.size() - current : pageCount;
        } else {
            return list.size();
        }
    }

    static class GunInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_gun_status)
        TextView tvItemGunStatus;
        @BindView(R.id.tv_item_cab_no)
        TextView tvItemCabNo;
        @BindView(R.id.iv_item_gun_icon)
        ImageView ivItemGunIcon;
        @BindView(R.id.tv_item_gun_type)
        TextView tvItemGunType;
        @BindView(R.id.tv_item_gun_count)
        TextView tvItemGunCount;
        @BindView(R.id.recycler_layout)
        RelativeLayout recyclerLayout;
        @BindView(R.id.iv_item_gun_check)
        ImageView ivItemGunCheck;

        GunInfoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
