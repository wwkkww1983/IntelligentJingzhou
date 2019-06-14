package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.zack.intelligent.R;


/**
 * 初始化对话框
 *
 * @author Administrator
 *
 */
public class InitDialog extends Dialog{
    private TextView title;

    public InitDialog(Context context){
        super(context, R.style.dialog);
        setContentView(R.layout.ac_load_dlg);

        title = findViewById(R.id.txt_dialog_title);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setTip(String tip){
        title.setText(tip);
    }

}
