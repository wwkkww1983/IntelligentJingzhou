package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zack.intelligent.R;

/**
 * Created by Administrator on 2017-07-10.
 */

public class AmmosNumDialog extends Dialog {

    private EditText etNumber;
    private TextView txtOperNumber;

    public interface OnDataListener{
        void onData(int number);
    }

    public OnDataListener onDataListener;

    public AmmosNumDialog(@NonNull Context context, OnDataListener onDataListener) {
        super(context,R.style.dialog);
        this.onDataListener = onDataListener;
        initView();
    }

    public AmmosNumDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, R.style.dialog);
        initView();
    }

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.dialog_ammos_num);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        etNumber = (EditText) findViewById(R.id.dl_et_ammos_number);
        txtOperNumber = (TextView) findViewById(R.id.dl_txt_oper_number);
        findViewById(R.id.dl_bt_comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存取弹数量
                String number = etNumber.getText().toString();
                if(!TextUtils.isEmpty(number)){
                    int ammoNum = Integer.parseInt(number);
                    if(onDataListener !=null){
                        onDataListener.onData(ammoNum);
                    }
                }
                dismiss();
            }
        });
        findViewById(R.id.dl_bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
