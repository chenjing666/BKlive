package com.biaoke.bklive.user.bean;

/**
 * Created by hasee on 2017/4/26.
 */

public class LiveVideo_list {
    private String Id;
    private String UserId;
    private String Exp;
    private String Pv;
    private String HV;
    private String Type;
    private String Title;
    private String SnapshotUrl;
    private String VideoUrl;
    private String Format;
    private String PubTime;

    @Override
    public String toString() {
        return "LiveVideo_list{" +
                "Id='" + Id + '\'' +
                ", UserId='" + UserId + '\'' +
                ", Exp='" + Exp + '\'' +
                ", Pv='" + Pv + '\'' +
                ", HV='" + HV + '\'' +
                ", Type='" + Type + '\'' +
                ", Title='" + Title + '\'' +
                ", SnapshotUrl='" + SnapshotUrl + '\'' +
                ", VideoUrl='" + VideoUrl + '\'' +
                ", Format='" + Format + '\'' +
                ", PubTime='" + PubTime + '\'' +
                '}';
    }

    public LiveVideo_list(String id, String userId, String exp, String pv, String HV, String type, String title, String snapshotUrl, String videoUrl, String format, String pubTime) {
        Id = id;
        UserId = userId;
        Exp = exp;
        Pv = pv;
        this.HV = HV;
        Type = type;
        Title = title;
        SnapshotUrl = snapshotUrl;
        VideoUrl = videoUrl;
        Format = format;
        PubTime = pubTime;
    }

    public LiveVideo_list(String userId, String exp, String pv, String HV, String type, String title, String snapshotUrl, String videoUrl, String format, String pubTime) {
        UserId = userId;
        Exp = exp;
        Pv = pv;
        this.HV = HV;
        Type = type;
        Title = title;
        SnapshotUrl = snapshotUrl;
        VideoUrl = videoUrl;
        Format = format;
        PubTime = pubTime;
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

    public String getExp() {
        return Exp;
    }

    public void setExp(String exp) {
        Exp = exp;
    }

    public String getPv() {
        return Pv;
    }

    public void setPv(String pv) {
        Pv = pv;
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

    public String getPubTime() {
        return PubTime;
    }

    public void setPubTime(String pubTime) {
        PubTime = pubTime;
    }
}
