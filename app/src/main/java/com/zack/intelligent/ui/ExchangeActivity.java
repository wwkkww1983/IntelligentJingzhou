package com.zack.intelligent.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.BaseActivity;
import com.zack.intelligent.DataCache;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.ManagerListAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.dialog.ChoiceDialog;
import com.zack.intelligent.ui.dialog.ExchangeDialog;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 值班管理
 */
public class ExchangeActivity extends BaseActivity {

    private static final String TAG = ExchangeActivity.class.getSimpleName();
    public static final String EXCHANGE = "1";
    public static final String ONLINE = "2";
    public static final String OFFLINE = "3";
    @BindView(R.id.ex_list_duty_manager)
    public ListView exListDutyManager;
    @BindView(R.id.ex_police_name)
    public TextView exPoliceName;
    @BindView(R.id.ex_police_type)
    public TextView exPoliceType;
    @BindView(R.id.ex_police_number)
    public TextView exPoliceNumber;
    @BindView(R.id.ex_police_political_status)
    public TextView exPolicePoliticalStatus;
    @BindView(R.id.ex_police_phone)
    public TextView exPolicePhone;
    @BindView(R.id.ex_leader_on_duty)
    public TextView exLeaderOnDuty;
    @BindView(R.id.ex_manager_exchange)
    public TextView exManagerExchange;
    @BindView(R.id.ex_manager_online)
    public TextView exManagerOnline;
    @BindView(R.id.ex_manager_offline)
    public TextView exManagerOffline;
    public List<MembersBean> dutyManagerList;
    public List<MembersBean> leadersList;
    private ExchangeDialog exchange;
    public ManagerListAdapter adapter;
    public List<MembersBean> policeList;
    private MembersBean currentPolice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        ButterKnife.bind(this);
        policeList =new ArrayList<>();
        leadersList = new ArrayList<>();

        String police = getIntent().getStringExtra("firstPoliceInfo");
        if(!TextUtils.isEmpty(police)){
            currentPolice =JSON.parseObject(police, MembersBean.class);
            if (currentPolice !=null) {
                GreendaoMg.addNormalOperateLog(currentPolice,
                        7, 7,  "【"+currentPolice.getName() + "】进入【值班交接】");
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                getManagerData();
                loadLeaders();
                getPoliceList();
            }
        }).start();
    }

    private void getPoliceList() {
        HttpClient.getInstance().getPoliceList(this, new HttpListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) throws com.alibaba.fastjson.JSONException {
                        try {
                            String result = response.get();
                            LogUtil.i(TAG, "getPoliceList onSucceed response: "+ result);
                            if(!TextUtils.isEmpty(result)){
                                DataBean dataBean = JSON.parseObject(result, DataBean.class);
                                if(dataBean !=null){
                                    boolean success = dataBean.isSuccess();
                                    String msg = dataBean.getMsg();
                                    if(success){
                                        String body = dataBean.getBody();
                                        policeList = JSON.parseArray(body, MembersBean.class);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailed(int what, Response<String> response) {
                        Log.i(TAG, "onFailed error: "+response.getException().getMessage());

                    }
                });
    }

    /**
     * 获取值班领导数据
     */
    private void loadLeaders() {
        HttpClient.getInstance().getCurrentDuty(this,1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "onSucceed leader data: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        leadersList = JSON.parseArray(dataBean.getBody(), MembersBean.class);
                        initLeaderData(leadersList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private void initListView() {
        adapter = new ManagerListAdapter(dutyManagerList);
        exListDutyManager.setAdapter(adapter);
        adapter.setSelectedPosition(0);
        exListDutyManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
            }
        });
    }

    private void getManagerData() {
        HttpClient.getInstance().getCurrentDuty(this,2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "getManagerData onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            dutyManagerList = JSON.parseArray(body, MembersBean.class);
                            Log.i(TAG, "onSucceed  duty Manager list: " + dutyManagerList.size());
                            initListView();
                        }
                    } else {
                        ToastUtil.showShort(dataBean.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed  error: " + response.getException().getMessage());

            }
        });
    }

    public void initLeaderData(List<MembersBean> currentLeader) {
        if (currentLeader != null && !currentLeader.isEmpty()) {
            exPoliceName.setText(currentLeader.get(0).getName());
            exPoliceType.setText(RTool.convertPoliceType(currentLeader.get(0).getPoliceType()));
            exPoliceNumber.setText(currentLeader.get(0).getNo());
//            exPolicePoliticalStatus.setText(RTool.convertDeparty(currentLeader.get(0).getDeparty()));
            exPolicePhone.setText(currentLeader.get(0).getPhone());
        }
    }

    @OnClick({R.id.ex_leader_on_duty, R.id.ex_manager_exchange, R.id.ex_manager_online,
            R.id.ex_manager_offline, R.id.ac_top_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ex_leader_on_duty: //设置值班领导
                //选择领导
                ChoiceDialog choice = new ChoiceDialog(this, leadersList);
                choice.show();
                break;
            case R.id.ex_manager_exchange: //管理员交接
                exchange = new ExchangeDialog(this, EXCHANGE, policeList);
                exchange.show();
                break;
            case R.id.ex_manager_online:  //管理员上线
                exchange = new ExchangeDialog(this, ONLINE, policeList);
                exchange.show();
                break;
            case R.id.ex_manager_offline: //管理员离线
                exchange = new ExchangeDialog(this, OFFLINE, policeList);
                exchange.show();
                break;
            case R.id.ac_top_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currentPolice !=null){
            GreendaoMg.addNormalOperateLog(currentPolice,
                    7, 8, "【" + currentPolice.getName() + "】退出【值班交接】");
        }
    }
}
