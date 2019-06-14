package com.zack.intelligent.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.DataCache;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.RoomBean;
import com.zack.intelligent.event.EventConsts;
import com.zack.intelligent.event.MessageEvent;
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.dialog.UpdateDialog;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.RTool;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 基本设置
 */
public class BasicFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = BasicFragment.class.getSimpleName();
    @BindView(R.id.basic_et_server_ip)
    EditText basicEtServerIp;
    @BindView(R.id.bt_set_save)
    Button btSetSave;
    Unbinder unbinder;
    @BindView(R.id.btn_update_member_data)
    Button btnUpdateMemberData;
    @BindView(R.id.btn_debug)
    Button btnDebug;
    @BindView(R.id.basic_edt_password)
    EditText basicEdtPassword;
    @BindView(R.id.list_view_guncab_list)
    ListView listViewGuncabList;
    @BindView(R.id.basic_cb_fingerprint)
    CheckBox basicCbFingerprint;
    @BindView(R.id.btn_sync_system_time)
    Button btnSyncSystemTime;
    @BindView(R.id.basic_txt_msg)
    TextView basicTxtMsg;
    @BindView(R.id.basic_txt_version)
    TextView basicTxtVersion;
    @BindView(R.id.basic_rb_bio)
    RadioButton basicRbBio;
    @BindView(R.id.basic_rb_pwd)
    RadioButton basicRbPwd;
    @BindView(R.id.basic_cb_alarm_switch)
    CheckBox basicCbAlarmSwitch;
    @BindView(R.id.basic_cb_vein)
    CheckBox basicCbVein;
    @BindView(R.id.basic_cb_iris)
    CheckBox basicCbIris;
    @BindView(R.id.basic_cb_alcohol)
    CheckBox basicCbAlcohol;
    @BindView(R.id.basic_cb_humiture)
    CheckBox basicCbHumiture;
    @BindView(R.id.basic_cb_face)
    CheckBox basicCbFace;
    @BindView(R.id.basic_rb_first_finger)
    RadioButton basicRbFirstFinger;
    @BindView(R.id.basic_rb_first_vein)
    RadioButton basicRbFirstVein;
    @BindView(R.id.basic_rb_first_iris)
    RadioButton basicRbFirstIris;
    @BindView(R.id.basic_rb_first_face)
    RadioButton basicRbFirstFace;
    @BindView(R.id.basic_rb_second_finger)
    RadioButton basicRbSecondFinger;
    @BindView(R.id.basic_rb_second_vein)
    RadioButton basicRbSecondVein;
    @BindView(R.id.basic_rb_second_iris)
    RadioButton basicRbSecondIris;
    @BindView(R.id.basic_rb_second_face)
    RadioButton basicRbSecondFace;
    @BindView(R.id.basic_rb_bios_finger)
    RadioButton basicRbBiosFinger;
    @BindView(R.id.basic_rb_bios_vein)
    RadioButton basicRbBiosVein;
    @BindView(R.id.basic_rb_bios_iris)
    RadioButton basicRbBiosIris;
    @BindView(R.id.basic_rb_bios_face)
    RadioButton basicRbBiosFace;
    @BindView(R.id.basic_ll_bios_select)
    LinearLayout basicLlBiosSelect;
    @BindView(R.id.basic_tv_room_name)
    TextView basicTvRoomName;
    @BindView(R.id.basic_et_room_name)
    EditText basicEtRoomName;
    @BindView(R.id.basic_tv_server_ip)
    TextView basicTvServerIp;
    @BindView(R.id.basic_tv_server_port)
    TextView basicTvServerPort;
    @BindView(R.id.basic_et_server_port)
    EditText basicEtServerPort;
    private View view;
    public GunCabListAdapter cabListAdapter;
    private List<GunCabsBean> gunCabList = new ArrayList<>();

    public BasicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_basic, container, false);
        unbinder = ButterKnife.bind(this, view);
        Log.i(TAG, "onCreateView: ");
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    private void initView() {
        basicCbFingerprint.setOnCheckedChangeListener(this);
        basicCbVein.setOnCheckedChangeListener(this);
        basicCbIris.setOnCheckedChangeListener(this);
        basicCbFace.setOnCheckedChangeListener(this);
        basicCbAlarmSwitch.setOnCheckedChangeListener(this);
        basicCbAlcohol.setOnCheckedChangeListener(this);
        basicCbHumiture.setOnCheckedChangeListener(this);

        basicRbBio.setOnCheckedChangeListener(this);
        basicRbPwd.setOnCheckedChangeListener(this);
        basicRbFirstFinger.setOnCheckedChangeListener(this);
        basicRbFirstVein.setOnCheckedChangeListener(this);
        basicRbFirstIris.setOnCheckedChangeListener(this);
        basicRbFirstFace.setOnCheckedChangeListener(this);
        basicRbSecondFinger.setOnCheckedChangeListener(this);
        basicRbSecondVein.setOnCheckedChangeListener(this);
        basicRbSecondIris.setOnCheckedChangeListener(this);
        basicRbSecondFace.setOnCheckedChangeListener(this);

        basicRbBiosFinger.setOnCheckedChangeListener(this);
        basicRbBiosVein.setOnCheckedChangeListener(this);
        basicRbBiosIris.setOnCheckedChangeListener(this);
        basicRbBiosFace.setOnCheckedChangeListener(this);

        basicEtServerIp.setText(SharedUtils.getServerIp());
        basicEtServerPort.setText(SharedUtils.getServerPort());
        basicEtRoomName.setText(SharedUtils.getRoomName());
        basicCbFingerprint.setChecked(SharedUtils.getFingerOpen());
        basicCbVein.setChecked(SharedUtils.getVeinOpen());
        basicCbIris.setChecked(SharedUtils.getIrisOpen());
        basicCbFace.setChecked(SharedUtils.getFaceOpen());
        basicCbAlarmSwitch.setChecked(SharedUtils.getAlarmOpen());
        basicCbAlcohol.setChecked(SharedUtils.getAlcoholDetect());
        basicCbHumiture.setChecked(SharedUtils.getHumitureOpen());
        if (SharedUtils.getBioLogin()) {
            basicRbBio.setChecked(true);
        } else {
            basicRbPwd.setChecked(true);
        }

        switch (SharedUtils.getFirstVerify()) {
            case Constants.DEVICE_FINGER:
                basicRbFirstFinger.setChecked(true);
                break;
            case Constants.DEVICE_VEIN:
                basicRbFirstVein.setChecked(true);
                break;
            case Constants.DEVICE_IRIS:
                basicRbFirstIris.setChecked(true);
                break;
            case Constants.DEVICE_FACE:
                basicRbFirstFace.setChecked(true);
                break;
        }

        switch (SharedUtils.getSecondVerify()) {
            case Constants.DEVICE_FINGER:
                basicRbSecondFinger.setChecked(true);
                break;
            case Constants.DEVICE_VEIN:
                basicRbSecondVein.setChecked(true);
                break;
            case Constants.DEVICE_IRIS:
                basicRbSecondIris.setChecked(true);
                break;
            case Constants.DEVICE_FACE:
                basicRbSecondFace.setChecked(true);
                break;
        }

        switch (SharedUtils.getBiosVerify()) {
            case Constants.DEVICE_FINGER:
                basicRbBiosFinger.setChecked(true);
                break;
            case Constants.DEVICE_VEIN:
                basicRbBiosVein.setChecked(true);
                break;
            case Constants.DEVICE_IRIS:
                basicRbBiosIris.setChecked(true);
                break;
            case Constants.DEVICE_FACE:
                basicRbBiosFace.setChecked(true);
                break;
        }

        basicEtServerIp.setText(SharedUtils.getServerIp());
        basicEtServerPort.setText(SharedUtils.getServerPort());
        try {
            PackageInfo pi = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            basicTxtVersion.setText("智能枪弹库管理系统V" + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        try {
            if (messageEvent.getMessage().equals(EventConsts.SYNC_TIME_SUCCESS)) { //同步成功
                basicTxtMsg.setText("同步时间成功");
            } else if (messageEvent.getMessage().equals(EventConsts.SYNC_TIME_FAILED)) { //同步失败
                basicTxtMsg.setText("同步时间失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initCabListData(); //初始化枪柜数据
    }

    private void initCabListData() {
        Log.i(TAG, "initListView: ");
        cabListAdapter = new GunCabListAdapter();
        listViewGuncabList.setAdapter(cabListAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCabs();
            }
        }).start();
    }

    /**
     * 获取所有枪柜
     */
    private void getCabs() {
        HttpClient.getInstance().getCabByRoom(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getCabByRoom onSucceed response: "+response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String body = dataBean.getBody();
                    if (success) {
                        if (!TextUtils.isEmpty(body)) {
                            List<GunCabsBean> gunCabs = JSON.parseArray(body, GunCabsBean.class);
                            gunCabList.clear();
                            gunCabList.addAll(gunCabs);
                            cabListAdapter.notifyDataSetChanged();
                            if (!gunCabList.isEmpty()) {
                                //保存默认枪柜id和编号
                                String gunCabId = SharedUtils.getGunCabId();
                                String gunCabNo = SharedUtils.getGunCabNo();
                                GunCabsBean gunCabsBean = gunCabList.get(0);
                                if (TextUtils.isEmpty(gunCabId)) { //枪柜id
//                                    cabListAdapter.setSelectItem(0);
                                    SharedUtils.saveGunCabType(gunCabsBean.getCabType()); //枪柜类型
                                    SharedUtils.saveGunCabId(gunCabsBean.getId());
                                }
                                if (TextUtils.isEmpty(gunCabNo)) { //枪柜编号
                                    SharedUtils.saveGunCabNo(gunCabsBean.getNo());
                                }
                            }
                            //默认选中枪柜
                            int selectedCabPosition = SharedUtils.getSelectedCabPosition();
                            cabListAdapter.setSelectItem(selectedCabPosition);
                        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 保存服务器地址 获取当前枪库的数据
     */
    private void savaBaseUrlAndRoomId() {
        String serverIp = basicEtServerIp.getText().toString();
        String serverPort = basicEtServerPort.getText().toString();
        Log.i(TAG, "savaBaseUrlAndRoomId serverIp: " + serverIp + " serverPort:" + serverPort);
        if (TextUtils.isEmpty(serverIp)) {
            SoundPlayUtil.getInstance().play(R.raw.enter_ip);
            return;
        }

        if (TextUtils.isEmpty(serverPort)) {
            SoundPlayUtil.getInstance().play(R.raw.enter_port);
            return;
        }
        SharedUtils.saveServerIp(serverIp);
        SharedUtils.saveServerPort(serverPort);
        Log.i(TAG, "savaBaseUrlAndRoomId serverUrl: http://" + SharedUtils.getServerIp() + ":" + SharedUtils.getServerPort());
        getRoomData("http://" + serverIp + ":" + serverPort);//获取枪柜名称
    }

    /**
     * 获取枪库名称
     *
     * @param baseUrl
     */
    private void getRoomData(String baseUrl) {
        HttpClient.getInstance().getRoomByServerUrl(getContext(), baseUrl, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getRoomList onSucceed  response : " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            RoomBean roomBean = JSON.parseObject(body, RoomBean.class);
                            LogUtil.i(TAG, "onSucceed  roomBean: " + JSON.toJSONString(roomBean));
                            if (roomBean != null) {
                                String roomId = roomBean.getId();
                                String roomName = roomBean.getName();
                                basicEtRoomName.setText(roomName);
                                SharedUtils.saveRoomId(roomId);
                                SharedUtils.saveRoomName(roomName);
                                showDialog("保存成功");
                            } else {
                                showDialog("枪库数据获取失败");
                            }
                        }
                    } else {
                        showDialog("获取枪库数据失败");
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

    private Dialog dialog;

    private void showDialog(String msg) {
        if (dialog != null) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
            DialogUtils.setTipText(msg);
            Log.i(TAG, "dialog is not null ");
        } else { //dialog为null
            dialog = DialogUtils.creatTipDialog(getContext(), "提示", msg,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //获取最新的数据 并刷新适配器
                            DataCache.getInstance().loadData(getContext(), null);
                            dialog.dismiss();
                        }
                    });
            if (!dialog.isShowing()) {
                dialog.show();
            }
            Log.i(TAG, "dialog is null");
        }
    }

    private UpdateDialog update;

    @OnClick({R.id.btn_update_member_data, R.id.btn_debug, R.id.bt_set_save,
            R.id.btn_sync_system_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_set_save: //保存
                try {
                    savaBaseUrlAndRoomId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_update_member_data: //更新数据
                //1.当前数据更新进度 是否更新完成 是否获取错误 把指纹数据写入到指纹仪
                if (update == null) {
                    update = new UpdateDialog(getContext());
                    if (!update.isShowing()) {
                        update.show();
                    }
                } else {
                    if (!update.isShowing()) {
                        update.show();
                        update.initView();
                    }
                }
                break;
            case R.id.btn_debug:  //打开/关闭调试
                String debugPwd = basicEdtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(debugPwd)) {
                    return;
                }
                if (debugPwd.equals("197315")) {
                    Constants.isDebug = true;
                }
                break;
            case R.id.btn_sync_system_time: //同步系统时间
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSystemTime();
                    }
                }).start();
                break;
        }
    }

    private void getSystemTime() {
        HttpClient.getInstance().getSystemTime(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "getSystemTime onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    String body = dataBean.getBody();
                    if (!TextUtils.isEmpty(body)) {
                        long currentTime = Utils.stringTime2Long(body);
                        boolean isSetTime = SystemClock.setCurrentTimeMillis(currentTime);
                        if (isSetTime) {
                            showDialog("同步系统时间成功");
                        } else {
                            showDialog("同步系统时间失败");
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getSystemTime onFailed error: " + response.getException().getMessage());
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        Log.i(TAG, "onCheckedChanged isChecked: " + isChecked);
        switch (id) {
            case R.id.basic_cb_fingerprint://指纹
                Log.i(TAG, "onCheckedChanged  指纹: ");
                SharedUtils.saveFingerOpen(isChecked);
                if (isChecked) {
                    basicCbVein.setChecked(false);
                }
                break;
            case R.id.basic_cb_vein://指静脉
                Log.i(TAG, "onCheckedChanged  指静脉: ");
                SharedUtils.saveVeinOpen(isChecked);
                if (isChecked) {
                    basicCbFingerprint.setChecked(false);
                }
                break;
            case R.id.basic_cb_iris://虹膜
                Log.i(TAG, "onCheckedChanged  虹膜: ");
                SharedUtils.saveIrisOpen(isChecked);
                break;
            case R.id.basic_cb_face://人脸
                Log.i(TAG, "onCheckedChanged  人脸: ");
                SharedUtils.saveFaceOpen(isChecked);
                break;
            case R.id.basic_cb_alarm_switch: //报警
                Log.i(TAG, "onCheckedChanged  报警: ");
                SharedUtils.setAlarmOpen(isChecked);
                break;
            case R.id.basic_cb_alcohol://酒精检测
                Log.i(TAG, "onCheckedChanged  酒精检测: ");
                SharedUtils.saveAlcoholDetect(isChecked);
                break;
            case R.id.basic_cb_humiture://温湿度检测
                Log.i(TAG, "onCheckedChanged  温湿度检测: ");
                SharedUtils.saveHumitureOpen(isChecked);
                break;
        }

        if (isChecked) {
            switch (id) {
                case R.id.basic_rb_bio:
                    Log.i(TAG, "onCheckedChanged bios: ");
                    SharedUtils.setBioLogin(true);
                    break;
                case R.id.basic_rb_pwd:
                    Log.i(TAG, "onCheckedChanged password: ");
                    SharedUtils.setBioLogin(false);
                    break;
                case R.id.basic_rb_first_finger:
                    SharedUtils.saveFirstVerify(Constants.DEVICE_FINGER);
                    break;
                case R.id.basic_rb_first_vein:
                    SharedUtils.saveFirstVerify(Constants.DEVICE_VEIN);
                    break;
                case R.id.basic_rb_first_iris:
                    SharedUtils.saveFirstVerify(Constants.DEVICE_IRIS);
                    break;
                case R.id.basic_rb_first_face:
                    SharedUtils.saveFirstVerify(Constants.DEVICE_FACE);
                    break;
                case R.id.basic_rb_second_finger:
                    Log.i(TAG, "onCheckedChanged second finger: ");
                    SharedUtils.saveSecondVerify(Constants.DEVICE_FINGER);
                    break;
                case R.id.basic_rb_second_vein:
                    Log.i(TAG, "onCheckedChanged second vein: ");
                    SharedUtils.saveSecondVerify(Constants.DEVICE_VEIN);
                    break;
                case R.id.basic_rb_second_iris:
                    Log.i(TAG, "onCheckedChanged second iris: ");
                    SharedUtils.saveSecondVerify(Constants.DEVICE_IRIS);
                    break;
                case R.id.basic_rb_second_face:
                    Log.i(TAG, "onCheckedChanged second face: ");
                    SharedUtils.saveSecondVerify(Constants.DEVICE_FACE);
                    break;
                case R.id.basic_rb_bios_finger:
                    SharedUtils.saveBiosVerify(Constants.DEVICE_FINGER);
                    break;
                case R.id.basic_rb_bios_vein:
                    SharedUtils.saveBiosVerify(Constants.DEVICE_VEIN);
                    break;
                case R.id.basic_rb_bios_iris:
                    SharedUtils.saveBiosVerify(Constants.DEVICE_IRIS);
                    break;
                case R.id.basic_rb_bios_face:
                    SharedUtils.saveBiosVerify(Constants.DEVICE_FACE);
                    break;
            }
        }
    }

    private class GunCabListAdapter extends BaseAdapter {
        private List<GunCabsBean> cabsBeanList =new ArrayList<>();
        int checkedPos;

        public GunCabListAdapter(){

        }

        @Override
        public int getCount() {
            return gunCabList.size();
        }

        @Override
        public GunCabsBean getItem(int position) {
            return gunCabList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSelectItem(int position) {
            this.checkedPos = position;
            notifyDataSetChanged();
        }

        public void setList(List<GunCabsBean> gunCabsBeanList){
            this.cabsBeanList =gunCabsBeanList;
            notifyDataSetChanged();
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.gun_cab_item,
                        parent, false);
                viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.gun_cab_item_layout);
                viewHolder.cabNo = (TextView) convertView.findViewById(R.id.text_view_cab_no);
                viewHolder.cabType = (TextView) convertView.findViewById(R.id.text_view_cab_type);
                viewHolder.imageSelect = (ImageView) convertView.findViewById(R.id.image_view_item_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final GunCabsBean gunCabsBean = gunCabList.get(position);
            String no = gunCabsBean.getNo();
            final int cabType = gunCabsBean.getCabType();
            final String convertCabType = RTool.convertCabType(cabType);
            viewHolder.cabNo.setText(no);
            viewHolder.cabType.setText(convertCabType);
            viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SharedUtils.saveSelectedCabPosition(position);
                        if (convertCabType.equals("综合柜")) {
                            SharedUtils.setIsSynthesisCab(true);
                        } else {
                            SharedUtils.setIsSynthesisCab(false);
                        }
                        Log.i(TAG, "onClick gunCabItem cabId: " + gunCabsBean.getId()
                                + " cabNo:" + gunCabsBean.getNo() + " cabType:" + gunCabsBean.getCabType());
                        SharedUtils.saveGunCabNo(gunCabsBean.getNo());
                        SharedUtils.saveGunCabId(gunCabsBean.getId());
                        SharedUtils.saveGunCabType(gunCabsBean.getCabType());
                        checkedPos = position;
                        GunCabListAdapter.this.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if (position == checkedPos) {
                viewHolder.itemLayout.setBackgroundResource(R.color.task_item_bg);
                viewHolder.imageSelect.setImageDrawable(
                        getResources().getDrawable(R.drawable.item_recycler_view_check_64));
            } else {
                viewHolder.itemLayout.setBackground(null);
                viewHolder.imageSelect.setImageDrawable(null);
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView cabNo;
            private TextView cabType;
            private ImageView imageSelect;
            private LinearLayout itemLayout;
        }
    }

}
