package com.biaoke.bklive.user.bean;

/**
 * Created by hasee on 2017/4/28.
 */

public class MyFansBean {
    //    "UserId": "1001",
//            "NickName": "昵称都不",
//            "IconUrl": "http://omy78z02r.bkt.clouddn.com/icon-1001-up.jpg",
//            "性别": "女",
//            "Level": "0",
//            "签名": "uxjjdj"
    private String UserId;
    private String NickName;
    private String IconUrl;
    private String Sex;
    private String Level;
    private String Signture;

    @Override
    public String toString() {
        return "MyFansBean{" +
                "UserId='" + UserId + '\'' +
                ", NickName='" + NickName + '\'' +
                ", IconUrl='" + IconUrl + '\'' +
                ", Sex='" + Sex + '\'' +
                ", Level='" + Level + '\'' +
                ", Signture='" + Signture + '\'' +
                '}';
    }

    public MyFansBean(String userId, String nickName, String iconUrl, String sex, String level, String signture) {
        UserId = userId;
        NickName = nickName;
        IconUrl = iconUrl;
        Sex = sex;
        Level = level;
        Signture = signture;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getSignture() {
        return Signture;
    }

    public void setSignture(String signture) {
        Signture = signture;
    }
}
