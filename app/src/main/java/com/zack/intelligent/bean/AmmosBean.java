package com.zack.intelligent.bean;

/**
 * 弹药数据集
 */
public class AmmosBean {

    private String id;      //弹药位置ID
    private String roomId;  //所属监控室ID
    private String roomName;//所属监控室
    private String cabId;   //所属枪柜ID
    private String cabNo;   //所属枪柜
    private String subCabId;//所属位置ID
    private String subCabNo;//所属位置(枪锁指令编号)
    private float weight;     //单个重量
    private int objectNumber;//弹药数量

    /**
     * 1、正常在库
     * 2、出警领出
     * 3、保养领出
     * 4、紧急出警领出
     * 99、异常不在位
     */
    private int objectStatus;//弹药状态
    /**
     * 2001、9mm手枪弹
     * 2002、5.54mm手枪弹
     * 2003、5.56mm步枪弹
     * 2004、7.62mm步枪弹
     * 2005、防爆枪弹
     *
     * 3001、64式手枪弹夹
     * 3002、77式手枪弹夹
     * 3003、92式手枪弹夹
     * 3004、38mm防暴枪弹夹
     * 3005、79式微冲弹夹
     */
    private int objectTypeId;//弹药类型
    /**
     * false  子弹
     * true  弹匣
     */
    private boolean box;   //是否弹匣
    private String taskId;   //关联任务ID
    private long addTime;    //添加时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCabId() {
        return cabId;
    }

    public void setCabId(String cabId) {
        this.cabId = cabId;
    }

    public String getCabNo() {
        return cabNo;
    }

    public void setCabNo(String cabNo) {
        this.cabNo = cabNo;
    }

    public String getSubCabId() {
        return subCabId;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(int objectNumber) {
        this.objectNumber = objectNumber;
    }

    public int getObjectStatus() {
        return objectStatus;
    }

    public void setObjectStatus(int objectStatus) {
        this.objectStatus = objectStatus;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }

    @Override
    public String toString() {
        return "AmmosBean{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", cabId='" + cabId + '\'' +
                ", cabNo='" + cabNo + '\'' +
                ", subCabId='" + subCabId + '\'' +
                ", subCabNo='" + subCabNo + '\'' +
                ", weight=" + weight +
                ", objectNumber=" + objectNumber +
                ", objectStatus=" + objectStatus +
                ", objectTypeId=" + objectTypeId +
                ", box=" + box +
                ", taskId='" + taskId + '\'' +
                ", addTime=" + addTime +
                '}';
    }
}
