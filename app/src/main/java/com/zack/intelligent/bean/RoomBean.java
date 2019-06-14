package com.zack.intelligent.bean;

/**
 * 监控室数据结构
 */

public class RoomBean {

    private String id;          //监控室ID
    private String unitId;      //单位ID
    private String unitName;    //所属单位名称
    private String no;          //监控室编号
    private String name;        //监控室名称
    private String address;     //监控地址
    private String ipAddress;   //IP地址
    private String manageId1;   //值班枪管员id1
    private String manageId2;   //值班枪管员id2
    private String manageId3;   //值班枪管员id3
    private String leadId;      //值班领导ID
    private int lockTime;      //锁定时间
    private String systemPassword; //系统密码
    private long addTime;       //添加时间
    /**
     * true   封柜
     * false  不封柜
     */
    private boolean ifSeal;       //封柜

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getManageId1() {
        return manageId1;
    }

    public void setManageId1(String manageId1) {
        this.manageId1 = manageId1;
    }

    public String getManageId2() {
        return manageId2;
    }

    public void setManageId2(String manageId2) {
        this.manageId2 = manageId2;
    }

    public String getManageId3() {
        return manageId3;
    }

    public void setManageId3(String manageId3) {
        this.manageId3 = manageId3;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public int getLockTime() {
        return lockTime;
    }

    public void setLockTime(int lockTime) {
        this.lockTime = lockTime;
    }

    public String getSystemPassword() {
        return systemPassword;
    }

    public void setSystemPassword(String systemPassword) {
        this.systemPassword = systemPassword;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public boolean isIfSeal() {
        return ifSeal;
    }

    public void setIfSeal(boolean ifSeal) {
        this.ifSeal = ifSeal;
    }

    @Override
    public String
    toString() {
        return "RoomBean{" +
                "id='" + id + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitName='" + unitName + '\'' +
                ", no='" + no + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", manageId1='" + manageId1 + '\'' +
                ", manageId2='" + manageId2 + '\'' +
                ", manageId3='" + manageId3 + '\'' +
                ", leadId='" + leadId + '\'' +
                ", lockTime=" + lockTime +
                ", systemPassword='" + systemPassword + '\'' +
                ", addTime=" + addTime +
                '}';
    }
}
