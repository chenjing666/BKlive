package com.biaoke.bklive.user.bean;

/**
 * Created by hasee on 2017/5/3.
 */

public class Charm_boardBean {
    private String userId;
    private String nickName;
    private String headUrl;
    private String sex;
    private String level;
    private String signture;
    private String charm;

    @Override
    public String toString() {
        return "Charm_boardBean{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", sex='" + sex + '\'' +
                ", level='" + level + '\'' +
                ", signture='" + signture + '\'' +
                ", charm='" + charm + '\'' +
                '}';
    }

    public Charm_boardBean(String userId, String nickName, String headUrl, String sex, String level, String signture, String charm) {
        this.userId = userId;
        this.nickName = nickName;
        this.headUrl = headUrl;
        this.sex = sex;
        this.level = level;
        this.signture = signture;
        this.charm = charm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSignture() {
        return signture;
    }

    public void setSignture(String signture) {
        this.signture = signture;
    }

    public String getCharm() {
        return charm;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }
}
