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

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zack.intelligent.utils.RTool.convertSubCabType;

public class TempInfoAdapter extends RecyclerView.Adapter<TempInfoAdapter.TempInfoViewHolder> {
    private static final String TAG = "GunInfoAdapter";
    private List<SubCabsBean> list;
    private SparseBooleanArray sba;
    private Context context;
    private Bitmap bitmap;

    public TempInfoAdapter(List<SubCabsBean> list) {
        this.list = list;
    }


    public void setList(List<SubCabsBean> list) {
        this.list = list;
        sba = new SparseBooleanArray(list.size());
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                sba.put(i, true);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public TempInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(
                R.layout.recyclerview_gun_item, parent, false);
        return new TempInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TempInfoAdapter.TempInfoViewHolder holder, final int position) {
        final SubCabsBean subCabsBean = list.get(position);
        initData(holder, subCabsBean, sba.get(position));
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
                Log.i(TAG, "onClick  position: " + position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                } else {
                    Log.i(TAG, "onClick  onItemClickListener  is null: ");
                }
            }
        });
    }

    private void initData(TempInfoViewHolder holder, SubCabsBean subCabsBean, boolean enabled) {
        try {
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
                int subCabType = subCabsBean.getSubCabType();//位置类型
                Log.i(TAG, "initData subCabType: " + subCabType);
                String subType = convertSubCabType(subCabType);
                holder.tvItemGunStatus.setText("空位");
                holder.tvItemCabNo.setText(subCabsBean.getNo());
                holder.ivItemGunIcon.setImageResource(R.drawable.short_gun_null);
                holder.tvItemGunType.setText(subType);
                holder.tvItemGunCount.setText("");
//                holder.recyclerLayout.setEnabled(enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setBitmap(TempInfoViewHolder holder, int resId) {
        try {
            bitmap = BitmapUtils.readBitMap(context, resId);
        } catch (Exception e) {
            Log.e(TAG, "GunInfoAdapter setBitmap: " + e.getMessage());
//            e.printStackTrace();
        }
        holder.ivItemGunIcon.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class TempInfoViewHolder extends RecyclerView.ViewHolder {
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

        TempInfoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public TempInfoAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(TempInfoAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setSparseBooleanArray(int key, boolean val) {
        if (sba != null) {
            sba.put(key, val);
        }
        notifyDataSetChanged();
    }
}