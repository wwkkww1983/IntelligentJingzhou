package com.zack.intelligent.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.TempInfoAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.TempStoreActivity;
import com.zack.intelligent.ui.dialog.TemporaryDialog;
import com.zack.intelligent.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 临时存放枪支
 */
public class TempInFragment extends Fragment {
    private static final String TAG = "TempInFragment";
    @BindView(R.id.temp_in_tittle)
    TextView tempInTittle;
    @BindView(R.id.temp_in_recycler_view)
    RecyclerView tempInRecyclerView;
    @BindView(R.id.temp_in_finish)
    Button tempInFinish;
    @BindView(R.id.temp_in_ll_view)
    RelativeLayout tempInLlView;
    Unbinder unbinder;
    private TempInfoAdapter adapter;
    private List<SubCabsBean> subCabs;
    private MembersBean manager1;
    private MembersBean manager2;

    public TempInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp_in, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        subCabs = new ArrayList<>();
        getTempStorePosition();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        tempInRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new TempInfoAdapter(subCabs);
        tempInRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TempInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                Log.i(TAG, "onItemClick  position:"+position);
                //点击设置临时存放枪支数据
                SubCabsBean subCabsBean = subCabs.get(position);
                if(subCabsBean !=null){
                    Log.i(TAG, "onItemClick subCabsBean: "+JSON.toJSONString(subCabsBean));
                    TemporaryDialog temporaryDialog = new TemporaryDialog(getContext(),
                            subCabsBean, manager1, manager2,
                            new TemporaryDialog.OnPostListener() {
                                @Override
                                public void onPostFinished(boolean isSuccess) {
                                    if (isSuccess) {
                                        adapter.setSparseBooleanArray(position, false);
                                        adapter.notifyItemChanged(position);
                                    }
                                }
                            });
                    if (!temporaryDialog.isShowing()) {
                        temporaryDialog.show();
                    }
                }
            }
        });
    }

    /**
     * 获取临时存放枪支的位置
     */
    private void getTempStorePosition() {
        HttpClient.getInstance().getTempPosition(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getTempStorePosition onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                if (dataBean != null) {
                    String body = dataBean.getBody();
                    String msg = dataBean.getMsg();
                    boolean success = dataBean.isSuccess();
                    if (!TextUtils.isEmpty(body)) {
                        GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                        subCabs = gunCabsBean.getSubCabs();
                        Log.i(TAG, "onSucceed subCabs size: "+subCabs.size());
                        adapter.setList(subCabs);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TempStoreActivity ac = (TempStoreActivity) context;
        manager1 = ac.getManager1();
        manager2 = ac.getManager2();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.temp_in_finish)
    public void onViewClicked() {
        getActivity().finish();
    }

}
