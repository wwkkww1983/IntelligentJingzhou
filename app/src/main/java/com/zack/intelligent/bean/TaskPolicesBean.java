package com.zack.intelligent.bean;


import java.util.List;

/**
 * 任务警员数据
 */
public class TaskPolicesBean {

    private String id;  //子数据id  执行任务id
    private String policeId; //执行警员id
    private String taskId;   //任务id
    private String name;     //警员姓名
    private boolean isComplete;  //是否完成
    private boolean isResponsible;  //是否责任人
    private String no;   //警员编号
    private List<TaskItemsBean> taskItems; //任务子对象列表

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoliceId() {
        return this.policeId;
    }

    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsComplete() {
        return this.isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean getIsResponsible() {
        return this.isResponsible;
    }

    public void setIsResponsible(boolean isResponsible) {
        this.isResponsible = isResponsible;
    }

    public String getNo() {
        return this.no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isResponsible() {
        return isResponsible;
    }

    public void setResponsible(boolean responsible) {
        isResponsible = responsible;
    }

    public List<TaskItemsBean> getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(List<TaskItemsBean> taskItems) {
        this.taskItems = taskItems;
    }

    @Override
    public String toString() {
        return "TaskPolicesBean{" +
                ", id='" + id + '\'' +
                ", policeId='" + policeId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", name='" + name + '\'' +
                ", isComplete=" + isComplete +
                ", isResponsible=" + isResponsible +
                ", no='" + no + '\'' +
                ", taskItems=" + taskItems +
                '}';
    }
}
