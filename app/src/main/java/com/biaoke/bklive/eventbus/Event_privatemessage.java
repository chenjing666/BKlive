package com.biaoke.bklive.eventbus;

/**
 * Created by hasee on 2017/4/25.
 */

public class Event_privatemessage {
    private String Msg;

    public Event_privatemessage(String msg) {
        Msg = msg;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
