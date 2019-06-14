package com.zack.intelligent.bean;

/**
 * 领取/归还枪支弹药
 */
public class OperBean {

    private String id; //null
    private String taskId;      //任务id
    private String taskItemId;	//	任务清单id
    private String manageId;	//	枪管员id
    private String cabId;	    //	所在枪柜id
    private String subCabId;	//  枪锁id
    private String subCabNo;    //  枪锁位置
    private String objectId;	//	枪支id/弹药id
    private String gunNo;	//  枪支编号
    private String gunEno;  //  电子编号
    private int objectTypeId;// 枪支类型
    private int operType;	//	操作类型：1=领取；2=归还
    private int operNumber;	//	数量
    private Long addTime;	//  null
    private Long updateTime; // null
    private int enabled;	//  null
    private int positionType; // 1.子弹 2.弹夹 3.枪支

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskItemId() {
        return this.taskItemId;
    }

    public void setTaskItemId(String taskItemId) {
        this.taskItemId = taskItemId;
    }

    public String getManageId() {
        return this.manageId;
    }

    public void setManageId(String manageId) {
        this.manageId = manageId;
    }

    public String getCabId() {
        return this.cabId;
    }

    public void setCabId(String cabId) {
        this.cabId = cabId;
    }

    public String getSubCabId() {
        return this.subCabId;
    }

    public void setSubCabId(String subCabId) {
        this.subCabId = subCabId;
    }

    public String getSubCabNo() {
        return subCabNo;
    }

    public void setSubCabNo(String subCabNo) {
        this.subCabNo = subCabNo;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getGunNo() {
        return this.gunNo;
    }

    public void setGunNo(String gunNo) {
        this.gunNo = gunNo;
    }

    public String getGunEno() {
        return this.gunEno;
    }

    public void setGunEno(String gunEno) {
        this.gunEno = gunEno;
    }

    public int getObjectTypeId() {
        return this.objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getOperType() {
        return this.operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
    }

    public int getOperNumber() {
        return this.operNumber;
    }

    public void setOperNumber(int operNumber) {
        this.operNumber = operNumber;
    }

    public Long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public int getEnabled() {
        return this.enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getPositionType() {
        return this.positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }


}