package com.zack.intelligent;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.event.EventConsts;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.GunTypeBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.event.MessageEvent;
import com.zack.intelligent.bean.PoliceBiosBean;
import com.zack.intelligent.face.FaceManager;
import com.zack.intelligent.finger.FingerManager;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.utils.FileCache;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.TransformUtil;
import com.zkteco.android.biometric.ZKLiveFaceService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据中心
 *
 * @author cloud
 */
public class DataCache {
    private static final String TAG = "DataCache";
    private static DataCache instance;
    // 缓存文件名
    public static final String F_GUN_CAB_INFO_LIST = "F_GUN_CAB_INFO_LIST";       //枪柜信息
    public static final String F_MENBER_INFO_LIST = "F_MENBER_INFO_LIST";   //警员身份信息
    public static final String F_CURRENT_MANAGER = "F_CURRENT_MANAGER"; //值班枪管员
    public static final String F_CURRENT_LEADER = "F_CURRENT_LEADER"; //值班领导
    public static final String F_DATA_DICTIONARY = "F_DATA_DICTIONARY";
    public static final String F_GUN_AMMO_TYPE = "F_GUN_AMMO_TYPE";
    public static final String F_POLICE_BIO_INFO = "F_POLICE_BIO_INFO";

    private FileCache fileCache;
    private Context context;
    public List<MembersBean> membersBeanList;  //所有成员数据集
    public List<GunCabsBean> gunCablist; //所有枪柜数据集
    public List<MembersBean> currentManagers; //值班枪管员
    public MembersBean currentLeader; //值班领导
    public List<PoliceBiosBean> policeBiosBeanList; //所有警员指纹信息
    private TextView msgInfo;
    public Map<String, String> objectType;
    private ExecutorService mExecutorService;
    public static boolean isDownChar = true;
    public static List<GunTypeBean> gunTypeBeanList =new ArrayList<>();

    public void loadData(Context context, TextView msgInfo) {
        this.context = context;
        this.msgInfo = msgInfo;
        fileCache = new FileCache(context);
        getDatas();
    }

    private void getDatas() {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getPoliceByRoom();
            }
        });
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getCabsByRoom();
            }
        });
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getCurrentManage();
            }
        });
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getLeadByRoom();
            }
        });
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                getGunType();
            }
        });
    }

    private DataCache() {
        init();
    }

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public void init() {
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        fileCache = new FileCache(App.getContext());
        membersBeanList = new ArrayList<>();  //所有成员数据集
        gunCablist = new ArrayList<>(); //所有枪柜数据集
        currentManagers = new ArrayList<>(); //值班枪管员
        policeBiosBeanList = new ArrayList<>(); //所有警员指纹信息
        objectType = new HashMap<>();
    }

    /**
     * 值班枪管员数据
     */
    private void getCurrentManage() {
        final long startTime = System.currentTimeMillis();
        HttpClient.getInstance().getCurrentDuty(context, 2, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String body = dataBean.getBody();
                    String msg = dataBean.getMsg();
                    if (success) {
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                fileCache.writeJsonFile(F_CURRENT_MANAGER, body);
                                currentManagers = JSON.parseArray(body, MembersBean.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            long endTime = System.currentTimeMillis();
                            LogUtil.i(TAG, "值班管理员获取数据成功！消耗" + (endTime - startTime) + "毫秒");
                            setTxtMsg("值班管理员获取成功");
                        } else {
                            currentManagers = null;
                            setTxtMsg("无值班管理员数据");
                        }
                    } else {
                        currentManagers = null;
                        setTxtMsg(msg);
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

    /**
     * 获取所有警员数据
     */
    public void getPoliceByRoom() {
        final long startTime = System.currentTimeMillis();
        HttpClient.getInstance().getPoliceList(context, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
//                LogUtil.i(TAG, " getPolice onSucceed response : " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    String body = dataBean.getBody();
                    if (success) {
                        if (!TextUtils.isEmpty(body)) {
                            fileCache.writeJsonFile(F_MENBER_INFO_LIST, body);
                            membersBeanList = JSON.parseArray(body, MembersBean.class);
                            Log.i(TAG, "onSucceed  membersBeanList size: " + membersBeanList.size());
                            LogUtil.i(TAG, "所有警员获取数据成功！ 消耗了" + (System.currentTimeMillis() - startTime) + "毫秒");
                            setTxtMsg("警员获取成功");
                            addPolicesBio(membersBeanList);
                            EventBus.getDefault().post(new MessageEvent(EventConsts.GET_MEMBERS_SUCCESS));
                        } else {
                            membersBeanList.clear();
                            setTxtMsg("无警员数据");
                        }
                    } else {
                        membersBeanList.clear();
                        setTxtMsg("获取警员数据失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.i(TAG, "onFailed  error: " + response.getException().getMessage());
            }
        });
    }

    private void setTxtMsg(String msg) {
        if (msgInfo != null) {
            msgInfo.append(msg + "\n");
        }
    }

    //将所有警员指纹保存到集合
    public void addPolicesBio(List<MembersBean> list) {
        if (list != null && !list.isEmpty()) {
            if (!policeBiosBeanList.isEmpty()) { //清除数据 重置
                policeBiosBeanList.clear();
            }
            for (int i = 0; i < list.size(); i++) {
                MembersBean membersBean = list.get(i);
                if (membersBean != null) {
                    List<PoliceBiosBean> policeBios = membersBean.getPoliceBios();
                    policeBiosBeanList.addAll(policeBios);
                }
            }
            String policeBioList = JSON.toJSONString(policeBiosBeanList);
            fileCache.writeJsonFile(F_POLICE_BIO_INFO, policeBioList);
            LogUtil.i(TAG, "addPolicesBio size: " + policeBiosBeanList.size());
            if (isDownChar) {
                try {
                    downCHar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<PoliceBiosBean> readPoliceBiosFromCacheFile() {
        String json = fileCache.readJsonFile(F_POLICE_BIO_INFO);
//        Log.i(TAG, "addPolicesBio json: " + json);
        if (!TextUtils.isEmpty(json)) {
            List<PoliceBiosBean> policeBiosList = new ArrayList<>();
            try {
                policeBiosList = JSON.parseArray(json, PoliceBiosBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!policeBiosList.isEmpty()) {
                policeBiosBeanList.clear();
                policeBiosBeanList.addAll(policeBiosList);
            }
        }
        return policeBiosBeanList;
    }

    //下发模版
    private void downCHar() {
        LogUtil.i(TAG, "downCHar: ");

        if (!policeBiosBeanList.isEmpty()) {
            LogUtil.i(TAG, "downCHar size: " + policeBiosBeanList.size());
//              updateDlMsgInfo.append("清除所有指纹\n");
            if (Constants.isFingerConnect && Constants.isFingerInit) {
                FingerManager.getInstance().clearAllFinger(); //清除设备中所有指纹
            }

            if (Constants.isFaceInit) {
                long context = FaceManager.context;
                Log.i(TAG, "downCHar context: " + context);
                ZKLiveFaceService.dbClear(FaceManager.context);
            }

            for (int i = 0; i < policeBiosBeanList.size(); i++) {
                PoliceBiosBean policeBiosBean = policeBiosBeanList.get(i);
                int deviceType = policeBiosBean.getDeviceType();
                String key = policeBiosBean.getKey();
                int id = policeBiosBean.getFingerprintId();
                Log.i(TAG, "downCHar id: " + id);
//                byte[] decodeKey = Base64.decode(key, Base64.DEFAULT);
                byte[] decodeKey = TransformUtil.hexStrToBytes(key);
                Log.i(TAG, "downCHar key " + key + " \n decodeKey: " + decodeKey.length);
                switch (deviceType) {
                    case Constants.DEVICE_FINGER: //指纹
                        if (Constants.isFingerConnect && Constants.isFingerInit) {
                            Log.i(TAG, "downCHar fingerID: " + id);
                            FingerManager.getInstance().fpDownChar(id, decodeKey);
                        }
                        break;
                    case Constants.DEVICE_VEIN: //指静脉
                        //保存用户模板到算法库
                        break;
                    case Constants.DEVICE_IRIS://虹膜
                        Log.i(TAG, "downCHar irisID: " + id);
                        break;
                    case Constants.DEVICE_FACE://人脸
                        Log.i(TAG, "downCHar  faceID: " + id);
                        if (Constants.isFaceInit) {
                            int ret2 = ZKLiveFaceService.dbAdd(FaceManager.context, String.valueOf(id), decodeKey);
                            Log.i(TAG, "downCHar face ret: " + ret2);
                        }
                        break;
                    default:
                        break;
                }
            }
            setTxtMsg("生物特征更新成功");
        } else {
            setTxtMsg("无生物特征数据");
        }
    }

    /**
     * 枪柜数据数据集
     */
    public void getCabsByRoom() {
        HttpClient.getInstance().getCabByRoom(context, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                LogUtil.i(TAG, "getCab onSucceed  response: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    if (dataBean != null) {
                        boolean success = dataBean.isSuccess();
                        String body = dataBean.getBody();
                        String msg = dataBean.getMsg();
                        if (success) {
                            if (!TextUtils.isEmpty(body)) {
                                fileCache.writeJsonFile(F_GUN_CAB_INFO_LIST, body);
                                try {
                                    gunCablist = JSON.parseArray(body, GunCabsBean.class);
                                    Log.i(TAG, "onSucceed gunCablist: " + gunCablist.size());
                                    for (int i = 0; i < gunCablist.size(); i++) {
                                        GunCabsBean gunCabsBean = gunCablist.get(i);
                                        String jsonS = JSON.toJSONString(gunCabsBean);
                                        //                                        Log.i(TAG, "onSucceed jsonS: " + jsonS);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                setTxtMsg("枪柜数据获取成功！");
                                EventBus.getDefault().post(new MessageEvent(EventConsts.GET_CABS_SUCCESS));
                            } else {
                                gunCablist = null;
                                setTxtMsg("枪柜数据为空！");
                            }
                        } else {
                            gunCablist = null;
                            setTxtMsg("枪柜数据获取失败！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "getCab onFailed error: " + response.getException().getMessage());
            }
        });
    }

    /**
     * 获取当前值班领导
     */
    private void getLeadByRoom() {
        final long startTime = System.currentTimeMillis();
        HttpClient.getInstance().getCurrentDuty(context, 1, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws com.alibaba.fastjson.JSONException {
//                Log.i(TAG, "onSucceed leader data: " + response.get());
                try {
                    DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
                    boolean success = dataBean.isSuccess();
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            try {
                                fileCache.writeJsonFile(F_CURRENT_LEADER, body);
                                long endTime = System.currentTimeMillis();
                                LogUtil.i(TAG, "值班领导数据获取成功！ 消耗了" + (endTime - startTime) + "毫秒");
                                setTxtMsg("值班领导获取成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            currentLeader = null;
                            setTxtMsg("无值班领导数据");
                        }
                    } else {
                        currentLeader = null;
                        setTxtMsg("值班领导获取失败");
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

    /**********************************************************************************************
     *                               POST
     **********************************************************************************************/

    /**
     * 添加操作日志
     *
     * @param jsonBody 日志主体
     */
    public void postOperLog(String jsonBody) {
        HttpClient.getInstance().postOperLog(context, jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postOperLog onSucceed  response: " + response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "postOperLog onFailed  error: " + response.getException().getMessage());

            }
        });
    }

    /**
     * 添加报警日志
     *
     * @param jsonBody 日志主体
     */
    public void postAlarmLog(String jsonBody) {
        HttpClient.getInstance().postAlarmtLog(context, jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postAlarmLog onSucceed  response: " + response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "postAlarmLog onFailed  error: " + response.getException().getMessage());

            }
        });
    }

    /**
     * 添加领还枪日志
     *
     * @param jsonBody 日志主体
     */
    public void postGetGunLog(String jsonBody) {
        HttpClient.getInstance().postGetGunLog(context, jsonBody, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) throws JSONException {
                Log.i(TAG, "postGetGunLog onSucceed  response: " + response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "postGetGunLog onFailed  error: " + response.getException().getMessage());
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
                    if (success) {
                        String body = dataBean.getBody();
                        if (!TextUtils.isEmpty(body)) {
                            List<GunTypeBean> gunTypeList = JSON.parseArray(body, GunTypeBean.class);
                            Log.i(TAG, "getGunType onSucceed gunType: " + JSON.toJSONString(gunTypeList));
                            DataCache.gunTypeBeanList.addAll(gunTypeList);
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

}
