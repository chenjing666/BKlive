package com.biaoke.bklive.eventbus;

/**
 * Created by hasee on 2017/4/17.
 */

public class Event_chatroom_errorMsg {
    private String errorMsg;

    public Event_chatroom_errorMsg(String msg) {
        errorMsg = msg;
    }

    public String getMsg() {
        return errorMsg;
    }

    public void setMsg(String msg) {
        errorMsg = msg;
    }
}
