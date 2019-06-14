package com.zack.intelligent.bean;

import android.support.annotation.NonNull;

/**
 * 枪锁信息
 */
public class SubCabsBean implements Comparable<SubCabsBean>{

    private String id;     //位置ID
    private String roomId; //监控室ID
    private String roomName;//所属监控室
    private String cabId;  //枪柜ID
    private String cabNo;  //所属枪柜
    private String no;     //枪锁号
    private String eno;    //枪锁电子编号
    private int subCabType;//位置类型  1、枪支2、弹药
    private String netAddress; //网卡地址
    private String ipAddress;  //监控地址
    private AmmosBean ammos;  //（子弹/弹夹）存放数据
    private long addTime;   // 添加时间
    private GunsBean guns; //枪支数据结构

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

    public int getSubCabType() {
        return subCabType;
    }

    public void setSubCabType(int subCabType) {
        this.subCabType = subCabType;
    }

    public String getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(String netAddress) {
        this.netAddress = netAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public AmmosBean getAmmos() {
        return ammos;
    }

    public void setAmmos(AmmosBean ammos) {
        this.ammos = ammos;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public GunsBean getGuns() {
        return guns;
    }

    public void setGuns(GunsBean guns) {
        this.guns = guns;
    }

    @Override
    public String toString() {
        return "SubCabsBean{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", cabId='" + cabId + '\'' +
                ", cabNo='" + cabNo + '\'' +
                ", no='" + no + '\'' +
                ", eno='" + eno + '\'' +
                ", subCabType=" + subCabType +
                ", netAddress='" + netAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", ammos=" + ammos +
                ", addTime=" + addTime +
                ", guns=" + guns +
                '}';
    }

    @Override
    public int compareTo(@NonNull SubCabsBean o) {
        int i = 0;
        try {
            i = Integer.parseInt(this.no) -Integer.parseInt(o.no);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

}
