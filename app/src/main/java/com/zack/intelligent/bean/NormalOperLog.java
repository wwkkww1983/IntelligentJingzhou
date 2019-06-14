package com.zack.intelligent.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * 操作日志
 */
@Entity
public class NormalOperLog {

    @Id(autoincrement = true)
    private Long _id;          //id
    private String id;         //id
    private String roomId;    //监控室id
    private String roomName;  //监控室名称
    private String cabId;     //枪柜id
    private String cabNo;     //枪柜编号
    private String policeId;  //警员id
    private String policeName; //警员姓名
    private int logType;
    /**
     * 超级管理员   ROLE_ADMINISTRATOR
     * 枪械管理员   ROLE_GUN_MAN
     * 普通警员     ROLE_POLICE_CONSTABLE
     * 领导         ROLE_BRANCH_OFFICE_LEADER
     */
    private int policeType; //警员类型
    /**
     * 1. 领枪
     * 2. 紧急领枪
     * 3:"申请领枪";
     * 4:"枪弹保养";
     * 5:"枪弹报废";
     * 6:"临时存放";
     * 7:"值班管理";
     * 8:"指纹管理";
     */
    private int operTaskType;  //操作任务类型
    /**
     * 1.注册指纹
     * 2.删除指纹
     * 3.设置值班领导
     * 4.管理员交接班
     * 5.管理员上线
     * 6.管理员离班
     * 7.进入
     * 8.退出
     * 9.正常开启柜门
     * 10.正常打开枪锁
     */
    private int logSubType;    //日志子类型
    private String manageId;   //值班管理员id
    private String leadId;     //值班领导id
    private String logContent; //日志内容
    private long addTime;      //创建时间
    private boolean isSync;    //是否同步

    @Generated(hash = 151088924)
    public NormalOperLog() {
    }


    @Generated(hash = 553201979)
    public NormalOperLog(Long _id, String id, String roomId, String roomName,
            String cabId, String cabNo, String policeId, String policeName,
            int logType, int policeType, int operTaskType, int logSubType,
            String manageId, String leadId, String logContent, long addTime,
            boolean isSync) {
        this._id = _id;
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.cabId = cabId;
        this.cabNo = cabNo;
        this.policeId = policeId;
        this.policeName = policeName;
        this.logType = logType;
        this.policeType = policeType;
        this.operTaskType = operTaskType;
        this.logSubType = logSubType;
        this.manageId = manageId;
        this.leadId = leadId;
        this.logContent = logContent;
        this.addTime = addTime;
        this.isSync = isSync;
    }


    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCabId() {
        return this.cabId;
    }

    public void setCabId(String cabId) {
        this.cabId = cabId;
    }

    public String getCabNo() {
        return this.cabNo;
    }

    public void setCabNo(String cabNo) {
        this.cabNo = cabNo;
    }

    public String getPoliceId() {
        return this.policeId;
    }

    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }

    public String getPoliceName() {
        return this.policeName;
    }

    public void setPoliceName(String policeName) {
        this.policeName = policeName;
    }

    public int getLogSubType() {
        return this.logSubType;
    }

    public void setLogSubType(int logSubType) {
        this.logSubType = logSubType;
    }

    public String getManageId() {
        return this.manageId;
    }

    public void setManageId(String manageId) {
        this.manageId = manageId;
    }

    public String getLeadId() {
        return this.leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getLogContent() {
        return this.logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getPoliceType() {
        return this.policeType;
    }

    public void setPoliceType(int policeType) {
        this.policeType = policeType;
    }

    public int getOperTaskType() {
        return this.operTaskType;
    }

    public void setOperTaskType(int operTaskType) {
        this.operTaskType = operTaskType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    public int getLogType() {
        return this.logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

}
