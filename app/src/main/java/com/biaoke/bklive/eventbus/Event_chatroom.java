package com.biaoke.bklive.eventbus;

/**
 * Created by hasee on 2017/4/17.
 */

public class Event_chatroom {
    private String Msg;

    public Event_chatroom(String msg) {
        Msg = msg;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
