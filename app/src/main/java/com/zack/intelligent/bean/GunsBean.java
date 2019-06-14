package com.zack.intelligent.bean;


/**
 *  枪支信息
 */
public class GunsBean {

    private String id;       //枪支ID
    private String roomId;   //监控室ID
    private String roomName; //所属监控室
    private String cabId;    //枪柜ID
    private String cabNo;    //所属枪柜号
    private String subCabId; //位置ID
    private String subCabNo; //所属位置编号
    private String no;       //枪号
    private String eno;      //电子编号

    /**
     * 1、正常在库
     * 2、出警领出
     * 3、保养领出
     * 4、紧急出警领出
     * 5、临时存放
     * 99、异常不在位
     *
     * 1 在库 2、不在库3、报废，4、紧急领出5、枪支保养6、临时存放
     */
    private int objectStatus;//枪支状态
    /**
     * 1001 64式手枪
     * 1002 77式手枪
     * 1003 92式手枪
     * 1004 38mm防暴枪
     * 1005 79式微冲
     */
    private int objectTypeId;//枪支类型
    private String policeNo; //警号
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getEno() {
        return eno;
    }

    public void setEno(String eno) {
        this.eno = eno;
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

    public String getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
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

    @Override
    public String toString() {
        return "GunsBean{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", cabId='" + cabId + '\'' +
                ", cabNo='" + cabNo + '\'' +
                ", subCabId='" + subCabId + '\'' +
                ", subCabNo='" + subCabNo + '\'' +
                ", no='" + no + '\'' +
                ", eno='" + eno + '\'' +
                ", objectStatus=" + objectStatus +
                ", objectTypeId=" + objectTypeId +
                ", policeNo=" + policeNo +
                ", taskId=" + taskId +
                ", addTime=" + addTime +
                '}';
    }
}
