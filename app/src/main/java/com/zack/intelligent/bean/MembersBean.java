package com.zack.intelligent.bean;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 警员数据结构
 */
public class MembersBean  implements Comparable<MembersBean>{

    private String id;          //警员ID
    private String unitId;      //所属单位ID
    private String unitName;    //所属单位
    private String departmentId;//所属部门
    private String salt;
    private String no;          //警号
    private String name;        //姓名
    private String photo;       //用户照片
    private String photoBase64; //用户照片Base64数据流

    /**
     * 0、无警衔
     * 1、一级警员
     * 2、二级警员
     * 3、一级警司
     * 4、二级警司
     * 5、三级警司
     * 6、一级警督
     * 7、二级警督
     * 8、三级警督
     * 9、一级警监
     * 10、二级警监
     * 11、三级警监
     */
    private String rank;       //警衔
    private String phone;      //手机号码
    /**
     * 1、中共党员
     * 2、共青团员
     * 3、群众
     */
    private String departy;   //政治面貌
    /**
     * 0、超级管理员
     * 1、枪管员
     * 2、警员
     * 3、领导
     */
    private int policeType;  //警员身份
    private String taskId;      //任务ID
    private String licence;     //许可
    private Long addTime;       //添加时间
    private String sex;         //性别
    private String nation;      //民族
    private String born;        //出生日期
    private String address;     //联系地址
    private String idCardNo;    //身份证号码
    private String grantDept;
    private String userLifeBegin;
    private String userLifeEnd;
    private String reserved;
    private String photoFileName;
    private String photoFileNameImage;
    private boolean isExistIdCard;   //是否持有ID卡
    private List<PoliceBiosBean> policeBios;   //警员录入指纹数据集

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUnitId() {
        return this.unitId;
    }
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
    public String getUnitName() {
        return this.unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getDepartmentId() {
        return this.departmentId;
    }
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
    public String getSalt() {
        return this.salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    public String getNo() {
        return this.no;
    }
    public void setNo(String no) {
        this.no = no;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhotoBase64() {
        return this.photoBase64;
    }
    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }
    public String getRank() {
        return this.rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getDeparty() {
        return this.departy;
    }
    public void setDeparty(String departy) {
        this.departy = departy;
    }
    public int getPoliceType() {
        return this.policeType;
    }
    public void setPoliceType(int policeType) {
        this.policeType = policeType;
    }
    public String getTaskId() {
        return this.taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getLicence() {
        return this.licence;
    }
    public void setLicence(String licence) {
        this.licence = licence;
    }
    public Long getAddTime() {
        return this.addTime;
    }
    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getNation() {
        return this.nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getBorn() {
        return this.born;
    }
    public void setBorn(String born) {
        this.born = born;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getIdCardNo() {
        return this.idCardNo;
    }
    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }
    public String getGrantDept() {
        return this.grantDept;
    }
    public void setGrantDept(String grantDept) {
        this.grantDept = grantDept;
    }
    public String getUserLifeBegin() {
        return this.userLifeBegin;
    }
    public void setUserLifeBegin(String userLifeBegin) {
        this.userLifeBegin = userLifeBegin;
    }
    public String getUserLifeEnd() {
        return this.userLifeEnd;
    }
    public void setUserLifeEnd(String userLifeEnd) {
        this.userLifeEnd = userLifeEnd;
    }
    public String getReserved() {
        return this.reserved;
    }
    public void setReserved(String reserved) {
        this.reserved = reserved;
    }
    public String getPhotoFileName() {
        return this.photoFileName;
    }
    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }
    public String getPhotoFileNameImage() {
        return this.photoFileNameImage;
    }
    public void setPhotoFileNameImage(String photoFileNameImage) {
        this.photoFileNameImage = photoFileNameImage;
    }
    public boolean getIsExistIdCard() {
        return this.isExistIdCard;
    }
    public void setIsExistIdCard(boolean isExistIdCard) {
        this.isExistIdCard = isExistIdCard;
    }

    public List<PoliceBiosBean> getPoliceBios() {
        return policeBios;
    }

    public void setPoliceBios(List<PoliceBiosBean> policeBios) {
        this.policeBios = policeBios;
    }

    @Override
    public int compareTo(@NonNull MembersBean o) {
        return Integer.parseInt(this.no) - Integer.parseInt(o.no);
    }

}
