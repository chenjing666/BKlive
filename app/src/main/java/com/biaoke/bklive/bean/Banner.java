package com.biaoke.bklive.bean;

/**
 * Created by hasee on 2017/4/8.
 */

public class Banner {
    private String imageUrl;

    @Override
    public String toString() {
        return "Banner{" +
                "imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public Banner(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
