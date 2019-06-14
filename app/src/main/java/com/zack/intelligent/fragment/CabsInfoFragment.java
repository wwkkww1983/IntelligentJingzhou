package com.zack.intelligent.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.GunInfoAdapter;
import com.zack.intelligent.bean.AmmosBean;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.QueryActivity;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 当前枪柜信息
 */

public class CabsInfoFragment extends Fragment implements GunInfoAdapter.OnItemClickListener {

    private static final String TAG = "CabsInfoFragment";
    @BindView(R.id.cab_info_recycler_view)
    RecyclerView cabInfoRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.cab_info_tv_type)
    TextView cabInfoTvType;
    @BindView(R.id.cab_info_tv_no)
    TextView cabInfoTvNo;
    @BindView(R.id.cab_btn_pre_page)
    Button cabBtnPrePage;
    @BindView(R.id.cab_tv_cur_page)
    TextView cabTvCurPage;
    @BindView(R.id.cab_btn_next_page)
    Button cabBtnNextPage;
    @BindView(R.id.ll_page)
    LinearLayout llPage;
    @BindView(R.id.gun_info_recycler_view)
    RecyclerView gunInfoRecyclerView;
    private GunInfoAdapter gunInfoAdapter;
    private List<SubCabsBean> subCabsList = new ArrayList<>();
    private Map<String, Integer> typeNumMap = new HashMap<>();
    private List<GunType> typeList = new ArrayList<>();

    private int index = 0;
    private int pageCount = 12;
    private GunTypeAdapter gunTypeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cabs_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        cabBtnNextPage.setVisibility(View.INVISIBLE);
        cabBtnPrePage.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCabInfo();
            }
        }).start();
        return view;
    }

    private void getCabInfo() {
        Log.i(TAG, "getCabInfo: ");
        subCabsList.clear();
        typeList.clear();
        HttpClient.getInstance().getCabById(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getCabById onSucceed  response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            GunCabsBean gunCab = JSON.parseObject(body, GunCabsBean.class);
                            initData(gunCab);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed error: " + response.getException().getMessage());
            }
        });
    }

    /**
     * 获取枪柜中的所有子物件
     * 给适配器填充数据
     */
    private void initData(GunCabsBean gunCabsBean) {
        Set<String> gunSet = new HashSet<>();
        String cabType = RTool.convertCabType(gunCabsBean.getCabType());
        String cabNo = gunCabsBean.getNo();
        cabInfoTvType.setText(cabType);//设置枪柜类型
        cabInfoTvNo.setText(cabNo); //设置枪柜编号
        subCabsList = gunCabsBean.getSubCabs();
        if (subCabsList.isEmpty()) {
            return;
        }
//        LogUtil.i(TAG, "initData subCabList: " + JSON.toJSONString(subCabsList));
        for (int j = 0; j < subCabsList.size(); j++) {
            SubCabsBean subCabsBean = subCabsList.get(j);
            GunsBean guns = subCabsBean.getGuns();
            AmmosBean ammos = subCabsBean.getAmmos();
            if (guns != null) {//枪支
                String objType = RTool.convertObjectType(guns.getObjectTypeId());//枪支名称
                if (!TextUtils.isEmpty(objType)) {
                    if (!gunSet.contains(objType)) {//未添加过
                        gunSet.add(objType);
                        typeNumMap.put(objType, 1);
                    } else {
                        Integer count = typeNumMap.get(objType);
                        count++;
                        typeNumMap.put(objType, count);
                    }
                }
            } else if (ammos != null) {//子弹、弹夹
                String objType = RTool.convertObjectType(ammos.getObjectTypeId());//枪支名称
                if (!TextUtils.isEmpty(objType)) {
                    if (!gunSet.contains(objType)) {//未添加过
                        gunSet.add(objType);
                        typeNumMap.put(objType, 1);
                    } else {
                        Integer count = typeNumMap.get(objType);
                        count++;
                        typeNumMap.put(objType, count);
                    }
                }
            }
        }

        for (String key : typeNumMap.keySet()) {
            Integer value = typeNumMap.get(key);
            Log.i(TAG, "initData key: " + key + " value:" + value);
            GunType gunType = new GunType();
            gunType.setTypeName(key);
            gunType.setTypeNum(value);
            typeList.add(gunType);
        }

        Log.i(TAG, "initData subCabList size: " + subCabsList.size() + " typelist size:" + typeList.size());
        Collections.sort(subCabsList);

        gunInfoAdapter = new GunInfoAdapter(subCabsList, index, pageCount);
        cabInfoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        cabInfoRecyclerView.setAdapter(gunInfoAdapter);
        gunInfoAdapter.setOnItemClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        gunInfoRecyclerView.setLayoutManager(llm);
        gunTypeAdapter = new GunTypeAdapter();
        gunInfoRecyclerView.setAdapter(gunTypeAdapter);

        initPreNextBtn();
    }

    private void initPreNextBtn() {
        if (subCabsList.isEmpty()) {
            cabTvCurPage.setText(index + 1 + "/1");
        } else {
            if (subCabsList.size() <= pageCount) {
                cabBtnNextPage.setVisibility(View.INVISIBLE);
            } else {
                cabBtnNextPage.setVisibility(View.VISIBLE);
            }
            cabTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        }
    }
    private List<SubCabsBean>  checkedList =new ArrayList<>();
    @Override
    public void onItemClick(View v, int position) {
        Log.i(TAG, "onItemClick position: "+position);
        //被选中的数据
        if(getActivity() instanceof QueryActivity){
            QueryActivity qa = (QueryActivity) getActivity();
            Set<Integer> positionSet = qa.positionSet;
            if (positionSet.contains(position)) {  //已经被选中 移除
                LogUtil.i(TAG, "onItemClick  REMOVE: ");
                positionSet.remove(position);
                checkedList.remove(subCabsList.get(position));
            } else { //没有被选中的 添加
                LogUtil.i(TAG, "onItemClick  ADD: ");
                positionSet.add(position);
                checkedList.add(subCabsList.get(position));
            }
        }
        LogUtil.i(TAG, "onItemClick checked size: " + checkedList.size());
        gunInfoAdapter.notifyItemChanged(position);
    }

    class GunType {
        String typeName;
        int typeNum;

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public int getTypeNum() {
            return typeNum;
        }

        public void setTypeNum(int typeNum) {
            this.typeNum = typeNum;
        }
    }

    public class GunTypeAdapter extends RecyclerView.Adapter<GunTypeAdapter.GunTypeViewHolder> {
        @Override
        public GunTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gun_cate_list_item, parent, false);
            return new GunTypeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GunTypeViewHolder holder, int position) {
            GunType gunType = typeList.get(position);
            holder.listItemTvGunType.setText(gunType.getTypeName() + ": ");
            holder.listItemTvGunNum.setText("" + gunType.getTypeNum());
            if (position % 2 == 0) {
                holder.gunCateRlRoot.setBackgroundResource(R.color.task_item_default_color);
            } else {
                holder.gunCateRlRoot.setBackgroundResource(R.color.task_item_bg);
            }
        }

        @Override
        public int getItemCount() {
            return typeList.size();
        }

        class GunTypeViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_item_tv_gun_type)
            TextView listItemTvGunType;
            @BindView(R.id.list_item_tv_gun_num)
            TextView listItemTvGunNum;
            @BindView(R.id.gun_cate_rl_root)
            RelativeLayout gunCateRlRoot;

            GunTypeViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.cab_btn_pre_page, R.id.cab_btn_next_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cab_btn_pre_page:
                List<SubCabsBean> selectedList = gunInfoAdapter.getSelectedList();
                String jsonString = JSON.toJSONString(selectedList);
                Log.i(TAG, "onViewClicked  selected list: " + jsonString);
                prePager();
                break;
            case R.id.cab_btn_next_page:
                List<SubCabsBean> selectedList2 = gunInfoAdapter.getSelectedList();
                String jsonString2 = JSON.toJSONString(selectedList2);
                Log.i(TAG, "onViewClicked  selected list: " + jsonString2);
                nexPager();
                break;
        }
    }

    private void prePager() {
        index--;
        gunInfoAdapter.setIndex(index);
        cabTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    private void nexPager() {
        index++;
        gunInfoAdapter.setIndex(index);
        cabTvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) subCabsList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            cabBtnPrePage.setVisibility(View.INVISIBLE);
            cabBtnNextPage.setVisibility(View.VISIBLE);
        } else if (subCabsList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            cabBtnPrePage.setVisibility(View.VISIBLE);
            cabBtnNextPage.setVisibility(View.INVISIBLE);
        } else {
            cabBtnNextPage.setVisibility(View.VISIBLE);
            cabBtnPrePage.setVisibility(View.VISIBLE);
        }
    }


}
