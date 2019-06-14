package com.zack.intelligent.db;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.zack.intelligent.App;
import com.zack.intelligent.Constants;
import com.zack.intelligent.DataCache;
import com.zack.intelligent.bean.AlarmLog;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.NormalOperLog;
import com.zack.intelligent.bean.OperGunsLog;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.db.gen.AlarmLogDao;
import com.zack.intelligent.db.gen.NormalOperLogDao;
import com.zack.intelligent.db.gen.OperGunsLogDao;
import com.zack.intelligent.service.CaptureService;
import com.zack.intelligent.ui.MainActivity;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.Utils;

/**
 * Created by Administrator on 2018/4/21.
 */
public class GreendaoMg {

    public GreendaoMg() {
    }

    /**
     * 添加领还枪日志
     *
     * @param taskType 1.紧急出警2.出警3.保养4.入库5.报废6.临时存放
     * @param operType 1.领取枪支 2.归还枪支 3.领取弹药 4.归还弹药
     */
    public static void addOperGunsLog(TaskItemsBean taskItemsBean,
                                      String manageId, String manageId2,
                                      int taskType, int operType, String objectId, int type) {
        Log.i("GreenDaoMG", "addOperGunsLog: ");
        try {
            OperGunsLog operGunsLog = new OperGunsLog();
            operGunsLog.setTaskId(taskItemsBean.getTaskId());
            operGunsLog.setRoomId(SharedUtils.getRoomId());
            operGunsLog.setRoomName(SharedUtils.getRoomName());
            operGunsLog.setCabId(SharedUtils.getGunCabId());
            operGunsLog.setCabNo(SharedUtils.getGunCabNo());
            operGunsLog.setManageId(manageId);
            operGunsLog.setManageId2(manageId2);
            operGunsLog.setLeadId(SharedUtils.getDutyLeaderId());
            operGunsLog.setPoliceId(taskItemsBean.getTaskPoliceId());
            operGunsLog.setPoliceName(taskItemsBean.getTaskPoliceName());
            operGunsLog.setTaskType(taskType);
            operGunsLog.setObjectTypeId(taskItemsBean.getObjectTypeId());
            operGunsLog.setObjectNum(taskItemsBean.getObjectNumber());
            operGunsLog.setOperType(operType);
            operGunsLog.setAddTime(System.currentTimeMillis());
            operGunsLog.setType(type);
            operGunsLog.setObjectId(objectId);
            String jsonBody = JSON.toJSONString(operGunsLog);
            Log.i("addOperGunsLog", " jsonBody: " + jsonBody);
            //插入操作数据
            OperGunsLogDao operGunsLogDao = DBManager.getInstance().getOperGunsLogDao();
            long insert = operGunsLogDao.insert(operGunsLog);
            String jsonString = JSON.toJSONString(operGunsLog);
            DataCache.getInstance().postGetGunLog(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCaptureService();
    }

    /**
     * 添加领还枪日志
     *
     * @param taskType 1.紧急出警2.出警3.保养4.入库5.报废6.临时存放
     * @param operType 1.领取枪支 2.归还枪支 3.领取弹药 4.归还弹药
     */
    public static void addBackGunsLog(TaskItemsBean taskItemsBean,
                                      int objectTypeId, int objectNumber,
                                      String manageId, String leaderId,
                                      int taskType, int operType, String objectId, int type) {
        Log.i("GreenDaoMG", "addBackGunsLog: ");
        try {
            OperGunsLog operGunsLog = new OperGunsLog();
            operGunsLog.setTaskId(taskItemsBean.getTaskId());
            operGunsLog.setRoomId(SharedUtils.getRoomId());
            operGunsLog.setRoomName(SharedUtils.getRoomName());
            operGunsLog.setCabId(SharedUtils.getGunCabId());
            operGunsLog.setCabNo(SharedUtils.getGunCabNo());
            operGunsLog.setManageId(manageId);
//            operGunsLog.setManageId2(manageId2);
            operGunsLog.setLeadId(leaderId);
            operGunsLog.setPoliceId(taskItemsBean.getTaskPoliceId());
            operGunsLog.setPoliceName(taskItemsBean.getTaskPoliceName());
            operGunsLog.setTaskType(taskType);
            operGunsLog.setObjectTypeId(objectTypeId);
            operGunsLog.setObjectNum(objectNumber);
            operGunsLog.setOperType(operType);
            operGunsLog.setAddTime(System.currentTimeMillis());
            operGunsLog.setType(type);
            operGunsLog.setObjectId(objectId);
            String jsonBody = JSON.toJSONString(operGunsLog);
            Log.i("addOperGunsLog", " jsonBody: " + jsonBody);
            //插入操作数据
            OperGunsLogDao operGunsLogDao = DBManager.getInstance().getOperGunsLogDao();
            long insert = operGunsLogDao.insert(operGunsLog);
            String jsonString = JSON.toJSONString(operGunsLog);
            DataCache.getInstance().postGetGunLog(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCaptureService();
    }

    /**
     * 添加报警日志
     *
     * @param logSubType
     * @param logContent
     */
    public static void addAlarmLog(int logSubType, String logContent, String tag) {
        AlarmLog alarmLog = new AlarmLog();
        alarmLog.setRoomId(SharedUtils.getRoomId());    //监控室id
        alarmLog.setRoomName(SharedUtils.getRoomName());  //监控室名称
        alarmLog.setCabId(SharedUtils.getGunCabId());    //枪柜id
        alarmLog.setCabNo(SharedUtils.getGunCabNo());  //枪柜编号
        alarmLog.setLeadId(SharedUtils.getDutyLeaderId());  //值班领导id
        alarmLog.setManageId(SharedUtils.getDutyManagerId());  //值班管理员id
        alarmLog.setManageId2(SharedUtils.getDutyManagerId2());
        alarmLog.setLogSubType(logSubType);      //日志子类型
//        alarmLog.setDisPoliceId(disPoliceId); //解除报警人员id
//        alarmLog.setDisPoliceName(disPoliceName);//解除报警人员姓名
        alarmLog.setLogTime(System.currentTimeMillis());
        alarmLog.setLogStatus(1);
        alarmLog.setLogContent(logContent);
        alarmLog.setAddTime(System.currentTimeMillis());
        alarmLog.setTag(tag);

        //插入报警日志
        try {
            AlarmLogDao alarmLogDao = DBManager.getInstance().getAlarmLogDao();
            long insert = alarmLogDao.insert(alarmLog);
            String jsonString = JSON.toJSONString(alarmLog);
            Log.i("addAlarmLog", "addAlarmLog jsonString: "+jsonString);
            DataCache.getInstance().postAlarmLog(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCaptureService();
    }


    public static boolean updateAlarmLog(String relievePoliceId, String relievePoliceName, String tag){
        try {
            AlarmLogDao alarmLogDao = DBManager.getInstance().getAlarmLogDao();
            AlarmLog unique = alarmLogDao.queryBuilder().where(AlarmLogDao.Properties.Tag.eq(tag)).unique();
            unique.setDisPoliceId(relievePoliceId);
            unique.setDisPoliceName(relievePoliceName);
            alarmLogDao.update(unique);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加操作日志
     *
     * @param membersBean 登陆的警员
     * @param taskType    任务类型
     * @param logSubType  操作类型
     * @param logContent  日志内容
     */
    public static void addNormalOperateLog(MembersBean membersBean, int taskType,
                                           int logSubType, String logContent) {
        NormalOperLog operLog = new NormalOperLog();
        operLog.setPoliceId(membersBean.getId());
        operLog.setPoliceName(membersBean.getName());
        operLog.setPoliceType(membersBean.getPoliceType());
        operLog.setRoomId(SharedUtils.getRoomId());
        operLog.setRoomName(SharedUtils.getRoomName());
        operLog.setCabId(SharedUtils.getGunCabId());
        operLog.setCabNo(SharedUtils.getGunCabNo());
        operLog.setLogSubType(logSubType);
        operLog.setOperTaskType(taskType);
        operLog.setManageId(SharedUtils.getDutyManagerId());
        operLog.setLeadId(SharedUtils.getDutyLeaderId());
        operLog.setLogContent(logContent);
        operLog.setAddTime(System.currentTimeMillis());

        //插入操作数据
        try {
            NormalOperLogDao normalOperLogDao = DBManager.getInstance().getNormalOperLogDao();
            long normalOperLogId = normalOperLogDao.insert(operLog);
            String jsonString = JSON.toJSONString(operLog);
            Log.i("addNormalOperateLog", "addNormalOperateLog  jsonString: "+jsonString);
            DataCache.getInstance().postOperLog(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCaptureService();
    }

    /**
     * 添加领还枪日志
     *
     * @param taskType 1.紧急出警2.出警3.保养4.入库5.报废6.临时存放
     * @param operType 1.领取枪支 2.归还枪支 3.领取弹药 4.归还弹药
     */
    public static void addTempStoreGunsLog(String manageId, String manageId2,
                                           int taskType, int operType, String objectId, int type,
                                           int objTypeId) {
        Log.i("GreenDaoMG", "addOperGunsLog: ");
        try {
            OperGunsLog operGunsLog = new OperGunsLog();
            operGunsLog.setTaskId(Utils.genUUID());
            operGunsLog.setRoomId(SharedUtils.getRoomId());
            operGunsLog.setRoomName(SharedUtils.getRoomName());
            operGunsLog.setCabId(SharedUtils.getGunCabId());
            operGunsLog.setCabNo(SharedUtils.getGunCabNo());
            operGunsLog.setManageId(manageId);
            operGunsLog.setManageId2(manageId2);
            operGunsLog.setLeadId(SharedUtils.getDutyLeaderId());
            operGunsLog.setPoliceId("");
            operGunsLog.setPoliceName("");
            operGunsLog.setTaskType(taskType);
            operGunsLog.setObjectTypeId(objTypeId);
            operGunsLog.setObjectNum(1);
            operGunsLog.setOperType(operType);
            operGunsLog.setAddTime(System.currentTimeMillis());
            operGunsLog.setType(type);
            operGunsLog.setObjectId(objectId);
            String jsonBody = JSON.toJSONString(operGunsLog);
            Log.i("addOperGunsLog", " jsonBody: " + jsonBody);
            //插入操作数据
            OperGunsLogDao operGunsLogDao = DBManager.getInstance().getOperGunsLogDao();
            long insert = operGunsLogDao.insert(operGunsLog);
            String jsonString = JSON.toJSONString(operGunsLog);
            DataCache.getInstance().postGetGunLog(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startCaptureService();
    }


    /**
     * 添加操作日志
     *
     * @param membersBean 登陆的警员
     * @param taskType    任务类型
     * @param logSubType  操作类型
     * @param logContent  日志内容
     */
    public static void addNormalLog(MembersBean membersBean, int taskType,
                                    int logSubType, String logContent) {
        //插入操作数据
        try {
            NormalOperLog operLog = new NormalOperLog();
            operLog.setPoliceId(membersBean.getId());
            operLog.setPoliceName(membersBean.getName());
            operLog.setPoliceType(membersBean.getPoliceType());
            operLog.setRoomId(SharedUtils.getRoomId());
            operLog.setRoomName(SharedUtils.getRoomName());
            operLog.setCabId(SharedUtils.getGunCabId());
            operLog.setCabNo(SharedUtils.getGunCabNo());
            operLog.setLogSubType(logSubType);
            operLog.setOperTaskType(taskType);
            operLog.setManageId(SharedUtils.getDutyManagerId());
            operLog.setLeadId(SharedUtils.getDutyLeaderId());
            operLog.setLogContent(logContent);
            operLog.setAddTime(System.currentTimeMillis());

            NormalOperLogDao normalOperLogDao = DBManager.getInstance().getNormalOperLogDao();
            long normalOperLogId = normalOperLogDao.insert(operLog);
            String jsonString = JSON.toJSONString(operLog);
            Log.i("addNormalLog", "addNormalLog  jsonString: " + jsonString);
            DataCache.getInstance().postOperLog(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startCaptureService();
    }


    /**
     * 启动抓拍服务
     */
    private static void startCaptureService() {
        //抓拍开启和非正在抓拍
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Constants.isCapturing) {
                    App.getContext().startService(new Intent(App.getContext(), CaptureService.class));
                }
            }
        }).start();
    }

}
