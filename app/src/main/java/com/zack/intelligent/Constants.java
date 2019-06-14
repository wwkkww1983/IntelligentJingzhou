package com.zack.intelligent;


/**
 * 常量
 */

public class Constants {

    public static String IP ="192.168.3.34";
    public static String PORT ="8080";
    public static String SERVER_NAME = "/PlomentInfoSystem/cal/";

    public static final int GUN_CAB_TYPE =1;

    public static boolean isCapture = false; //是否抓拍
    public static boolean isCapturing = false; //是否正在抓拍

    public static boolean isDebug = false; //调试

    public static final boolean IS_NEW_BOARD =true; //新板子/旧板子
    public static final int DEVICE_FINGER= 1;
    public static final int DEVICE_VEIN= 2;
    public static final int DEVICE_IRIS= 3;
    public static final int DEVICE_FACE= 4;

    public static boolean isFingerInit =false;
    public static boolean isVeinInit =false;
    public static boolean isIrisInit =false;
    public static boolean isFaceInit =false;

    public static boolean isFingerConnect =false;
    public static boolean isVeinConnect =false;

    public static final int  CAB_TYPE_LONG= 1; //长枪柜
    public static final int  CAB_TYPE_AMMO= 2;//弹药柜
    public static final int  CAB_TYPE_MIX= 3;//综合柜
    public static final int  CAB_TYPE_SHORT= 0;//短枪柜


    public static final int  POLICE_TYPE_ADMIN= 0;//系统管理员
    public static final int  POLICE_TYPE_MANAGER= 1;//枪管员
    public static final int  POLICE_TYPE_POLICE= 2;//警员
    public static final int  POLICE_TYPE_LEADER= 3;//领导

    /**
     * 报警类型常量
     *
     * 1.非正常开启柜门 = 1, 震动报警
     * 2.非正常领取枪支或弹药 = 2,  //钥匙开枪锁
     * 4.柜门超时未锁闭 = 3, //枪柜门超时未关
     * 5.智能柜断电 = 4,  //
     * 6.备用方式开柜门 = 5
     * 7.网络断开 = 6
     * 8.温湿度异常 = 7
     * 10.酒精浓度异常 = 8
     * */
    public static final int ALARM_ABNORMAL_OPEN_DOOR= 1;//既不用钥匙 也不用面板控制打开
    public static final int ALARM_ABNORMAL_OPER_GUN_AMMO= 2; //用钥匙打开枪锁领取枪支弹药
    public static final int ALARM_GUN_OVERTIME_BACK= 3; //枪支超时未归还
    public static final int ALARM_CAB_OVERTIME_LOCK= 4;//枪柜门超时未关闭
    public static final int ALARM_POWER_ABNORMAL =5;//市电断开
    public static final int ALARM_BACKUP_OPEN_GUN_LOCK =6;//备用钥匙开启柜门
    public static final int ALARM_NETWORK_DISCONNECT =7;//网络断开连接
    public static final int ALARM_HUMITURE_ABNORMAL =8;//温湿度异常
    public static final int ALARM_ALCOHOLIC_ABNORMAL =9;//酒精溶度异常

    public static final int TASK_TYPE_URGENT =1; //紧急领枪
    public static final int TASK_TYPE_GET =2;   //领枪
    public static final int TASK_TYPE_KEEP =3;  //保养
    public static final int TASK_TYPE_IN =4;    //入库
    public static final int TASK_TYPE_SCRAP =5; //报废
    public static final int TASK_TYPE_STORE =6; //临时存放

    public static final int OPER_TYPE_GET_GUN =1;//领取枪支
    public static final int OPER_TYPE_BACK_GUN =2;//归还枪支
    public static final int OPER_TYPE_GET_AMMO =3;//领取弹药
    public static final int OPER_TYPE_BACK_AMMO =4;//归还弹药

    public static final int OBJECT_TYPE_BULLET =1;//子弹
    public static final int OBJECT_TYPE_CLIP =2;//弹匣
    public static final int OBJECT_TYPE_GUN =3;//枪支

    public static final String ACTIVITY_URGENT = "URGENT";
    public static final String ACTIVITY_GET = "GET";
    public static final String ACTIVITY_BACK = "BACK";
    public static final String ACTIVITY_KEEP = "KEEP";
    public static final String ACTIVITY_SCRAP = "SCRAP";
    public static final String ACTIVITY_TEMP_STORE = "TEMP_STORE";
    public static final String ACTIVITY_EXCHANGE = "EXCHANGE";
    public static final String ACTIVITY_USER = "USER";
    public static final String ACTIVITY_IN_STORE = "INSTORE";
    public static final String ACTIVITY_SETTING = "SETTING";
    //是否第一个验证
    public static boolean isFirstVerify =true;

    //是否震动报警
    public static boolean openVibration = true;

    /**
     *  根据枪柜ID获取枪柜信息
     */
    public static final String GET_CAB_BY_CAB_ID = SERVER_NAME + "getCabList.action";

    /**
     * 根据枪库ID获取枪柜
     */
    public static final String GET_CAB_BY_ROOM_ID = SERVER_NAME + "getcabList.action";

    /**
     * 用户名密码登录
     */
    public static final String GET_USER_LOGIN = SERVER_NAME + "policeLogin.action";


    public static final String GET_USER_LOGIN_2 = SERVER_NAME + "selectLeaderByUserNameAndPassword.action";
    /**
     * 获取警员列表
     */
    public static final String GET_POLICE_LIST = SERVER_NAME + "getListUser.action";
    /**
     * 根据警员id获取警员
     */
    public static final String GET_POLICE_BY_POLICE_ID = SERVER_NAME + "getPlomentInfo.action";

    /**
     *根据生物特征ID删除生物特征数据
     */
    public static final String DELETE_POLICE_BIOS = SERVER_NAME + "deletePoliceBio.action";

    /**
     *上传警员生物特征数据
     */
    public static final String UPLOAD_POLICE_BIOS = SERVER_NAME + "insertPoliceBio.action";

    /**
     *设置枪库值班领导和值班管理员
     */
    public static final String SET_DUTY_LEADER_MANAGER = SERVER_NAME + "setStatusOnDuty.action";

    /**
     *根据枪枪柜ID获取系统日志
     */
    public static final String GET_LOG_BY_CAB = SERVER_NAME + "getrobask.action";

    /**
     *根据枪库ID获取系统日志
     */
    public static final String GET_LOG_BY_ROOM = SERVER_NAME + "getgunLiber.action";

    /**
     *根据警员ID获取警员当前申请的任务信息
     */
    public static final String GET_TASK_BY_POLICEID = SERVER_NAME + "getTaskInfoList.action";

    /**
     *提交领枪
     */
    public static final String POST_GET_GUN = SERVER_NAME + "insertguntask.action";

    /**
     *提交紧急出警任务
     */
    public static final String POST_URGENT_TASK = SERVER_NAME + "alcationtask.action";

    /**
     *获取系统时间
     */
    public static final String GET_SYSTEM_TIME = SERVER_NAME + "getSystemtime.action";

    /**
     *上传报警日志
     */
    public static final String POST_ALARM_LOG = SERVER_NAME + "insertAlarlog.action";

    /**
     *上传领还枪日志
     */
    public static final String POST_GUN_LOG = SERVER_NAME + "insertgunlog.action";

    /**
     *上传操作日志
     */
    public static final String POST_OPER_LOG = SERVER_NAME + "insertNorlog.action";

    /**
     * 获取当前任务
     * mangenid
     */
    public static final String GET_CURRENT_TASK = SERVER_NAME + "gettacksinfo.action";

    /**
     * 获取当前值班领导和管理员
     */
    public static final String GET_CURRENT_DUTY = SERVER_NAME + "getPoliceCategory.action";

    /**
     * 根据枪库id获取领导
     */
    public static final String GET_LEADER_BY_ROOM = SERVER_NAME + "getleads.action";

    /**
     * 根据服务器地址获取所属枪库
     */
    public static final String GET_ROOM_BY_URL = SERVER_NAME +  "getRoomip.action";

    /**
     * 获取枪支弹药类型
     */
    public static final String GET_GUN_AMMO_TYPE = SERVER_NAME +"getguntype.action";

    /**
     *  提交归还紧急领取枪弹
     */
    public static final String POST_URGENT_BACK = SERVER_NAME +"inservicetask.action";

    /**
     * 获取紧急领出的枪支数据
     */
    public static final String GET_URGENT_BACK = SERVER_NAME +"taskgetinfo.action";

    /**
     * 获取枪支保养任务
     */
    public static final String GET_KEEP_TASK = SERVER_NAME +"getGunmaintenance.action";

    /**
     * 提交报废任务数据
     */
    public static final String POST_SCRAP_DATA = SERVER_NAME +"insergundiscartded.action";

    /**
     * 提交枪支保养领出数据
     */
    public static final String POST_KEEP_GET_DATA = SERVER_NAME +"insertGunmaintenance.action";

    /**
     * 获取保养归还数据
     */
    public static final String GET_KEEP_BACK_DATA = SERVER_NAME +"gettogunmaintenace.action";

    /**
     * 上传抓拍图片
     */
    public static final String POST_CAPTURE_PICTURE = SERVER_NAME +"insertPictureImg.action";

    /**
     * 获取抓拍图片
     */
    public static final String GET_CAPTURE_PICTURE = SERVER_NAME +"getPictureImg.action";

    /**
     *提交临时存放数据
     */
    public static final String POST_TEMP_STORE = SERVER_NAME + "insertStorag.action";

    /**
     * 提交临时存放枪支领取
     *
     * */
    public static final String POST_GET_TEMP = SERVER_NAME + "insertgunstem.action";

    /**
     * 获取临时存放枪支位置
     */
    public static final String GET_TEMP_GUN_POSITION = SERVER_NAME +"getAdress2.action";

    /**
     * 获取临时存放枪支数据
     */
    public static final String GET_TEMP_GUN_DATA = SERVER_NAME +"getGuntemProrary.action";

    /**
     * 获取枪弹入库任务
     */
    public static final String GET_STORE_TASK = SERVER_NAME +"getStorageTask.action";

    /**
     * 上传枪支弹入库数据
     */
    public static final String UPLOAD_STORE_DATA = SERVER_NAME +"insertgunbultNumber.action";


}
