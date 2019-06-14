package com.zack.intelligent.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.google.gson.Gson;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunTypeBean;
import com.zack.intelligent.bean.GunsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.db.GreendaoMg;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.serial.SerialPortUtil;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SoundPlayUtil;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.internal.Util;

/**
 * 临时存放枪支
 */

public class TemporaryDialog extends Dialog {
    private static final String TAG = "TemporaryDialog";
    @BindView(R.id.dl_temp_sp_gun_type)
    Spinner dlTempSpGunType;
    @BindView(R.id.dl_temp_btn_confirm)
    Button dlTempBtnConfirm;
    @BindView(R.id.dl_temp_btn_cancel)
    Button dlTempBtnCancel;
    @BindView(R.id.dl_edt_gun_no)
    EditText dlEdtGunNo;
    @BindView(R.id.dl_edt_gun_eno)
    EditText dlEdtGunEno;
    @BindView(R.id.dl_temp_line_type)
    LinearLayout dlTempLineType;

    private Context context;
    private SubCabsBean subCabsBean;
    private ArrayAdapter<ObjectTypeBean> adapter;
    private Unbinder bind;
    private List<ObjectTypeBean> objectTypeBeanList;
    private ObjectTypeBean selectObjType;
    private List<GunTypeBean> gunTypeList; //枪支弹药类型
    private MembersBean manager1;
    private MembersBean manager2;
    private String gunId;

    public TemporaryDialog(@NonNull Context context, SubCabsBean subCabsBean, MembersBean manager1,
                           MembersBean manager2, OnPostListener onPostListener) {
        super(context, R.style.dialog);
        this.context = context;
        this.subCabsBean = subCabsBean;
        this.onPostListener = onPostListener;
        this.manager1 = manager1;
        this.manager2 = manager2;
        initView();
    }

    public interface OnPostListener {
        void onPostFinished(boolean isSuccess);
    }

    OnPostListener onPostListener;

    private void initView() {
        setContentView(R.layout.dl_temporary);
        bind = ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        getGunType(); //获取枪支类型
        objectTypeBeanList = new ArrayList<>();
        adapter = new ArrayAdapter<ObjectTypeBean>(context,
                R.layout.simple_spinner_dropdown_item1, objectTypeBeanList);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        if (dlTempSpGunType == null) {
            return;
        }
        if (adapter == null) {
            Log.i(TAG, "initView adapter is null: ");
            return;
        }
        dlTempSpGunType.setAdapter(adapter);
        dlTempSpGunType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectObjType = objectTypeBeanList.get(position);
//                String jsonString = JSON.toJSONString(selectObjType);
                Gson gson = new Gson();
                String json = gson.toJson(selectObjType);
                Log.i(TAG, "onItemSelected json: " + json);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //获取枪支弹药类型
    private void getGunType() {
        HttpClient.getInstance().getGunType(context, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, "getGunType onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String body = dataBean.getBody();
                    if (success) {
                        if (!TextUtils.isEmpty(body)) {
                            gunTypeList = JSON.parseArray(body, GunTypeBean.class);
                            if (!gunTypeList.isEmpty()) {
                                for (GunTypeBean gunTypeBean : gunTypeList) {
                                    int typenum = gunTypeBean.getTypenum();
                                    if (typenum == 1) {//枪支类型
                                        String typeName = gunTypeBean.getType(); //名称
                                        int typeNo = gunTypeBean.getTypeno(); //编号
                                        ObjectTypeBean objectTypeBean = new ObjectTypeBean();
                                        objectTypeBean.setTypeId(typeNo);
                                        objectTypeBean.setObjType(typeName);
                                        objectTypeBeanList.add(objectTypeBean);
                                    }
                                }
                            }
                            LogUtil.i(TAG, "getGunType onSucceed gunType: " + JSON.toJSONString(gunTypeList));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getGunType onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private class ObjectTypeBean {
        int typeId;
        String objType;

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getObjType() {
            return objType;
        }

        public void setObjType(String objType) {
            this.objType = objType;
        }

        @Override
        public String toString() {
            return objType;
        }
    }

    @OnClick({R.id.dl_temp_btn_confirm, R.id.dl_temp_btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dl_temp_btn_confirm: //确认存放
                if (subCabsBean == null) {
                    ToastUtil.showShort("枪弹位置对象为空");
                    return;
                }
                try {
                    postStoreGun();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                dismiss();
                break;
            case R.id.dl_temp_btn_cancel:
                dismiss();
                break;
        }
    }

    /**
     * 提交存放枪支数据
     */
    private void postStoreGun() {
        String gunNo = dlEdtGunNo.getText().toString();
        Log.i(TAG, "postStoreGun formatDate: " + gunNo);
        String gunEno = dlEdtGunEno.getText().toString();

        if (TextUtils.isEmpty(gunNo)) {
            SoundPlayUtil.getInstance().play(R.raw.enter_gun_no);
            return;
        }
        GunsBean gunsBean = new GunsBean();
        gunId = Utils.genUUID();
        gunsBean.setId(gunId);
        gunsBean.setRoomId(subCabsBean.getRoomId());
        gunsBean.setRoomName(subCabsBean.getRoomName());
        gunsBean.setCabId(subCabsBean.getCabId());
        gunsBean.setCabNo(subCabsBean.getCabNo());
        gunsBean.setSubCabId(subCabsBean.getId());
        gunsBean.setSubCabNo(subCabsBean.getNo());
        gunsBean.setNo(gunNo);
        if (TextUtils.isEmpty(gunEno)) {
            gunEno = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        }
        gunsBean.setEno(gunEno);
        gunsBean.setObjectStatus(6);
        if (selectObjType == null) {
            ToastUtil.showShort("物件类型为空");
            return;
        }
        gunsBean.setObjectTypeId(selectObjType.getTypeId());
        gunsBean.setAddTime(System.currentTimeMillis());
        String jsonString = JSON.toJSONString(gunsBean);
        Log.i(TAG, "postStoreGun jsonString: " + jsonString);
        postTempStore(jsonString);
    }

    private void postTempStore(String jsonString) {
        HttpClient.getInstance().postTempStore(context, jsonString, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postTempStore onSucceed  response: " + response.get());
                try {
                    if (!TextUtils.isEmpty(response.get())) {
                        DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                        boolean success = dataBean.isSuccess();
                        if (success) {
                            showDialog("提交成功");
                            onPostListener.onPostFinished(true);
                            for (int i = 0; i < 2; i++) {
                                SerialPortUtil.getInstance().openLock("0");
                                Thread.sleep(500);
                            }
                            if (subCabsBean != null) {
                                for (int i = 0; i < 3; i++) {
                                    SerialPortUtil.getInstance().openLock(subCabsBean.getNo());
                                    Thread.sleep(500);
                                }
                                GreendaoMg.addTempStoreGunsLog(
                                        manager1.getId(),
                                        manager2.getId(),
                                        Constants.TASK_TYPE_STORE,
                                        Constants.OPER_TYPE_BACK_GUN,
                                        gunId,
                                        Constants.OBJECT_TYPE_GUN,
                                        selectObjType.getTypeId());
                            }
                        } else {
                            showDialog("提交失败");
                            onPostListener.onPostFinished(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TemporaryDialog.this.dismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error: " + response.getException().getMessage());
                showDialog("提交失败");
                onPostListener.onPostFinished(false);
                TemporaryDialog.this.dismiss();
            }
        });
    }

    Dialog dialog;

    protected void showDialog(String msg) {
        if (!((Activity) context).isFinishing()) {
            if (dialog != null) {
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

    @Override
    public void dismiss() {
        super.dismiss();
        try {
            bind.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
