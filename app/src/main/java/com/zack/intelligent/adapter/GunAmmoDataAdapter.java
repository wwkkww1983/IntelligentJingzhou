package com.zack.intelligent.adapter;

import android.support.v7.widget.RecyclerView;
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
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 枪弹数据
 */

public class GunAmmoDataAdapter extends RecyclerView.Adapter<GunAmmoDataAdapter.ViewHolder> {

    private List<SubCabsBean> subCabs;
    //    private List<SubCabsBean> selectList = new ArrayList<>();
    private int index;
    private int pageCount;

    public GunAmmoDataAdapter(List<SubCabsBean> list, int pageCount) {
        this.subCabs = list;
        this.pageCount = pageCount;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.gun_ammo_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int pos = position + index * pageCount;
        if (!subCabs.isEmpty()) {
            final SubCabsBean subCabsBean = subCabs.get(pos);
            GunsBean guns = subCabsBean.getGuns();
            AmmosBean ammos = subCabsBean.getAmmos();
            String no = subCabsBean.getNo();
            int subCabNo = Integer.parseInt(no);
            int gunCabType = SharedUtils.getGunCabType();

            holder.itemObjectNo.setText(no);
            int objTypeId = 0;
            if (guns != null) {
                objTypeId = guns.getObjectTypeId();
                int resId = RTool.convertobjTypeToImageId(objTypeId);
                holder.itemIvObjectImg.setImageResource(resId); //设置图片
                String objectName = RTool.convertObjectType(objTypeId);
                holder.itemObjectName.setText(objectName); //设置名称
                int objectStatus = guns.getObjectStatus();
                if (objectStatus == 1 || objectStatus == 6) {//在位
                    switch (gunCabType){
                        case 1://长枪
                            holder.itemIvObjectImg.setImageResource(R.drawable.long_gun); //设置图片
                            break;
                        case 2://弹药
                            break;
                        case 3://综合
                            if(subCabNo >=1 && subCabNo <=25){//短枪
                                holder.itemIvObjectImg.setImageResource(R.drawable.short_gun);
                            }else if(subCabNo >=26 && subCabNo <=29){//长枪
                                holder.itemIvObjectImg.setImageResource(R.drawable.long_gun); //设置图片
                            }
                            break;
                        default://短枪
                            holder.itemIvObjectImg.setImageResource(R.drawable.short_gun);
                            break;
                    }
                } else {//不在位
                    switch (gunCabType){
                        case 1://长枪
                            holder.itemIvObjectImg.setImageResource(R.drawable.long_gun_null); //设置图片
                            break;
                        case 2://弹药
                            break;
                        case 3://综合
                            if(subCabNo >=1 && subCabNo <=25){//短枪
                                holder.itemIvObjectImg.setImageResource(R.drawable.short_gun_null);
                            }else if(subCabNo >=26 && subCabNo <=29){//长枪
                                holder.itemIvObjectImg.setImageResource(R.drawable.long_gun_null); //设置图片
                            }
                            break;
                        default://短枪
                            holder.itemIvObjectImg.setImageResource(R.drawable.short_gun_null);
                            break;
                    }
                }
            } else if (ammos != null) {
                objTypeId = ammos.getObjectTypeId();
                int objectNumber = ammos.getObjectNumber();
                String objectName = RTool.convertObjectType(objTypeId);
                holder.itemObjectNumber.setVisibility(View.VISIBLE);
                holder.itemObjectName.setText(objectName); //设置名称
                holder.itemObjectNumber.setText(objectNumber + "发");
                boolean box = ammos.isBox();
                if (box) {
                    holder.itemIvObjectImg.setImageResource(R.drawable.ic_clip); //设置图片
                } else {
                    holder.itemIvObjectImg.setImageResource(R.drawable.bullet); //设置图片
                }
            } else {
                switch (gunCabType){
                    case 1://长枪
                        holder.itemObjectName.setText("长枪"); //设置名称
                        holder.itemIvObjectImg.setImageResource(R.drawable.long_gun_null); //设置图片
                        break;
                    case 2://弹药
                        holder.itemIvObjectImg.setImageResource(R.drawable.bullet_null); //设置图片
                        holder.itemObjectName.setText("弹药");
                        break;
                    case 3://综合
                        if(subCabNo >=1 && subCabNo <=25){//短枪
                            holder.itemObjectName.setText("短枪"); //设置名称
                            holder.itemIvObjectImg.setImageResource(R.drawable.short_gun_null);
                        }else if(subCabNo >=26 && subCabNo <=29){//长枪
                            holder.itemObjectName.setText("长枪"); //设置名称
                            holder.itemIvObjectImg.setImageResource(R.drawable.long_gun_null); //设置图片
                        }else if(subCabNo >=31 && subCabNo <=32){
                            holder.itemObjectName.setText("弹药");
                            holder.itemIvObjectImg.setImageResource(R.drawable.bullet_null); //设置图片
                        }
                        break;
                    default://短枪
                        holder.itemObjectName.setText("短枪");
                        holder.itemIvObjectImg.setImageResource(R.drawable.short_gun_null); //设置图片
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int current = index * pageCount;
        return subCabs.size() - current < pageCount ? subCabs.size() - current : pageCount;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_object_img)
        ImageView itemIvObjectImg;
        @BindView(R.id.item_object_name)
        TextView itemObjectName;
        @BindView(R.id.item_rl_list_item)
        RelativeLayout itemRlListItem;
        @BindView(R.id.item_object_number)
        TextView itemObjectNumber;
        @BindView(R.id.item_object_no)
        TextView itemObjectNo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
