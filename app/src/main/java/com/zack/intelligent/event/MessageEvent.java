package com.zack.intelligent.event;

/**
 * Created by Administrator on 2017-08-04.
 */

public class MessageEvent {

    String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
