package com.biaoke.bklive.bean;

/**
 * Created by hasee on 2017/4/27.
 */

public class PriMsg {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private String headUrl;
    private int type;

    public String getHeadUrl() {
        return headUrl;
    }

    public PriMsg(String content, String headUrl, int type) {
        this.content = content;
        this.headUrl = headUrl;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

}
