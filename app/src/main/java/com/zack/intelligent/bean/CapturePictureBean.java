package com.zack.intelligent.bean;

/**
 * 抓拍图片数据
 */
public class CapturePictureBean {

    private String roomId;//枪库id
    private String cabId;//枪柜id
    private String photoName; //图片名称
    private String photoFile; //base64数据集
    private long addTime; //抓拍时间
    private boolean isUploaded; //是否上传完成

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCabId() {
        return cabId;
    }

    public void setCabId(String cabId) {
        this.cabId = cabId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(String photoFile) {
        this.photoFile = photoFile;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
}
