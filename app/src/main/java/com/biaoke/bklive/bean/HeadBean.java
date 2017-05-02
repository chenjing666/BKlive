package com.biaoke.bklive.bean;

/**
 * Created by Administrator on 2017/4/11.
 */

public class HeadBean {
    private String imgUrl;
    private String userId;
    private String userlistnickName;

    public String getUserlistnickName() {
        return userlistnickName;
    }

    public void setUserlistnickName(String userlistnickName) {
        this.userlistnickName = userlistnickName;
    }

    public HeadBean(String imgUrl, String userId, String userlistnickName) {
        this.imgUrl = imgUrl;
        this.userId=userId;
        this.userlistnickName = userlistnickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
