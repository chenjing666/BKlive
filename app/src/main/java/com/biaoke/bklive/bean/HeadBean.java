package com.biaoke.bklive.bean;

/**
 * Created by Administrator on 2017/4/11.
 */

public class HeadBean {
    private String imgUrl;
    private String userId;
    public HeadBean(String imgUrl,String userId) {
        this.imgUrl = imgUrl;
        this.userId=userId;
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
