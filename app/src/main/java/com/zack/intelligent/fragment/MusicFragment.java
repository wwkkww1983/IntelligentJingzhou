package com.zack.intelligent.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zack.intelligent.R;
import com.zack.intelligent.hardware.Sensor;
import com.zack.intelligent.ups.Ups;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * debug
 */
public class MusicFragment extends Fragment implements Sensor.OnHumitureValueListener, Ups.OnParamReceive {

    private static final String TAG = "MusicFragment";
    Unbinder unbinder;
    @BindView(R.id.btn_volume_up)
    Button btnVolumeUp;
    @BindView(R.id.btn_volume_down)
    Button btnVolumeDown;
    @BindView(R.id.music_et_switch_code)
    EditText musicEtSwitchCode;
    @BindView(R.id.btn_open_alarm)
    Button btnOpenAlarm;
    @BindView(R.id.btn_open_alcohol)
    Button btnOpenAlcohol;
    @BindView(R.id.btn_open_door_lock)
    Button btnOpenDoorLock;
    @BindView(R.id.btn_read_humiture_value)
    Button btnReadHumitureValue;
    @BindView(R.id.btn_read_alcohol_value)
    Button btnReadAlcoholValue;
    @BindView(R.id.btn_read_ups_status)
    Button btnReadUpsStatus;
    @BindView(R.id.read_ups_battery_percentum)
    Button readUpsBatteryPercentum;
    @BindView(R.id.edt_receive_msg)
    EditText edtReceiveMsg;
    @BindView(R.id.btn_clear_receive)
    Button btnClearReceive;
    private View view;

    private AudioManager audioManager;

    public MusicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_setup, container, false);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        unbinder = ButterKnife.bind(this, view);
        Sensor.getInstance().setOnHumitureValueListener(this);

        Ups.getInstance().setOnParamReceive(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (edtReceiveMsg != null) {
                edtReceiveMsg.append((String) msg.obj + "\n");
            }
        }
    };

    @OnClick({R.id.btn_volume_up, R.id.btn_volume_down, R.id.btn_open_alarm, R.id.btn_open_alcohol,
            R.id.btn_open_door_lock, R.id.btn_read_humiture_value, R.id.btn_read_alcohol_value,
            R.id.btn_read_ups_status, R.id.read_ups_battery_percentum, R.id.btn_clear_receive})
    public void onViewClicked(View view) {
        String code = musicEtSwitchCode.getText().toString();
        switch (view.getId()) {
            case R.id.btn_volume_up://加大音量
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI);
                break;
            case R.id.btn_volume_down://减小音量
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI);
                break;
            case R.id.btn_open_alarm: //打开报警器 1打开 0关闭
                if (!TextUtils.isEmpty(code)) {
                    if (code.equals("1")) {
                        edtReceiveMsg.append("打开报警器\n");
                    } else if (code.equals("0")) {
                        edtReceiveMsg.append("关闭报警器\n");
                    }
                    Sensor.getInstance().alarmSwitch(Integer.parseInt(code));
                }
                break;
            case R.id.btn_open_alcohol://打开酒精传感器 1打开 0关闭
                if (!TextUtils.isEmpty(code)) {
                    if (code.equals("1")) {
                        edtReceiveMsg.append("打开酒精传感器\n");
                    } else if (code.equals("0")) {
                        edtReceiveMsg.append("关闭酒精传感器\n");
                    }
                    Sensor.getInstance().switchEthanol(Integer.parseInt(code));
                }
                break;
            case R.id.btn_open_door_lock://打开门锁 1打开 0关闭
                if (!TextUtils.isEmpty(code)) {
                    if (code.equals("1")) {
                        edtReceiveMsg.append("打开枪柜门锁\n");
                    } else if (code.equals("0")) {
                        edtReceiveMsg.append("关闭枪柜门锁\n");
                    }
                    Sensor.getInstance().switchDoorLock(Integer.parseInt(code));
                }
                break;
            case R.id.btn_read_humiture_value: //读取温湿度值
                Sensor.getInstance().readHumitureValue();
                break;
            case R.id.btn_read_alcohol_value://读取酒精溶度值
                byte[] bytes = Sensor.getInstance().readEthanolValue();
                String hexString = TransformUtil.toHexString(bytes);
                Log.i(TAG, "onViewClicked bytes: " + hexString);
                if (!TextUtils.isEmpty(hexString)) {
                    int i = Integer.parseInt(hexString, 16);
                    Log.i(TAG, "onViewClicked ethanol : " + i);
                    edtReceiveMsg.append(((float) i / 1000) + " V\n");
                }
                break;
            case R.id.btn_read_ups_status://查询电源参数
                Ups.getInstance().init();
                Ups.getInstance().queryParam();
                break;
            case R.id.read_ups_battery_percentum: //查询电量百分比
                Ups.getInstance().queryPercent();
                break;
            case R.id.btn_clear_receive:
                edtReceiveMsg.setText("");
                break;
        }
    }

    @Override
    public void onHumitureValue(String value) {
        if (!TextUtils.isEmpty(value)) {
            final String[] hValue = value.split(" ");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (edtReceiveMsg != null) {
                        edtReceiveMsg.append("温度：" + hValue[2] + "." + hValue[3] + "℃"
                                + "湿度：" + hValue[0] + "." + hValue[1] + "%\n");
                    }
                }
            });
        }
    }

    @Override
    public void onParamData(String data) {
        if (!TextUtils.isEmpty(data)) {
            String[] params = data.split(" ");
            if (params.length == 47) { //接收到参数
                char c1 = (char) Integer.parseInt(params[1], 16);
                char c2 = (char) Integer.parseInt(params[2], 16);
                char c3 = (char) Integer.parseInt(params[3], 16);
                char c4 = (char) Integer.parseInt(params[4], 16);
                char c5 = (char) Integer.parseInt(params[5], 16);
                String inputVoltage = new StringBuilder().append(c1 + c2 + c3 + c4 + c5).toString();
                Log.i(TAG, "onParamData 输入电压: " + inputVoltage + "V");
                char c38 = (char) Integer.parseInt(params[38], 16);
                if (c38 == '1') {//市电异常
                    handler.obtainMessage(1, "市电异常").sendToTarget();
                    Log.i(TAG, "onParamData 市电异常: ");
                } else if (c38 == '0') {//市电正常
                    handler.obtainMessage(1, "市电正常").sendToTarget();
                    Log.i(TAG, "onParamData 市电正常: ");
                }
            } else if (params.length == 5) {
                char units = (char) Integer.parseInt(params[2], 16);
                char tens = (char) Integer.parseInt(params[3], 16);
                Log.i(TAG, "onParamData 电池电量：" + tens + units + "%");
                handler.obtainMessage(1, "电池电量：" + tens + units + "%").sendToTarget();
            }
        }
    }
}
