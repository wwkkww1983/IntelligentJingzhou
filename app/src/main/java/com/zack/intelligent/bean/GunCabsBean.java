package com.zack.intelligent.bean;

import java.util.List;

/**
 * 枪柜信息
 */

public class GunCabsBean {

    private String id;       //枪柜ID
    private String roomId;   //监控室ID
    private String roomName; //所属监控室名称
    private String no;       //枪柜编号
    private int cabType;      //枪柜类型 1、枪柜2、弹柜3、枪弹一体柜
    private Object netAddress; //网卡地址
    private String ipAddress; //监控地址
    private int netStatus; // 网络状态 1  网络不通 2  网络连通
    private int eleStatus; //电源状态1、电源正常 2、备用电源 3、备用电源即将耗尽
    private int tempStatus;//温度状态1、正常温度2、警告温度 3、报警温度
    private long addTime;  //添加时间
    private List<SubCabsBean> subCabs; //枪锁数据集

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getCabType() {
        return cabType;
    }

    public void setCabType(int cabType) {
        this.cabType = cabType;
    }

    public Object getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(Object netAddress) {
        this.netAddress = netAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getNetStatus() {
        return netStatus;
    }

    public void setNetStatus(int netStatus) {
        this.netStatus = netStatus;
    }

    public int getEleStatus() {
        return eleStatus;
    }

    public void setEleStatus(int eleStatus) {
        this.eleStatus = eleStatus;
    }

    public int getTempStatus() {
        return tempStatus;
    }

    public void setTempStatus(int tempStatus) {
        this.tempStatus = tempStatus;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public List<SubCabsBean> getSubCabs() {
        return subCabs;
    }

    public void setSubCabs(List<SubCabsBean> subCabs) {
        this.subCabs = subCabs;
    }

    @Override
    public String toString() {
        return "GunCabsBean{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", no='" + no + '\'' +
                ", cabType=" + cabType +
                ", netAddress=" + netAddress +
                ", ipAddress='" + ipAddress + '\'' +
                ", netStatus=" + netStatus +
                ", eleStatus=" + eleStatus +
                ", tempStatus=" + tempStatus +
                ", addTime=" + addTime +
                ", subCabs=" + subCabs +
                '}';
    }
}
