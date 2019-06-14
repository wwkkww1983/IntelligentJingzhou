package com.zack.intelligent.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.TransformUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 枪锁配置
 */
public class LockFragment extends Fragment implements SerialPortUtil.OnDataReceiveListener {

    private static final String TAG = "LockFragment";
    @BindView(R.id.btn_query_lock_exist)
    Button btnQueryLockExist;
    @BindView(R.id.btn_query_lock_status)
    Button btnQueryLockStatus;
    @BindView(R.id.btn_open_lock)
    Button btnOpenLock;
    @BindView(R.id.btn_open_all_lock)
    Button btnOpenAllLock;
    @BindView(R.id.edt_query_lock_exist)
    EditText edtQueryLockExist;
    @BindView(R.id.edt_query_lock_status)
    EditText edtQueryLockStatus;
    @BindView(R.id.edt_open_lock)
    EditText edtOpenLock;
    Unbinder unbinder;
    @BindView(R.id.btn_open_cab_lock)
    Button btnOpenCabLock;
    @BindView(R.id.edt_set_lock_address)
    EditText edtSetLockAddress;
    @BindView(R.id.btn_set_lock_address)
    Button btnSetLockAddress;
    @BindView(R.id.edt_lock_receive_msg)
    EditText edtLockReceiveMsg;
    @BindView(R.id.edt_set_bullet_weight)
    EditText edtSetBulletWeight;
    @BindView(R.id.btn_set_bullet_weight)
    Button btnSetBulletWeight;
    @BindView(R.id.edt_read_bullet_weight)
    EditText edtReadBulletWeight;
    @BindView(R.id.btn_read_bullet_weight)
    Button btnReadBulletWeight;
    @BindView(R.id.edt_read_bullet_count)
    EditText edtReadBulletCount;
    @BindView(R.id.btn_read_bullet_count)
    Button btnReadBulletCount;
    @BindView(R.id.edt_set_tare)
    EditText edtSetTare;
    @BindView(R.id.btn_set_tare)
    Button btnSetTare;
    @BindView(R.id.edt_open_new_lock)
    EditText edtOpenNewLock;
    @BindView(R.id.lock_btn_clear_recv)
    Button lockBtnClearRecv;
    @BindView(R.id.et_left_cab_no)
    EditText etLeftCabNo;
    @BindView(R.id.et_right_cab_no)
    EditText etRightCabNo;
    @BindView(R.id.btn_set_cab_no)
    Button btnSetCabNo;
    private List<SubCabsBean> subCabsList;

    public LockFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lock, container, false);
        unbinder = ButterKnife.bind(this, view);

        SerialPortUtil.getInstance().onCreate();
        SerialPortUtil.getInstance().setOnDataReceiveListener(this);
        subCabsList = new ArrayList<>();
        //获取当前枪柜数据
        getCabById();
        return view;
    }

    /**
     * 获取枪柜数据
     */
    private void getCabById() {
        HttpClient.getInstance().getCabById(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getCabById onSucceed response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    String body = dataBean.getBody();
                    GunCabsBean gunCabsBean = JSON.parseObject(body, GunCabsBean.class);
                    subCabsList = gunCabsBean.getSubCabs();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed: " + response.getException().getMessage());

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private int type;
    private Map<String, Boolean> openStatus = new HashMap<>();
    Dialog choiceDialog;

    @OnClick({R.id.btn_query_lock_exist, R.id.btn_query_lock_status, R.id.btn_open_lock,
            R.id.btn_open_all_lock, R.id.btn_open_cab_lock,
            R.id.btn_set_lock_address, R.id.btn_set_bullet_weight,
            R.id.btn_read_bullet_weight, R.id.btn_read_bullet_count, R.id.btn_set_tare,
            R.id.lock_btn_clear_recv, R.id.btn_set_cab_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_query_lock_exist: //查询锁在不在
                type = 1;
                String exist = edtQueryLockExist.getText().toString();
                if (!TextUtils.isEmpty(exist)) {
                    SerialPortUtil.getInstance().isLockExist(exist);
                }
                break;
            case R.id.btn_query_lock_status://查询锁状态
                type = 2;
                String status = edtQueryLockStatus.getText().toString();
                if (!TextUtils.isEmpty(status)) {
                    SerialPortUtil.getInstance().checkStatus(status);
                }
                break;
            case R.id.btn_open_lock://开锁
                type = 3;
                String no = edtOpenLock.getText().toString();
                if (!TextUtils.isEmpty(no)) {
                    SerialPortUtil.getInstance().openLock(no);
                }
                break;
            case R.id.btn_open_all_lock: //打开所有枪锁
                type = 5;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!subCabsList.isEmpty()) {
                            for (SubCabsBean subCabsBean : subCabsList) {
                                String subCabNo = subCabsBean.getNo();
                                if (!TextUtils.isEmpty(subCabNo)) {
                                    openStatus.put(subCabNo, false);
                                    for (int i = 0; i < 5; i++) {
                                        SerialPortUtil.getInstance().openLock(subCabNo);
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        setStatusTxt("打开所有枪锁");
                    }
                }).start();
                break;
            case R.id.btn_open_cab_lock: //打开枪柜
                type = 6;
                SerialPortUtil.getInstance().openLock(SharedUtils.getLeftCabNo());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SerialPortUtil.getInstance().openLock(SharedUtils.getRightCabNo());

                setStatusTxt("打开枪柜门");
                break;
            case R.id.btn_set_lock_address: //设置锁地址
                type = 7;
                choiceDialog = DialogUtils.createChoiceDialog(getContext(), "", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = edtSetLockAddress.getText().toString();
                        if (!TextUtils.isEmpty(address)) {
                            SerialPortUtil.getInstance().setAddress(address);
                        }
                        choiceDialog.dismiss();
                    }
                });
                choiceDialog.show();
                break;
            case R.id.btn_set_bullet_weight: //设置子弹重量
                type = 9;
                String boxNo = edtSetBulletWeight.getText().toString().trim();
                if (!TextUtils.isEmpty(boxNo)) {
                    SerialPortUtil.getInstance().setBulletWeight(boxNo);
                }

                break;
            case R.id.btn_read_bullet_weight: //读取子弹重量
                type = 10;
                String bNo = edtReadBulletWeight.getText().toString().trim();
                if (!TextUtils.isEmpty(bNo)) {
                    SerialPortUtil.getInstance().readBulletWeight(bNo);
                }
                break;
            case R.id.btn_read_bullet_count: //读取子弹个数
                type = 11;
                String trim = edtReadBulletCount.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    SerialPortUtil.getInstance().readBulletCount(trim);
                }
                break;
            case R.id.btn_set_tare://设置皮重
                type = 12;
                String trim1 = edtSetTare.getText().toString().trim();
                if (!TextUtils.isEmpty(trim1)) {
                    SerialPortUtil.getInstance().setBulletTare(trim1);
                }
                break;
            case R.id.lock_btn_clear_recv:
                edtLockReceiveMsg.setText("");
                break;
            case R.id.btn_set_cab_no:
                //保存枪柜门地址
                String leftNo = etLeftCabNo.getText().toString();
                String rightNo = etRightCabNo.getText().toString();
                if (!TextUtils.isEmpty(leftNo)) {
                    SharedUtils.saveLeftCabNo(leftNo);
                }
                if (!TextUtils.isEmpty(rightNo)) {
                    SharedUtils.saveRightCabNo(rightNo);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SerialPortUtil.getInstance().close();
    }

    @Override
    public void onDataReceive(byte[] buffer, int size) {
//        Map<Integer, Boolean> status = new HashMap<>();
        final String hexString = TransformUtil.BinaryToHexString(buffer);
        Log.i(TAG, "onDataReceive hexString: " + hexString);
        if (!TextUtils.isEmpty(hexString)) {
            //获取应答
            String[] split = hexString.split(" ");
            Log.i(TAG, "onDataReceive  split: " + split[1]);
            int i1 = Integer.parseInt(split[1], 16);
            switch (type) {
                case 1:
                    if (split.length == 6) {
                        setStatusTxt(i1 + "号枪锁存在");
                    }
                    break;
                case 2:
                    if (split.length == 7) {
                        setStatusTxt(i1 + "号枪锁正常");
                    }
                    break;
                case 3:
                    if (split.length == 7) {
                        setStatusTxt(i1 + "枪锁已打开");
                    }
                    break;
                case 4:
                    if (split.length == 7) {
                        setStatusTxt("锁号成功修改为" + i1);
                    }
                    break;
                case 5:
                    if (split.length == 7) {
                        setStatusTxt("打开枪柜中所有枪锁，已打开" + i1);
                        openStatus.put(i1 + "", true);
                    }
                    break;
                case 6:
//                    if (split.length ==7 && split[1].equals("00")) {
//                        setStatusTxt("枪柜已打开");
//                    }
                    if (split.length == 6 && split[2].equals("00")) {
                        setStatusTxt("枪柜已打开");
                    }
                    break;
                case 7:
                    if (split.length == 7) {
                        setStatusTxt("枪锁设置成功，锁地址：" + i1);
                    }
                    break;
                case 8:
                    if (split.length == 5) {
                        setStatusTxt("枪柜门锁地址成功修改为" + Integer.parseInt(split[2], 16));
                    }
                    break;
                case 9:
                    if (split.length == 5) {
                        setStatusTxt("子弹盒地址" + i1 + "设置子弹重量");
                    }
                    break;
                case 10: //55 01 05 2D 00 01 01 09 75
                    if (split.length == 9) {
                        Log.i(TAG, "onDataReceive 重量: " + split[7]);
                        if (!split[4].equals("00")) {
                            split[4] = split[4].substring(1, 2);
                            Log.i(TAG, "onDataReceive subString: " + split[4]);
                        } else {
                            split[4] = "";
                        }
                        if (!split[5].equals("00")) {
                            split[5] = split[5].substring(1, 2);
                            Log.i(TAG, "onDataReceive subString: " + split[5]);
                        } else {
                            split[5] = "";
                        }
                        if (!split[6].equals("00")) {
                            split[6] = split[6].substring(1, 2);
                            Log.i(TAG, "onDataReceive subString: " + split[6]);
                        } else {
                            split[6] = "";
                        }
                        if (!split[7].equals("00")) {
                            split[7] = split[7].substring(1, 2);
                            Log.i(TAG, "onDataReceive subString: " + split[7]);
                        } else {
                            split[7] = "";
                        }
                        setStatusTxt("子弹盒" + i1 + "读取子弹重量为 " + split[4] + split[5] + split[6] + split[7] + " 克");
                    }
                    break;
                case 11:  //55 01 05 2E 00 00 00 28 57
                    if (split.length == 9) {
                        int i = Integer.parseInt(split[7], 16);
                        Log.i(TAG, "onDataReceive i: " + i);
                        setStatusTxt("子弹盒" + i1 + "读取子弹个数为 " + i + " 个");
                    }
                    break;
                case 12:
                    if (split.length == 5) {
                        setStatusTxt("子弹盒" + i1 + "设置皮重");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setStatusTxt(final String info) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (edtLockReceiveMsg != null && !TextUtils.isEmpty(info)) {
                        edtLockReceiveMsg.append(info + "\n");
                    }
                }
            });
        } catch (Exception e) {
            ToastUtil.showShort(e.getMessage());
        }
    }

}
