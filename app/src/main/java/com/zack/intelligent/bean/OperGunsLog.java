package com.zack.intelligent.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 领还枪日志
 */
@Entity
public class OperGunsLog {

    @Id(autoincrement = true)
    private Long _id;
    private String id;
    private String taskId;  //任务iD
    private String roomId;  //监控室ID
    private String roomName;  //监控室名称
    private String cabId;   //枪柜Id
    private String cabNo;   //枪柜编号
    private String manageId;   //值班管理员
    private String manageId2;   //值班管理员2
    private String leadId;      //值班领导
    private String policeId; //警员ID
    private String policeName; //警员姓名
    private String objectId; //物件id
    private int objectTypeId; //物件类型
    private int objectNum;    //领取/归还数量
    private int taskType;   //任务类型  1.紧急出警2.出警3.保养4.入库5.报废6.临时存放
    private int operType;  //操作类型  1.领取枪支 2.归还枪支 3.领取弹药 4.归还弹药
    private long addTime;   //生成时间
    private boolean isSync; //是否同步
    private int type; //1.子弹 2.弹夹 3.枪支

    @Generated(hash = 1291300200)
    public OperGunsLog() {
    }


    @Generated(hash = 955718370)
    public OperGunsLog(Long _id, String id, String taskId, String roomId,
            String roomName, String cabId, String cabNo, String manageId,
            String manageId2, String leadId, String policeId, String policeName,
            String objectId, int objectTypeId, int objectNum, int taskType,
            int operType, long addTime, boolean isSync, int type) {
        this._id = _id;
        this.id = id;
        this.taskId = taskId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.cabId = cabId;
        this.cabNo = cabNo;
        this.manageId = manageId;
        this.manageId2 = manageId2;
        this.leadId = leadId;
        this.policeId = policeId;
        this.policeName = policeName;
        this.objectId = objectId;
        this.objectTypeId = objectTypeId;
        this.objectNum = objectNum;
        this.taskType = taskType;
        this.operType = operType;
        this.addTime = addTime;
        this.isSync = isSync;
        this.type = type;
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

    public String getManageId() {
        return manageId;
    }

    public void setManageId(String manageId) {
        this.manageId = manageId;
    }

    public String getManageId2() {
        return manageId2;
    }

    public void setManageId2(String manageId2) {
        this.manageId2 = manageId2;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public String getPoliceId() {
        return this.policeId;
    }

    public void setPoliceId(String PoliceId) {
        this.policeId = PoliceId;
    }

    public String getPoliceName() {
        return this.policeName;
    }

    public void setPoliceName(String PoliceName) {
        this.policeName = PoliceName;
    }

    public int getObjectTypeId() {
        return this.objectTypeId;
    }

    public void setObjectTypeId(int ObjectTypeId) {
        this.objectTypeId = ObjectTypeId;
    }

    public int getObjectNum() {
        return this.objectNum;
    }

    public void setObjectNum(int ObjectNum) {
        this.objectNum = ObjectNum;
    }

    public int getTaskType() {
        return this.taskType;
    }

    public void setTaskType(int TaskType) {
        this.taskType = TaskType;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(long AddTime) {
        this.addTime = AddTime;
    }

    public int getOperType() {
        return this.operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
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

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getObjectId() {
        return this.objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}

