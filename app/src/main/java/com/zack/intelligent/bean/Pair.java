package com.zack.intelligent.bean;

/**
 * Created by Administrator on 2017-07-08.
 */

public class Pair {

    public Pair(String taskItemId, String objectType) {
        this.taskItemId = taskItemId;
        this.objectType = objectType;
    }

    private String taskItemId;
    private String objectType;

    public String getTaskItemId() {
        return taskItemId;
    }

    public String getObjectType() {
        return objectType;
    }

    @Override
    public String toString() {
        return objectType;
    }
}
