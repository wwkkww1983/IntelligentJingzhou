package com.zack.intelligent.bean;

import java.util.List;

/**
 * 警员出警任务
 */
public class TaskBean {

    private String id;  //出警任务ID
    private String roomId;  //监控室id
    private String code;    //任务编码
    private int taskType;  //类型 1.领取枪弹  2.归还枪弹 3.保养枪支4.报废枪支 5.紧急领取枪弹 6.临时存放
    private String taskSubType;  //领枪原因 1.涉黄2.涉赌3.涉毒4.刑事案件99.其它
    private int taskStatus;   //状态 1.未审批2.已审批3.审批不通过4.执行中5.结束
    private String policeId;  //申请任务的警员id
    private long startTime;  //任务开始时间
    private long endTime;    //任务结束时间
    private long realEndTime;  //实际结束时间
    private String approveLeadId;  //审批领导id
    private boolean isReport;     //是否报告
    private long addTime;         //添加时间
    public List<TaskPolicesBean> policesBeen;  //警员任务列表
    private String remark;
    private String targetId;

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRoomId() {
        return this.roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public int getTaskType() {
        return this.taskType;
    }
    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
    public String getTaskSubType() {
        return this.taskSubType;
    }
    public void setTaskSubType(String taskSubType) {
        this.taskSubType = taskSubType;
    }
    public int getTaskStatus() {
        return this.taskStatus;
    }
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
    public String getPoliceId() {
        return this.policeId;
    }
    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }
    public String getApproveLeadId() {
        return this.approveLeadId;
    }
    public void setApproveLeadId(String approveLeadId) {
        this.approveLeadId = approveLeadId;
    }
    public boolean getIsReport() {
        return this.isReport;
    }
    public void setIsReport(boolean isReport) {
        this.isReport = isReport;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getTargetId() {
        return this.targetId;
    }
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(long realEndTime) {
        this.realEndTime = realEndTime;
    }

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean report) {
        isReport = report;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public List<TaskPolicesBean> getPolicesBeen() {
        return policesBeen;
    }

    public void setPolicesBeen(List<TaskPolicesBean> policesBeen) {
        this.policesBeen = policesBeen;
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", code='" + code + '\'' +
                ", taskType=" + taskType +
                ", taskSubType=" + taskSubType +
                ", taskStatus=" + taskStatus +
                ", policeId='" + policeId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", realEndTime=" + realEndTime +
                ", approveLeadId='" + approveLeadId + '\'' +
                ", isReport=" + isReport +
                ", addTime=" + addTime +
                ", policesBeen=" + policesBeen +
                ", remark='" + remark + '\'' +
                ", targetId='" + targetId + '\'' +
                '}';
    }
}
