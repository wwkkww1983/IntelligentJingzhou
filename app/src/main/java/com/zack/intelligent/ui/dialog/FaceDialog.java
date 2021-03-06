package com.zack.intelligent.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.Constants;
import com.zack.intelligent.R;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.face.FaceManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.UserActivity;
import com.zack.intelligent.utils.BitmapUtils;
import com.zack.intelligent.utils.DialogUtils;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.ToastUtil;
import com.zack.intelligent.utils.TransformUtil;
import com.zack.intelligent.utils.Utils;
import com.zack.intelligent.utils.YuvToBgr;
import com.zkteco.android.biometric.ZKLiveFaceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.zack.intelligent.utils.DialogUtils.creatTipDialog;

public class FaceDialog extends Dialog implements SurfaceHolder.Callback {
    private static final String TAG = "FaceDialog";

    @BindView(R.id.dl_face_surface_view)
    SurfaceView dlFaceSurfaceView;
    @BindView(R.id.dl_face_tv_msg)
    TextView dlFaceTvMsg;
    @BindView(R.id.dl_face_iv_close)
    ImageView dlFaceIvClose;
    @BindView(R.id.dl_face_iv_status)
    ImageView dlFaceIvStatus;
    private Context mContext;
    private MembersBean curPolice; //????????????
    private List<PoliceBiosBean> policeBiosBeanList; //???????????????
    private boolean isRegistered; //????????????
    private String bioId;//???????????????id
    private SurfaceHolder holder;
    private long context;//???????????????context
    private Camera mCamera;//??????
    private int width = 640, height = 480; //????????????

    public FaceDialog(@NonNull Context context, MembersBean membersBean, List<PoliceBiosBean> list) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.curPolice = membersBean;
        this.policeBiosBeanList = list;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_face);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        dlFaceSurfaceView.setZOrderOnTop(true);
        holder = dlFaceSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        context = FaceManager.context;
        Log.i(TAG, "onViewClicked context: "+context);
        openCameraAndSetSurfaceviewSize();
        initData();
    }

    public void initData() {
//        Log.i(TAG, "initData: " + membersBean);
        isRegistered = false; //????????????????????????
        dlFaceTvMsg.setText("???????????????????????????");
        setImageByBioType(false);
        if(curPolice !=null){
            List<PoliceBiosBean> policeBios = curPolice.getPoliceBios();
            if (!policeBios.isEmpty()) {
                for (int i = 0; i < policeBios.size(); i++) {
                    PoliceBiosBean policeBiosBean = policeBios.get(i);
                    int deviceType = policeBiosBean.getDeviceType();
                    if (deviceType == Constants.DEVICE_FACE) {
                        bioId = policeBiosBean.getId();
                        setImageByBioType(true);
                    }
                }
            } else { //???????????????????????????????????????
                setImageByBioType(false);
            }
        }
    }

    private void setImageByBioType(boolean isRegister) {
        if (isRegister) {
            Log.i(TAG, "setImageByBioType : ");
            Bitmap bitmap = BitmapUtils.readBitMap(
                    mContext, R.drawable.face_regist_red);
            dlFaceIvStatus.setImageBitmap(bitmap);
            dlFaceTvMsg.setText("?????????");
            isRegistered = true;
        } else {
            Log.i(TAG, "clearImgFinger: ");
            Bitmap bitmap = BitmapUtils.readBitMap(
                    mContext, R.drawable.face_regist_blue);
            dlFaceIvStatus.setImageBitmap(bitmap);
            dlFaceTvMsg.setText("?????????");
            isRegistered = false;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //??????surfaceview
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        option =false;
    }

    @OnClick({R.id.dl_face_iv_status, R.id.dl_face_iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dl_face_iv_status:
                if (isRegistered) { //????????? ??????????????????
                    if (!TextUtils.isEmpty(bioId)) {
                        deleteFace();
                    }
                } else { //????????? ??????????????????
                    dlFaceIvStatus.setVisibility(View.INVISIBLE);
                    dlFaceSurfaceView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.dl_face_iv_close:
                dismiss();
                break;
        }
    }

    /**
     * ???????????? ??????????????????
     *
     * @return
     */
    private Void openCameraAndSetSurfaceviewSize() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(0); //????????????
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(width, height); //???????????????
                //parameters.setRotation(270);
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(0, info);
                mCamera.setParameters(parameters);

            } catch (Exception e) {
                ToastUtil.showShort("??????????????????");
                e.printStackTrace();
            }
        } else {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width, height); //???????????????
            //parameters.setRotation(270);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            mCamera.setParameters(parameters);
        }
        return null;
    }

    private List<Integer> bioIdList = new ArrayList<>();

    private int getFaceId() {
        if (!policeBiosBeanList.isEmpty()) {
            for (int i = 0; i < policeBiosBeanList.size(); i++) {
                PoliceBiosBean policeBiosBean = policeBiosBeanList.get(i);
                int deviceType = policeBiosBean.getDeviceType();
                if (deviceType == Constants.DEVICE_FACE) {
                    int biosId = policeBiosBean.getFingerprintId();
                    Log.i(TAG, "getIrisId bioID: " + biosId);
                    bioIdList.add(biosId);
                }
            }
        }
        if (!bioIdList.isEmpty()) {
            List<Integer> compare = Utils.compare(bioIdList, 1000);
            int min = Collections.min(compare);
            Log.i(TAG, "getIrisId min: " + min);
            return min;
        }
        return 1;
    }

    private Dialog choiceDialog;

    private void deleteFace() {
        choiceDialog = DialogUtils.createChoiceDialog(mContext,
                "???????????????????????????????????????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //????????????
                        Log.i(TAG, "onClick ????????????????????? ");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                postDelete(bioId);
                            }
                        }).start();
                        choiceDialog.dismiss();
                    }
                });
        choiceDialog.show();
    }

    private void postDelete(final String bioId) {
        HttpClient.getInstance().deleteBios(mContext, bioId, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "onSucceed  response : " + response.get());

                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) {
                    showTipDialogAndDismiss("???????????????");
                    List<PoliceBiosBean> policeBios = curPolice.getPoliceBios();
                    if (policeBios != null && !policeBios.isEmpty()) {
                        for (int i = 0; i < policeBios.size(); i++) {
                            PoliceBiosBean policeBiosBean = policeBios.get(i);
                            String id = policeBiosBean.getId();
                            if (id.equals(bioId)) {
                                int fingerprintId = policeBiosBean.getFingerprintId();
                                ZKLiveFaceService.dbDel(FaceManager.context, String.valueOf(fingerprintId));
                            }
                        }
                    }
                    //??????????????????
//                        GreendaoMg.addNormalLog(currentPolice,
//                                8, 2, "???" + currentPolice.getName() + "?????????????????????????????????," +
//                                        "?????????" + membersBean.getName() + "??????????????? ");
                    getPoliceList();
                } else {
                    showTipDialogAndDismiss("???????????????");
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed  error: " + response.getException().getMessage());
            }
        });
    }

    private void getPoliceList() {
        HttpClient.getInstance().getPoliceList(getContext(), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getPoliceList onSucceed response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String msg = dataBean.getMsg();
                    if (success) {
                        String body = dataBean.getBody();
                        List<MembersBean> polices = JSON.parseArray(body, MembersBean.class);
                        UserActivity ua = (UserActivity) mContext;
                        ua.policeList = polices;
                        curPolice = ua.policeList.get(ua.selectedPosition);
                        ua.curPolice = curPolice;
                        LogUtil.i(TAG, "onSucceed curPolice: " + JSON.toJSONString(curPolice));
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "getPoliceList onFailed error: " + response.getException().getMessage());
            }
        });
    }

    private Dialog tipDialog;

    private void showTipDialogAndDismiss(String msg) {
        tipDialog = DialogUtils.creatTipDialog(mContext, "??????", msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                FaceDialog.this.dismiss();
            }
        });
        tipDialog.show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        if (mCamera != null) { //????????????
            SetAndStartPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: " );
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * ???????????????????????????
     *
     * @param holder
     * @return
     */
    private Void SetAndStartPreview(SurfaceHolder holder) {
        Log.i(TAG, "SetAndStartPreview: ");
        try {
            mCamera.setPreviewDisplay(holder);
            //mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewCallback(new Preview()); //
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort("??????????????????");
        }
        return null;
    }

    private boolean option = true;

    private class Preview implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (option) {
                option = false;
                register_fuction(data);
            }
        }
    }

    /**
     * ????????????
     *
     * @param data
     */
    private void register_fuction(final byte[] data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
                byte[] bgrData = YuvToBgr.YV12ToRGB(image.getYuvData(), width, height);

                int[] detectedFaces = new int[1];
                long nTickStart = System.currentTimeMillis();
                //????????????
                Log.i(TAG, "run  context: " + context);
                int ret = ZKLiveFaceService.detectFacesFromNV21(context, data, width, height, detectedFaces);
                Log.i(TAG, "zktest-detectFaces ret=" + ret + ",count=" + detectedFaces[0] + ", timeUsed=" + (System.currentTimeMillis() - nTickStart));
                Log.i(TAG, "run context: " + context);
                if (ret == 0 && detectedFaces[0] > 0) {
                    Log.i(TAG, "run ??????????????????: ");
                    sendHandle(4, null);
                    getFaceContext();//????????????????????????
                } else {
                    Log.i(TAG, "run ??????????????????: " + ret);
                    sendHandle(5, ret);
                }
            }
        }).start();
    }

    //????????????????????????
    private void getFaceContext() {
        long[] faceContext = new long[1];
        int ret = 0;
        //????????????????????????
        Log.i(TAG, "getFaceContext context: " + context);
        ret = ZKLiveFaceService.getFaceContext(context, 0, faceContext);
        Log.i(TAG, "getFaceContext ret: " + ret + " faceContext: " + faceContext[0] + "  context:" + context);
        if (ret == 0) {
            Log.i(TAG, "getFaceContext ????????????????????????: ");
            sendHandle(6, null);
            extractTemplate(faceContext[0]);
        } else {
            Log.i(TAG, "getFaceContext ????????????????????????: ");
            sendHandle(6, ret);
            if (ret == 11) {
                getLastError(context);
            }
        }
    }

    //????????????????????????
    private void getLastError(long context) {
        byte[] lasterror = new byte[256];
        int[] size = new int[1];
        size[0] = 256;
        //?????????????????????????????????
        int ret = ZKLiveFaceService.getLastError(context, lasterror, size);
        if (ret == 0) {
            String errStr = new String(lasterror, 0, size[0]);
            ToastUtil.showShort(errStr);
            sendHandle(-1, errStr);
        }
    }

    /**
     * ??????????????????
     *
     * @param faceContext
     */
    private void extractTemplate(long faceContext) {
        Log.i(TAG, "extractTemplate: " + faceContext);
        int ret = 0;
        byte[] template = new byte[2048];
        int[] size = new int[1];
        int[] resverd = new int[1];
        size[0] = 2048;
        Message msg = new Message();
        long nTickStart = System.currentTimeMillis();
        //??????????????????
        ret = ZKLiveFaceService.extractTemplate(faceContext, template, size, resverd);
        System.out.println("zktest-extractTemplate ret=" + ret + ",size=" + size[0] + ",timeUsed=" + (System.currentTimeMillis() - nTickStart));
        if (ret == 0) {
            Log.i(TAG, "extractTemplate ??????????????????: ");
            sendHandle(8, null);
            int _ret = 0;
            //?????????????????????????????????????????????
            Log.i(TAG, "extractTemplate context: " + context);
            int faceId = getFaceId();
            _ret = ZKLiveFaceService.dbAdd(context, "" + faceId, template);
            System.out.println("zktest-dbAdd ret=" + ret);
            if (0 == _ret) {
                Log.i(TAG, "extractTemplate ??????????????????: ");
                sendHandle(10, "ID:" + faceId);
//                byte[] encode = Base64.encode(template, Base64.DEFAULT); //????????????base64????????????
//                String key = new String(encode, 0, encode.length); //??????????????????String?????????
                String key = TransformUtil.toHexString(template);
                Log.i(TAG, "extractTemplate faceID: " + faceId);
                Log.i(TAG, "extractTemplate key: " + key);
                uploadChar(key, faceId);
            } else {
                Log.i(TAG, "extractTemplate ??????????????????: ");
                sendHandle(11, _ret);
            }
        } else {
            Log.i(TAG, "extractTemplate ??????????????????: ");
            sendHandle(9, ret);
        }
    }

    //?????????
    public void sendHandle(int what, Object obj) {
        Message message = mHandler.obtainMessage(what, obj);
        message.sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (dlFaceTvMsg == null) {
                return;
            }
            switch (msg.what) {
                case -1:
                    dlFaceTvMsg.setText("/??????:" + msg.obj.toString());
                    break;
                case 4:
                    dlFaceTvMsg.setText("??????????????????");
                    break;
                case 5:
                    dlFaceTvMsg.setText("??????????????????????????????:" + msg.obj);
                    option = true;
                    break;
                case 6:
                    dlFaceTvMsg.setText("????????????????????????");
                    break;
                case 8:
                    dlFaceTvMsg.setText("??????????????????");
                    break;
                case 9:
                    dlFaceTvMsg.setText("??????????????????????????????:" + msg.obj);
                    Log.i(TAG, "handleMessage context: " + context);
                    getLastError(context);
                    option = true;
                    break;
                case 10:
                    dlFaceTvMsg.setText("??????????????????" + msg.obj);
                    if (dlFaceSurfaceView.getVisibility() == View.VISIBLE) {
                        dlFaceSurfaceView.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 11:
                    dlFaceTvMsg.setText("??????????????????????????????:" + msg.obj);
                    option = true;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void uploadChar(String key, int faceID) {
        if (curPolice == null) {
            Log.i(TAG, "uploadChar membersBean is null: ");
            return;
        }
        PoliceBiosBean postPoliceBio = new PoliceBiosBean();
        postPoliceBio.setPoliceId(curPolice.getId()); //??????id
        postPoliceBio.setDeviceType(Constants.DEVICE_FACE);
        postPoliceBio.setBioType(1);  //???????????????
        postPoliceBio.setBioCheckType(1);  //?????????????????????
        postPoliceBio.setFingerprintId(faceID);
        postPoliceBio.setKey(key);
        postPoliceBio.setTemplateType("FAM-520");
        final String jsonBody = JSON.toJSONString(postPoliceBio, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "onEnrollStatus jsonBody: " + jsonBody);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //??????????????????
                postPoliceBios(jsonBody);
            }
        }).start();
    }

    private void postPoliceBios(String jsonBody) {
        HttpClient.getInstance().postPoliceBios(getContext(), jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "post bios onSucceed  response: " + response.get());
                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                boolean success = dataBean.isSuccess();
                if (success) { //??????????????????
                    getPoliceList(); //????????????????????????
                    showTipDialogAndDismiss("??????????????????");
                } else {  //??????????????????
                    showTipDialogAndDismiss("??????????????????");
                }
                dlFaceSurfaceView.setVisibility(View.INVISIBLE);
                dlFaceIvStatus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed error: " + response.getException().getMessage());
                showTipDialogAndDismiss("??????????????????");
            }
        });
    }

    private void showFaildDialog(String msg) {
        if (tipDialog == null) {
            tipDialog = creatTipDialog(mContext, "??????", msg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipDialog.dismiss();
                }
            });
            if (!tipDialog.isShowing()) {
                tipDialog.show();
            }
        } else {
            if (!tipDialog.isShowing()) {
                tipDialog.show();
            }
        }
    }
}
