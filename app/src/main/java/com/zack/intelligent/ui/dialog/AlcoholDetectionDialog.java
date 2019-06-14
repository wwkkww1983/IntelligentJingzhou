package com.zack.intelligent.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zack.intelligent.R;
import com.zack.intelligent.hardware.Sensor;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.TransformUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */

public class AlcoholDetectionDialog extends Dialog {

    private static final String TAG = "AlcoholDetectionDialog";
    @BindView(R.id.dl_alcohol_txt_msg)
    TextView dlAlcoholTxtMsg;
    @BindView(R.id.dl_alcohol_txt_value)
    TextView dlAlcoholTxtValue;
    @BindView(R.id.dl_alcohol_btn_close)
    Button dlAlcoholBtnClose;
    @BindView(R.id.dl_alcohol_txt_countdown)
    TextView dlAlcoholTxtCountdown;
    private Unbinder bind;
    private Context context;
    private String manageId, policeId;
    private Class<?> toClass;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (dlAlcoholTxtCountdown != null) {
                    dlAlcoholTxtCountdown.setText("剩余时间" + (int) msg.obj);
                }
            } else if (msg.what == 1) {
                if (dlAlcoholTxtValue != null) {
                    dlAlcoholTxtValue.setText("当前酒精溶度：" + (int) msg.obj);
                }
            }
        }
    };

    public AlcoholDetectionDialog(
            @NonNull Context context, String policeId, Class<?> toClass) {
        super(context, R.style.dialog);
        this.context = context;
        this.manageId = manageId;
        this.policeId = policeId;
        this.toClass = toClass;
        initView();
    }

    private int receiveTime = 5;
    private List<Float> valueList = new ArrayList<>();

    private void initView() {
        setContentView(R.layout.dl_alcohol_detection);
        bind = ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        SerialPortUtil.getInstance().onCreate();
        Log.i(TAG, "initView thread id: " + Thread.currentThread().getId());
        new Thread(new SendValueBufferTask()).start();

    }

    private Dialog dialog;

    protected void showDialog(String msg) {
        if (!((Activity) context).isFinishing()) {
            if (dialog != null) {
                Log.i(TAG, "showDialog is not null and is not isShowing: ");
                DialogUtils.setTipText(msg);
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            } else {
                dialog = DialogUtils.creatTipDialog(context, "提示", msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

    private boolean isStop;
    private int sendTime = 15;

    @OnClick(R.id.dl_alcohol_btn_close)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dl_alcohol_btn_close:
                //close this dialog
                dismiss();
                break;
        }
    }


    private class SendValueBufferTask implements Runnable {
        @Override
        public void run() {
            while (!isStop && sendTime > 0) {
                Log.i(TAG, "run readTime: " + sendTime);
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = sendTime;
                handler.sendMessage(message);
                //每次读取不断减少次数
                sendTime--;
                byte[] alcohol = Sensor.getInstance().readEthanolValue();
                String hexString = TransformUtil.toHexString(alcohol);
                Log.i(TAG, "onViewClicked bytes: " + hexString);
                if (!TextUtils.isEmpty(hexString)) {
                    final int value = Integer.parseInt(hexString, 16);
                    Log.i(TAG, "onViewClicked ethanol : " + value);
                    message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = value;
                    handler.sendMessage(message);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (value > 2250) { //2.5V
                                AlcoholDetectionDialog.this.dismiss();
                                showDialog("酒精溶度检测超过阀值，禁止领取枪支和弹药");
                            } else {
                                if (sendTime == 0) {
                                    ToastUtil.showShort("酒精浓度值检测正常");
                                    Intent intent = new Intent(context, toClass);
                                    intent.putExtra("manage_id", manageId);
                                    intent.putExtra("police_id", policeId);
                                    context.startActivity(intent);
                                    AlcoholDetectionDialog.this.dismiss();
                                }
                            }
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.i(TAG, "dismiss: ");
        bind.unbind();
        isStop = true;
        SerialPortUtil.getInstance().close();
    }

}
