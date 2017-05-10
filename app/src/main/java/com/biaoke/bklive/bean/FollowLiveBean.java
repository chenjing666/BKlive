package com.biaoke.bklive.bean;

/**
 * Created by hasee on 2017/4/28.
 */

public class FollowLiveBean {
    private String Id;
    private String UserId;
    private String NickName;
    private String IconUrl;
    private String Exp;
    private String Title;
    private String SnapshotUrl;
    private String VideoUrl;
    private String Format;
    private String HV;
    private String Type;
    private String Online;

    @Override
    public String toString() {
        return "FollowLiveBean{" +
                "Id='" + Id + '\'' +
                ", UserId='" + UserId + '\'' +
                ", NickName='" + NickName + '\'' +
                ", IconUrl='" + IconUrl + '\'' +
                ", Exp='" + Exp + '\'' +
                ", Title='" + Title + '\'' +
                ", SnapshotUrl='" + SnapshotUrl + '\'' +
                ", VideoUrl='" + VideoUrl + '\'' +
                ", Format='" + Format + '\'' +
                ", HV='" + HV + '\'' +
                ", Type='" + Type + '\'' +
                ", Online='" + Online + '\'' +
                '}';
    }

    public FollowLiveBean(String id, String userId, String nickName, String iconUrl, String exp, String title, String snapshotUrl, String videoUrl, String format, String HV, String type, String online) {
        Id = id;
        UserId = userId;
        NickName = nickName;
        IconUrl = iconUrl;
        Exp = exp;
        Title = title;
        SnapshotUrl = snapshotUrl;
        VideoUrl = videoUrl;
        Format = format;
        this.HV = HV;
        Type = type;
        Online = online;
    }

    public FollowLiveBean(String userId, String nickName, String iconUrl, String exp, String title, String snapshotUrl, String videoUrl, String format, String HV, String type, String online) {
        UserId = userId;
        NickName = nickName;
        IconUrl = iconUrl;
        Exp = exp;
        Title = title;
        SnapshotUrl = snapshotUrl;
        VideoUrl = videoUrl;
        Format = format;
        this.HV = HV;
        Type = type;
        Online = online;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getExp() {
        return Exp;
    }

    public void setExp(String exp) {
        Exp = exp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSnapshotUrl() {
        return SnapshotUrl;
    }

    public void setSnapshotUrl(String snapshotUrl) {
        SnapshotUrl = snapshotUrl;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getHV() {
        return HV;
    }

    public void setHV(String HV) {
        this.HV = HV;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getOnline() {
        return Online;
    }

    public void setOnline(String online) {
        Online = online;
    }
}
