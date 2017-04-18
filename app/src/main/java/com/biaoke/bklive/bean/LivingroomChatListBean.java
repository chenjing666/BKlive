package com.biaoke.bklive.bean;

/**
 * Created by hasee on 2017/4/17.
 */

public class LivingroomChatListBean {
    private String ImageUrl;
    private String Level;
    private String NickName;
    private String Message;

    @Override
    public String toString() {
        return "LivingroomChatListBean{" +
                "ImageUrl='" + ImageUrl + '\'' +
                ", Level='" + Level + '\'' +
                ", NickName='" + NickName + '\'' +
                ", Message='" + Message + '\'' +
                '}';
    }

    public LivingroomChatListBean(String imageUrl, String level, String nickName, String message) {
        ImageUrl = imageUrl;
        Level = level;
        NickName = nickName;
        Message = message;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
