package com.zack.intelligent.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;



/**
 * 报警日志
 */

@Entity
public class AlarmLog {

    /**
     * 震动报警功能
     • 网络断开报警功能
     • 服务器断开报警功能
     • 断电报警功能
     • 超时未关门报警功能
     • 枪锁机械应急开启报警功能
     */
    @Id(autoincrement = true)
    public Long _id;
    private String id;
    private String roomId;    //监控室id
    private String roomName;  //监控室名称
    private String cabId;     //枪柜id
    private String cabNo;     //枪柜编号
    /**
     * 1.柜体异常震动报警 = 1, 震动报警
     * 2.备用方式开枪锁 = 2,  钥匙开枪锁
     * 3.枪支或弹药未按时归还 = 3, 归还枪弹超时
     * 4.柜门超时未锁闭 = 4
     * 5.智能柜断电 = 5
     * 6.备用方式开柜门 = 6 钥匙开门
     * 7.网络断开 = 7
     * 8.温湿度异常 = 8
     * 10.酒精浓度异常 = 9
     * */
    private int logSubType;      //日志子类型
    private String manageId;     //值班管理员id
    private String manageId2;     //值班管理员2id
    private String leadId;      //值班领导id
    private long logTime;        //日志时间
    /**
     * 1、未处理
     * 2、处理解除成功
     * 3、处理失败
     */
    private int logStatus;       //日志状态
    private String disPoliceId;  //解除警员Id
    private String disPoliceName;  //解除警员姓名
    private String relieveTime;  //解除报警时间
    private String logContent;   //日志内容
    private long addTime;        //报警时间
    private boolean isSync;     //是否同步
    private String tag;   //唯一标识

    @Generated(hash = 446008287)
    public AlarmLog() {
    }

    @Generated(hash = 835884606)
    public AlarmLog(Long _id, String id, String roomId, String roomName,
            String cabId, String cabNo, int logSubType, String manageId,
            String manageId2, String leadId, long logTime, int logStatus,
            String disPoliceId, String disPoliceName, String relieveTime,
            String logContent, long addTime, boolean isSync, String tag) {
        this._id = _id;
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.cabId = cabId;
        this.cabNo = cabNo;
        this.logSubType = logSubType;
        this.manageId = manageId;
        this.manageId2 = manageId2;
        this.leadId = leadId;
        this.logTime = logTime;
        this.logStatus = logStatus;
        this.disPoliceId = disPoliceId;
        this.disPoliceName = disPoliceName;
        this.relieveTime = relieveTime;
        this.logContent = logContent;
        this.addTime = addTime;
        this.isSync = isSync;
        this.tag = tag;
    }

    public String getRoomId() {
        return this.roomId;
    }
    public void setRoomId(String RoomId) {
        this.roomId = RoomId;
    }
    public String getRoomName() {
        return this.roomName;
    }
    public void setRoomName(String RoomName) {
        this.roomName = RoomName;
    }
    public String getCabId() {
        return this.cabId;
    }
    public void setCabId(String CabId) {
        this.cabId = CabId;
    }
    public String getCabNo() {
        return this.cabNo;
    }
    public void setCabNo(String CabNo) {
        this.cabNo = CabNo;
    }
    public int getLogSubType() {
        return this.logSubType;
    }
    public void setLogSubType(int LogSubType) {
        this.logSubType = LogSubType;
    }
    public String getManageId() {
        return this.manageId;
    }
    public void setManageId(String ManageId) {
        this.manageId = ManageId;
    }
    public String getLeadId() {
        return this.leadId;
    }
    public void setLeadId(String LeadId) {
        this.leadId = LeadId;
    }
    public long getLogTime() {
        return this.logTime;
    }
    public void setLogTime(long LogTime) {
        this.logTime = LogTime;
    }
    public int getLogStatus() {
        return this.logStatus;
    }
    public void setLogStatus(int LogStatus) {
        this.logStatus = LogStatus;
    }
    public String getDisPoliceId() {
        return this.disPoliceId;
    }
    public void setDisPoliceId(String DisPoliceId) {
        this.disPoliceId = DisPoliceId;
    }
    public String getDisPoliceName() {
        return this.disPoliceName;
    }
    public void setDisPoliceName(String DisPoliceName) {
        this.disPoliceName = DisPoliceName;
    }
    public String getLogContent() {
        return this.logContent;
    }
    public void setLogContent(String LogContent) {
        this.logContent = LogContent;
    }
    public long getAddTime() {
        return this.addTime;
    }
    public void setAddTime(long AddTime) {
        this.addTime = AddTime;
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
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
    public String getId() {
        return this.id;
    }

    public String getManageId2() {
        return manageId2;
    }

    public void setManageId2(String manageId2) {
        this.manageId2 = manageId2;
    }

    public String getRelieveTime() {
        return relieveTime;
    }

    public void setRelieveTime(String relieveTime) {
        this.relieveTime = relieveTime;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
