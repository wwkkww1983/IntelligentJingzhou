package com.zack.intelligent.bean;


import java.util.List;

/**
 * 子任务数据
 */
public class TaskItemsBean {

    private String id;
    private String taskId;
    private String taskPoliceId;    //执行任务警员id
    private String taskPoliceName;  //执行任务警员姓名
    private int taskItemType;     //单项任务类型1.领取/归还/枪支2.领取/归还弹/弹夹
    private int taskItemStatus;   //单项任务状态1.领取中2.执行中3.完成
    private int objectTypeId;   //子对象类型（枪/弹药）
    private int objectNumber;   //子对象数量
    private String belongId;    //所属id
    private String title;       //标题
    private int oneOperNumber;  //领取数量
    private int twoOperNumber;  //归还数量
    private List<OperBean> outGunOpers;  //枪支领出

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

    public String getTaskPoliceId() {
        return this.taskPoliceId;
    }

    public void setTaskPoliceId(String taskPoliceId) {
        this.taskPoliceId = taskPoliceId;
    }

    public String getTaskPoliceName() {
        return taskPoliceName;
    }

    public void setTaskPoliceName(String taskPoliceName) {
        this.taskPoliceName = taskPoliceName;
    }

    public int getTaskItemType() {
        return this.taskItemType;
    }

    public void setTaskItemType(int taskItemType) {
        this.taskItemType = taskItemType;
    }

    public int getTaskItemStatus() {
        return this.taskItemStatus;
    }

    public void setTaskItemStatus(int taskItemStatus) {
        this.taskItemStatus = taskItemStatus;
    }

    public int getObjectTypeId() {
        return this.objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getObjectNumber() {
        return this.objectNumber;
    }

    public void setObjectNumber(int objectNumber) {
        this.objectNumber = objectNumber;
    }

    public String getBelongId() {
        return this.belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOneOperNumber() {
        return this.oneOperNumber;
    }

    public void setOneOperNumber(int oneOperNumber) {
        this.oneOperNumber = oneOperNumber;
    }

    public int getTwoOperNumber() {
        return this.twoOperNumber;
    }

    public void setTwoOperNumber(int twoOperNumber) {
        this.twoOperNumber = twoOperNumber;
    }

    public List<OperBean> getOutGunOpers() {
        return outGunOpers;
    }

    public void setOutGunOpers(List<OperBean> outGunOpers) {
        this.outGunOpers = outGunOpers;
    }

    @Override
    public String toString() {
        return "TaskItemsBean{" +
                "id='" + id + '\'' +
                ", taskPoliceId='" + taskPoliceId + '\'' +
                ", taskItemType=" + taskItemType +
                ", taskItemStatus=" + taskItemStatus +
                ", objectTypeId=" + objectTypeId +
                ", objectNumber=" + objectNumber +
                ", belongId='" + belongId + '\'' +
                ", title='" + title + '\'' +
                ", oneOperNumber=" + oneOperNumber +
                ", twoOperNumber=" + twoOperNumber +
                ", outGunOpers=" + outGunOpers +
                '}';
    }
}
