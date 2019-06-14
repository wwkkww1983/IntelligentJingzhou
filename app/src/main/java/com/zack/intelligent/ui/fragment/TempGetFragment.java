package com.zack.intelligent.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.dialog.InitDialog;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.SoundPlayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 临时存放领取枪支
 */
public class TempGetFragment extends Fragment {
    private static final String TAG = "TempGetFragment";

    @BindView(R.id.temp_get_recycler_view)
    RecyclerView tempGetRecyclerView;
    @BindView(R.id.temp_get_btn_confirm)
    Button tempGetBtnConfirm;
    @BindView(R.id.temp_get_btn_finish)
    Button tempGetBtnFinish;
    @BindView(R.id.temp_get_ll_bottom)
    LinearLayout tempGetLlBottom;
    Unbinder unbinder;
    private List<GunsBean> gunsBeanList = new ArrayList<>();
    private List<GunsBean> checkedList = new ArrayList<>();
    private TempDataAdapter tempDataAdapter;
    private MembersBean manager1;
    private MembersBean manager2;

    public TempGetFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TempStoreActivity ac = (TempStoreActivity) context;
        manager1 = ac.getManager1();
        manager2 = ac.getManager2();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp_get, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        initDialog = new InitDialog(getContext());
        getTempData();
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        tempGetRecyclerView.setLayoutManager(llm);
        tempDataAdapter = new TempDataAdapter();
        tempGetRecyclerView.setAdapter(tempDataAdapter);
    }

    private void getTempData() {
        HttpClient.getInstance().getTempGunData(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String body = dataBean.getBody();
                    if (success) {
                        if (!TextUtils.isEmpty(body)) {
                            GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                            List<SubCabsBean> subCabs = gunCabsBean.getSubCabs();
                            if (!subCabs.isEmpty()) {
                                for (SubCabsBean subCabsBean : subCabs) {
                                    GunsBean guns = subCabsBean.getGuns();
                                    //                                AmmosBean ammos = subCabsBean.getAmmos();
                                    if (guns != null) {
                                        gunsBeanList.add(guns);
                                    }
                                }
                                tempDataAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private InitDialog initDialog;

    @OnClick({R.id.temp_get_btn_confirm, R.id.temp_get_btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.temp_get_btn_confirm://确认领出枪支
                if (checkedList.isEmpty()) {
                    SoundPlayUtil.getInstance().play(R.raw.select_gun_ammo);
                    return;
                }

                String jsonString = JSON.toJSONString(checkedList);
                Log.i(TAG, "onViewClicked jsonString: " + jsonString);
                initDialog.setTip("正在提交领枪数据。。。");
                initDialog.show();

                List<Map<String, String>> mapList = new ArrayList<>();
                for (int i = 0; i < checkedList.size(); i++) {
                    GunsBean gunsBean = checkedList.get(i);
                    Map<String, String> gunIdList = new HashMap<>();
                    String id = gunsBean.getId();
                    String subCabId = gunsBean.getSubCabId();
                    gunIdList.put("gunid", id);
                    gunIdList.put("robarkLocationId", subCabId);
                    mapList.add(gunIdList);
                }

                String jsonString1 = JSON.toJSONString(mapList);
                Log.i(TAG, "onViewClicked  jsonString1: " + jsonString1);

                HttpClient.getInstance().postGetTemp(getContext(), jsonString1, new HttpListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) throws JSONException {
                        Log.i(TAG, "postGetTemp onSucceed response: " + response);
                        try {
                            DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                            boolean success = dataBean.isSuccess();
                            if (success) {
                                //提交枪支数据成功
                                initDialog.setTip("提交领枪数据完成，打开枪柜门。。。");
                                //打开枪柜门和枪锁
                                for (int i = 0; i < 2; i++) {
                                    SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                                    Thread.sleep(500);
                                }
                                for (GunsBean gunsBean : checkedList) {
                                    String subCabNo = gunsBean.getSubCabNo();
                                    for (int i = 0; i < 5; i++) {
                                        initDialog.setTip("枪柜门已打开，打开" + subCabNo + "号枪锁");
                                        SerialPortUtil.getInstance().openLock(subCabNo);
                                        Thread.sleep(200);
                                    }

                                    GreendaoMg.addTempStoreGunsLog(
                                            manager1.getId(),
                                            manager2.getId(),
                                            Constants.TASK_TYPE_STORE,
                                            Constants.OPER_TYPE_GET_GUN,
                                            gunsBean.getId(),
                                            Constants.OBJECT_TYPE_GUN,
                                            gunsBean.getObjectTypeId());
                                }
                                initDialog.dismiss();
                                getActivity().finish();
                            } else {
                                initDialog.dismiss();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            initDialog.dismiss();
                        }
                        initDialog.dismiss();
                    }

                    @Override
                    public void onFailed(int what, Response<String> response) {
                        initDialog.dismiss();
                    }
                });
                break;
            case R.id.temp_get_btn_finish://结束
                getActivity().finish();
                break;
        }
    }

    public class TempDataAdapter extends RecyclerView.Adapter<TempDataAdapter.TempGetDataViewHolder> {
        private static final String TAG = "BackDataAdapter";
        private Map<Integer, Boolean> checkStatus;

        public TempDataAdapter() {
            checkStatus = new HashMap<>();
        }

        @Override
        public TempGetDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_temp_get_gun,
                    parent, false);
            return new TempGetDataViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TempGetDataViewHolder holder, final int position) {
            holder.taskItemCbBack.setOnCheckedChangeListener(null);
            if (checkStatus.containsKey(position)) {
                holder.taskItemCbBack.setChecked(checkStatus.get(position));
            }

            final GunsBean gunsBean = gunsBeanList.get(position);
            String subCabNo = gunsBean.getSubCabNo(); //所在位置编号
            int objectTypeId = gunsBean.getObjectTypeId(); //物件类型
            String objType = RTool.convertObjectType(objectTypeId);
            int objectStatus = gunsBean.getObjectStatus();
            String no = gunsBean.getNo();

            holder.tempItemTvGunNo.setText(no);
            holder.tempItemTvGunStatus.setText("临时存放");
            holder.tempItemTvObjectType.setText(objType);
            holder.tempItemTvPosition.setText(subCabNo);
            if (position % 2 == 0) {
                holder.tempListItem.setBackgroundResource(R.color.task_item_bg);
            }

            holder.taskItemCbBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkStatus.put(position, isChecked);
                    if (isChecked) {//添加选中数据
                        checkedList.add(gunsBean);
                    } else {  //取消选中从集合中移除
                        checkedList.remove(gunsBean);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return gunsBeanList.size();
        }

        public class TempGetDataViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.temp_item_tv_object_type)
            TextView tempItemTvObjectType;
            @BindView(R.id.temp_item_tv_position)
            TextView tempItemTvPosition;
            @BindView(R.id.temp_item_tv_gun_status)
            TextView tempItemTvGunStatus;
            @BindView(R.id.temp_item_tv_gun_no)
            TextView tempItemTvGunNo;
            @BindView(R.id.task_item_cb_back)
            CheckBox taskItemCbBack;
            @BindView(R.id.temp_list_item)
            LinearLayout tempListItem;

            TempGetDataViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

}
