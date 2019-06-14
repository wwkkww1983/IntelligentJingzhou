package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.intelligent.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/10.
 */

public class EnrollVeinDialog extends Dialog {
    private static final String TAG = "EnrollVeinDialog";
    @BindView(R.id.dl_txt_vein_status)
    TextView dlTxtVeinStatus;
    @BindView(R.id.dl_img_vein_close)
    ImageView dlImgVeinClose;
    private Context context;

    public EnrollVeinDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dl_enroll_vein);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @OnClick(R.id.dl_img_vein_close)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.dl_img_vein_close:
                dismiss();
                break;
        }
    }
}
