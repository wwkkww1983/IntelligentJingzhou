package com.zack.intelligent.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zack.intelligent.DataCache;
import com.zack.intelligent.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *  更新所有数据
 */

public class UpdateDialog extends Dialog {

    private static final String TAG = "UpdateDialog";
    @BindView(R.id.update_dl_msg_info)
    TextView updateDlMsgInfo;
    @BindView(R.id.update_dl_confirm)
    Button updateDlConfirm;
    private Context context;
    private Unbinder bind;

    public UpdateDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
        initView();
    }

    public void initView() {
        setContentView(R.layout.update_data);
        bind = ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        //加载所有数据 指纹特征数据写入指纹仪并显示进度
        DataCache.getInstance().loadData(context, updateDlMsgInfo);
    }


    @OnClick(R.id.update_dl_confirm)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.update_dl_confirm:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bind.unbind();
    }
}
