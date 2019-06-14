package com.zack.intelligent.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.zack.intelligent.Constants;
import com.zack.intelligent.utils.SharedUtils;

import java.util.HashMap;
import java.util.Map;

import static com.yanzhenjie.nohttp.RequestMethod.POST;

/**
 * 网络请求类
 */

public class HttpClient {

    private static final String TAG = "HttpClient";
    private static HttpClient instance;
    private RequestQueue requestQueue;

    private HttpClient() {
        requestQueue = NoHttp.newRequestQueue();
    }

    public static HttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }

    /**
     * @param context
     * @param request
     * @param what
     * @param callback
     */
    public void addStringRequest(Context context, Request<String> request, int what,
                                 HttpListener<String> callback) {
        requestQueue.add(what, request, new HttpResponseListener<>(context, request, callback, false, false));
    }

    /**
     * 根据枪柜ID获取枪柜信息
     *
     * @param context
     * @param callBack
     */
    public void getCabById(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_CAB_BY_CAB_ID, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("id", SharedUtils.getGunCabId());
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "getCabById json: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 用户名密码登录
     *
     * @param context
     * @param callBack
     */
    public void userLogin(Context context, String username, String password, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_USER_LOGIN, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 用户名密码登录
     *
     * @param context
     * @param callBack
     */
    public void userLogin2(Context context, String username, String password, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
//                "http://192.168.9.206:8080"
                        +Constants.GET_USER_LOGIN_2, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "userLogin2 jsonString: "+jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 上传操作日志
     *
     * @param context
     * @param callBack
     */
    public void postOperLog(Context context, String json, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_OPER_LOG, POST);
        request.setDefineRequestBodyForJson(json);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 上传报警日志
     *
     * @param context
     * @param callBack
     */
    public void postAlarmtLog(Context context, String json, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_ALARM_LOG, POST);
        request.setDefineRequestBodyForJson(json);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 上传领还枪日志
     *
     * @param context
     * @param callBack
     */
    public void postGetGunLog(Context context, String json, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_GUN_LOG, POST);
        request.setDefineRequestBodyForJson(json);
        addStringRequest(context, request, 0, callBack);
    }


    /**
     * 获取警员信息列表
     *
     * @param context
     * @param callBack
     */
    public void getPoliceList(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_POLICE_LIST, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据枪库id获取枪柜列表
     */
    public void getCabByRoom(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_CAB_BY_ROOM_ID, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "getCabByRoom  json: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 上传生物特征
     */
    public void postPoliceBios(Context context, String json, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.UPLOAD_POLICE_BIOS, POST);
        request.setDefineRequestBodyForJson(json);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据警员id获取警员
     *
     * @param policeId 警员id
     */
    public void getPoliceByid(Context context, String policeId, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_POLICE_BY_POLICE_ID, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("id", policeId);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "getPoliceByid jsonString: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据生物特征ID删除生物特征
     *
     * @param biosId     生物特征ID
     * @param deleteType 删除类型 1 删除单个 2删除全部
     */
    public void deleteBios(Context context, String biosId, int deleteType, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.DELETE_POLICE_BIOS, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("bioid", biosId);
        map.put("deleteType", deleteType);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "deleteBios  jsonString: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 设置枪库值班领导和值班管理员
     *
     * @param currentId 交班人员id
     * @param newId     接班人员id
     * @param type      1 设置管理员 2设置领导
     */
    public void setDuty(Context context, String currentId, String newId,
                        int type, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.SET_DUTY_LEADER_MANAGER, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        map.put("currentid", currentId);
        map.put("newid", newId);
        map.put("settype", type);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);

        Log.i(TAG, "setDuty jsonString: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据枪柜ID获取系统日志
     *
     * @param context
     * @param logType  1.报警日志 2.领还枪日志 3.操作日志
     * @param callBack
     */
    public void getLogByCabId(Context context, int logType,
                              HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_LOG_BY_CAB, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("cabId", SharedUtils.getGunCabId());
        map.put("logType", logType);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "getLogByCabId jsonString: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据枪库ID获取系统日志
     */
    public void getLogByRoomid(Context context, int logType, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_LOG_BY_ROOM, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", SharedUtils.getRoomId());
        map.put("logType", logType);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据警员ID获取警员任务
     *
     * @param policeId 警员id
     * @param taskType 1 紧急领枪 2 普通领枪 3 保养任务
     */
    public void getTaskByPoliceId(Context context, String policeId, int taskType, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_TASK_BY_POLICEID, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("policeId", policeId);
        map.put("taskType", taskType);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交领枪
     */
    public void postGetGun(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_GET_GUN, POST);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交紧急领枪
     */
    public void postUrgentGetGun(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_URGENT_TASK, POST);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取系统时间
     */
    public void getSystemTime(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_SYSTEM_TIME, POST);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取当前领取任务
     */
    public void getCurrentGetTask(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_CURRENT_TASK, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        map.put("type", 1);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取当前归还任务
     */
    public void getCurrentBackTask(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_CURRENT_TASK, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        map.put("type", 2);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取当前值班领导或管理员
     *
     * @param cat 1 领导 2 枪管员
     */
    public void getCurrentDuty(Context context, int cat, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_CURRENT_DUTY, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        map.put("category", cat);
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据枪库id获取领导
     *
     * @param context
     * @param callBack
     */
    public void getLeaderByRoom(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_LEADER_BY_ROOM, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 根据IP获取枪库信息
     *
     * @param context
     * @param callBack
     */
    public void getRoomByServerUrl(Context context, String url, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                url + Constants.GET_ROOM_BY_URL, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomIp", url+"/");
        String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "getRoomByServerUrl jsonString: " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取枪支弹药类型
     *
     * @param context
     * @param callBack
     */
    public void getGunType(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_GUN_AMMO_TYPE, POST);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取紧急出警领出的枪弹数据
     *
     * @param context  上下文
     * @param policeId 警员id
     * @param callBack 接口回调
     */
    public void getUrgentBackData(Context context, String policeId, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_URGENT_BACK, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("policeId", policeId);
        String JSONString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        Log.i(TAG, "getUrgentBackData jsonString : " + JSONString);
        request.setDefineRequestBodyForJson(JSONString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取紧急出警领出的枪弹数据
     *
     * @param context    上下文
     * @param jsonString 归还枪弹数据
     * @param callBack   接口回调
     */
    public void postUrgentBack(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_URGENT_BACK, POST);
        Log.i(TAG, "postUrgentBack jsonString : " + jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取枪支保养任务数据
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void getKeepTask(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_KEEP_TASK, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        map.put("type", 3);
        String jsonString = JSON.toJSONString(map);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取枪支报废任务数据
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void getScrapTask(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_KEEP_TASK, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        map.put("type", 4);
        String jsonString = JSON.toJSONString(map);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交枪支报废数据
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void postScrapData(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_SCRAP_DATA, POST);
        Map<String, Object> map = new HashMap<>();
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交枪支保养领出数据
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void postKeepGetData(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_KEEP_GET_DATA, POST);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交枪支保养数据
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void getKeepBackData(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.GET_KEEP_BACK_DATA, POST);
        Map<String, Object> map = new HashMap<>();
        map.put("roomid", SharedUtils.getRoomId());
        String jsonString = JSON.toJSONString(map);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交枪支保养归还数据
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void postKeepBackData(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_KEEP_GET_DATA, POST);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交抓拍图片
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void postCapturePicture(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_CAPTURE_PICTURE, POST);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取抓拍图片
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void getCapturePicture(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        + Constants.GET_CAPTURE_PICTURE, POST);
        Map<String, Object> map =new HashMap<>();
        map.put("roomId",SharedUtils.getRoomId());
        String jsonString = JSON.toJSONString(map);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 获取临时存放枪支位置
     *
     * @param context  上下文
     * @param callBack 接口回调
     */
    public void getTempPosition(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        + Constants.GET_TEMP_GUN_POSITION, POST);
        Map<String, Object> map =new HashMap<>();
        map.put("gunLibId",SharedUtils.getRoomId()); //枪库id
        map.put("robarkId",SharedUtils.getGunCabId());//枪柜id
        String jsonString = JSON.toJSONString(map);

        Log.i(TAG, "getTempPosition  jsonString: "+jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 提交临时存放枪支
     */
    public void postTempStore(Context context, String json, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        +Constants.POST_TEMP_STORE, POST);
        request.setDefineRequestBodyForJson(json);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 领取临时存放枪支数据
     */
    public void getTempGunData(Context context, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        + Constants.GET_TEMP_GUN_DATA, POST);
        Map<String, Object> data =new HashMap<>();
        data.put("robId", SharedUtils.getGunCabId());
        String jsonString = JSON.toJSONString(data);
        Log.i(TAG, "getTempGunData : "+jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 领取临时存放枪支
     */
    public void postGetTemp(Context context, String jsonString, HttpListener<String> callBack) {
        Request<String> request = NoHttp.createStringRequest(
                "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                        + Constants.POST_GET_TEMP, POST);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 领取临时存放枪支
     */
    public void getStoreTask(Context context, HttpListener<String> callBack) {
        Log.i(TAG, "getStoreTask  base url: "+"http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort());
      String URL = "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                + Constants.GET_STORE_TASK;
        Log.i(TAG, "getStoreTask  URL: "+URL);
        Request<String> request = NoHttp.createStringRequest(URL, POST);

        Map<String, Object> data =new HashMap<>();
        data.put("roomId", SharedUtils.getRoomId());
        String jsonString = JSON.toJSONString(data);
        Log.i(TAG, "getStoreTask : "+jsonString);
        request.setDefineRequestBodyForJson(jsonString);
        addStringRequest(context, request, 0, callBack);
    }

    /**
     * 领取临时存放枪支
     */
    public void uploadStoreData(Context context, String json, HttpListener<String> callBack) {
        Log.i(TAG, "uploadStoreData  base url: "+"http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort());
        String URL = "http://"+ SharedUtils.getServerIp() +":"+SharedUtils.getServerPort()
                + Constants.UPLOAD_STORE_DATA;
        Log.i(TAG, "uploadStoreData  URL: "+URL);
        Request<String> request = NoHttp.createStringRequest(URL, POST);

//        Map<String, Object> data =new HashMap<>();
//        data.put("roomId", SharedUtils.getRoomId());
//        String jsonString = JSON.toJSONString(data);
        Log.i(TAG, "uploadStoreData : "+json);
        request.setDefineRequestBodyForJson(json);
        addStringRequest(context, request, 0, callBack);
    }

}
