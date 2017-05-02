package com.biaoke.bklive.bean;

/**
 * Created by hasee on 2017/4/25.
 */

public class PrivateMessageBean {
    private String ImageUrl;
    private String Level;
    private String NickName;
    private String Message;
    private int CurrentTime;
    private String MsgNum;
    private String Sex;

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    @Override
    public String toString() {
        return "PrivateMessageBean{" +
                "ImageUrl='" + ImageUrl + '\'' +
                ", Level='" + Level + '\'' +
                ", NickName='" + NickName + '\'' +
                ", Message='" + Message + '\'' +
                ", CurrentTime='" + CurrentTime + '\'' +
                ", MsgNum='" + MsgNum + '\'' +
                ", Sex='" + Sex + '\'' +
                '}';
    }

    public PrivateMessageBean(String imageUrl, String level, String nickName, String message, int currentTime, String msgNum, String sex) {
        ImageUrl = imageUrl;
        Level = level;
        NickName = nickName;
        Message = message;
        CurrentTime = currentTime;
        MsgNum = msgNum;
        Sex = sex;
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

    public int getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(int currentTime) {
        CurrentTime = currentTime;
    }

    public String getMsgNum() {
        return MsgNum;
    }

    public void setMsgNum(String msgNum) {
        MsgNum = msgNum;
    }
}
