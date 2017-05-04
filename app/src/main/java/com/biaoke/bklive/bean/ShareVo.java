package com.biaoke.bklive.bean;

/**
 * Created by Howie on 2017/4/10.
 */

public class ShareVo {
    private String linkUrl;//点击分享的链接地址
    private String title;//分享的标题
    private String imgUrl;//分享的图片
    private String content;//分享内容

    public ShareVo(String linkUrl, String title, String imgUrl, String content) {
        this.linkUrl = linkUrl;
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
    }

    public ShareVo(String linkUrl, String title) {
        this.linkUrl = linkUrl;
        this.title = title;
    }

    public ShareVo(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}