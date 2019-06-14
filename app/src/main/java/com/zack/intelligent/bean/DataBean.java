package com.zack.intelligent.bean;

/**
 *
 */

public class DataBean {

    private boolean success;
    private String body;
    private String msg;
    private String data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "success=" + success +
                ", body='" + body + '\'' +
                '}';
    }

}
