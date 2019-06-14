package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.ExchangeActivity;
import com.zack.intelligent.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017-07-04.
 */

public class ChoiceDialog extends Dialog {

    private static final String TAG = ChoiceDialog.class.getSimpleName();
    @BindView(R.id.dl_list_lead)
    ListView dlListLead;
    @BindView(R.id.dl_choice_confirm)
    Button dlChoiceConfirm;
    @BindView(R.id.dl_choice_cancel)
    Button dlChoiceCancel;
    private Unbinder bind;
    private List<MembersBean> leadersList;
    private LeaderListAdapter adapter;
    private MembersBean selectLeader;
    private Context context;
    private List<MembersBean> curLeaderList;

    public ChoiceDialog(@NonNull Context context,List<MembersBean> leaders) {
        super(context, R.style.dialog);
        this.context = context;
        this.curLeaderList =leaders;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dl_choice_lead);
        bind = ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        leadersList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getLeaderByRoomId();
            }
        }).start();

        dlListLead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + position);
                adapter.setCheckedPos(position);
                selectLeader = leadersList.get(position);
                Log.i(TAG, "onItemClick selector: " + selectLeader.getName());
            }
        });
    }

    private void getLeaderByRoomId() {
        HttpClient.getInstance().getLeaderByRoom(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "onSucceed  response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if(success){
                        String body = dataBean.getBody();
                        if(!TextUtils.isEmpty(body)){
                            leadersList = JSON.parseArray(body, MembersBean.class);
                            adapter = new LeaderListAdapter();
                            dlListLead.setAdapter(adapter);
                            if (leadersList !=null && !leadersList.isEmpty()) {
                                for (int i = 0; i < leadersList.size(); i++) {
                                    MembersBean membersBean = leadersList.get(i);
                                    String id = membersBean.getId();
                                    Log.i(TAG, "onSucceed  id: "+id  +" curId:"+curLeaderList.get(0).getId());
                                    if(id.equals(curLeaderList.get(0).getId())){
                                        adapter.setCheckedPos(i);
                                        selectLeader = leadersList.get(i);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed  error: "+response.getException().getMessage());
            }
        });
    }

    @OnClick({R.id.dl_choice_confirm, R.id.dl_choice_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dl_choice_confirm: //设置值班领导
                if (selectLeader != null) {
                    try {
                        setDutyLeader();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "onViewClicked  selectLeader is null: ");
                }
                ChoiceDialog.this.dismiss();
                break;
            case R.id.dl_choice_cancel: //取消
                ChoiceDialog.this.dismiss();
                break;
        }
    }

    private Dialog tipDialog;

    /**
     * 设置值班领导
     */
    private void setDutyLeader() {
        String currentId =null;
        if (curLeaderList != null && !curLeaderList.isEmpty()) {
            currentId = curLeaderList.get(0).getId();
        }
        HttpClient.getInstance().setDuty(getContext(), currentId, selectLeader.getId(), 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if(success){
                        showTipDialog("设置成功");
                    }else{
                        showTipDialog("设置失败");
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

    private void showTipDialog(String msg) {
        tipDialog = DialogUtils.creatTipDialog(context, "提示", msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLeader();
                tipDialog.dismiss();
            }
        });
        tipDialog.show();
    }

    private void getCurrentLeader() {
        HttpClient.getInstance().getCurrentDuty(getContext(), 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        leadersList = JSON.parseArray(dataBean.getBody(), MembersBean.class);
                        if(context instanceof  ExchangeActivity){
                            ExchangeActivity ea = (ExchangeActivity) context;
                            ea.initLeaderData(leadersList);
                            ea.leadersList =leadersList;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getCurrentLeader onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private class LeaderListAdapter extends BaseAdapter {

        private int checkedPos;

        @Override
        public int getCount() {
            return leadersList.size();
        }

        @Override
        public Object getItem(int position) {
            return leadersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setCheckedPos(int position) {
            checkedPos = position;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item_leader, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MembersBean leader = leadersList.get(position);
            holder.name.setText(leader.getName());
            holder.no.setText(leader.getNo());
            if (checkedPos == position) {
                holder.itemLayout.setBackgroundResource(R.color.bg_blue);
                holder.imageSelect.setImageDrawable(
                        parent.getContext().getResources().getDrawable(R.drawable.item_recycler_view_check_64));
            } else {
                holder.itemLayout.setBackgroundDrawable(null);
                holder.imageSelect.setImageDrawable(null);
            }
            return convertView;
        }

        class ViewHolder {
            TextView no;
            TextView name;
            ImageView imageSelect;
            LinearLayout itemLayout;

            public ViewHolder(View view) {
                no = (TextView) view.findViewById(R.id.text_view_no);
                name = (TextView) view.findViewById(R.id.text_view_name);
                imageSelect = (ImageView) view.findViewById(R.id.image_view_item_select);
                itemLayout = (LinearLayout) view.findViewById(R.id.gun_cab_item_layout);
                view.setTag(this);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bind.unbind();
    }
}
