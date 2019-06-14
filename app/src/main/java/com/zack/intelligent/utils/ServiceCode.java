package com.zack.intelligent.utils;

/**
 * 后台接口
 */

public class ServiceCode {

    /**
     * 根据监控室ID获取枪柜列表{roomid}
     */
    public static final int CODE_GET_CABS_BYROOM = 1002;
    /**
     * 根据枪柜ID获取枪柜信息{cabid}
     */
    public static final int CODE_GET_CAB_BYID = 1001;
    /**
     * 根据监控室ID获取当前值班领导{roomid}"
     */
    public static final int CODE_GET_CURRENT_LEADER = 1004;
    /**
     * 根据监控室ID获取当前值班枪管员{roomid}
     */
    public static final int CODE_GET_CURRENT_MANAGER_BYROOM = 1004;
    /**
     * 根据监控室ID获取领导列表{roomid}
     */
    public static final int CODE_GET_LEADS_BYROOM = 1004;
    /**
     * 根据监控室ID获取普通警员列表{roomid}
     */
    public static final int CODE_GET_POLICE_BYROOM = 1007;
    /**
     * 根据监控室ID查询其下面警员所有的指纹数据条数{roomid}"
     */
    public static final int CODE_GET_BIOCOUNT_BYROOM = 1008;
    /**
     * 根据监控室ID获取单位和警员的树形列表{roomid}"
     */
    public static final int CODE_GET_UNIT_BYROOM = 0;
    /**
     * 根据监控室ID获取监控室{roomid}"
     */
    public static final int CODE_GET_ROOM_BYID = 1012;
    /**
     * 根据监控室ID获取警员出警任务
     */
    public static final int CODE_GET_CURRENT_TASK_ITEM = 0;
    /**
     * 根据警员ID获取警员数据 {policeId}
     */
    public static final int CODE_GET_POLICE_BYID = 1003;
    /**
     * 根据单位ID获取监控室列表 {unitId}
     */
    public static final int CODE_GET_ROOM_BYUNIT = 1014;
    /**
     * 根据任务ID获取任务信息{taskid}
     */
    public static final int CODE_GET_TASK_TASKID = 1023;
    /**
     * 根据监控室ID、日志类型、获取系统日志 {roomId}/{logType}
     */
    public static final int CODE_GET_SYSTEMLOG_BYROOM = 1018;
    /**
     * 根据枪柜ID、日志类型、获取系统日志 {cabId}/{logType}
     */
    public static final int CODE_GET_SYSTEMLOG_BYCAB = 1019;

    /**
     * 根据生物特征ID删除生物特征数据 {bioId}
     */
    public static final int CODE_GET_DELETE_BIO = 1009;

    /**
     * 删除所有生物特征
     */
    public static final int CODE_GET_BIO_DELETE_ALL = 1010;

    /**
     * 获取紧急出警任务 +{policeId}
     * 紧急出警 1
     * 领枪 2
     * 保养 3
     */
    public static final int CODE_GET_TASK = 1022;

    /**
     * 获取系统时间
     */
    public static final int CODE_GET_SYSTEM_TIME = 1032;

    /**
     * 获取系统配置
     */
    public static final int CODE_GET_SYTEM_SETTINGS = 1020;

    /**
     * 获取监控室列表
     */
    public static final int CODE_GET_ROOM_LIST = 1013;

    /**
     * 获取数据字典
     */
    public static final int CODE_GET_DATA_DICTIONARY = 1029;

    /**
     * 获取枪支默认数据
     */
    public static final int CODE_GET_DEFAULT_TYPE = 1033;

    /**
     * 获取默认数据
     */
    public static final int CODE_GET_ALARM_DATA = 1037;

    public static final int CODE_GET_UPLOAD_OPEN = 1038;

    /**********************************POST***********************************************************************/

    /**
     * 提交申请出警任务
     */
    public static final int CODE_POST_TASK = 1028;
    /**
     * 提交紧急出警任务
     */
    public static final int CODE_POST_URGENT = 1028;
    /**
     * 提交领枪记录
     */
    public static final int CODE_POST_GUNOPER_OUT = 1025;
    /**
     * 提交还枪记录
     */
    public static final int CODE_POST_GUNOPER_IN = 1025;
    /**
     * 提交领弹记录
     */
    public static final int CODE_POST_AMMO_OUT = 1025;
    /**
     * 提交还弹记录
     */
    public static final int CODE_POST_AMMO_IN = 1025;
    /**
     * 根据枪柜ID提交心跳包
     */
    public static final int CODE_POST_CAB_HEART = 0;
    /**
     * 设置枪支状态
     */
    public static final int CODE_POST_GUN_HEART = 0;
    /**
     * 上传警员生物特征（指纹）数据
     */
    public static final int CODE_POST_POLICE_BIO = 1011;
    /**
     * 上传警员身份证数据
     */
    public static final int CODE_POST_POLICE_IDCARD = 0;
    /**
     * 设置监控室值班领导
     */
    public static final int CODE_POST_LEAD = 1015;
    /**
     * 设置监控室值班枪管员
     */
    public static final int CODE_POST_MANAGE = 1015;
    /**
     * 添加系统日志
     */
    public static final int CODE_POST_SYSTEM_LOG = 1030;
    /**
     * 紧急出警枪支领出
     */
    public static final int CODE_POST_URGENT_GUN_OUT = 0;
    /**
     * 紧急出警枪支归还
     */
    public static final int CODE_POST_URGENT_GUN_IN = 0;
    /**
     * 紧急出警弹/弹夹领出
     */
    public static final int CODE_POST_URGENT_AMMO_OUT = 0;
    /**
     * 紧急出警弹/弹夹归还
     */
    public static final int CODE_POST_URGENT_AMMO_IN = 0;
    /**
     * 根据警员编号和密码登录
     */
    public static final int CODE_POST_POLICE_LOGIN = 1027;

    /**
     * 根据TaskId提交审批任务
     */
    public static final int CODE_POST_APPROVE_TASK = 1031;

    /**
     * 提交临时存放数据
     */
    public static final int CODE_POST_TEMP_STORE = 1034;

    /**
     * 提交归还临时存放枪支
     */
    public static final int CODE_POST_TEMP_STORE_OPER = 1035;


}
