package com.java110.things.adapt.accessControl.sxoa;

import java.io.Serializable;

public class FaceFeatureResultDto implements Serializable {
    private String ffId; //人脸特征ID
    private String ffResidentId;//住户ID
    private String ffUserId; //用户ID
    private String ffKey; // 自定义ID
    private String ffUrl; //存放的OSS key
    private String ffFeatureUrl;//人脸特征字符串
    private String ffCreateTime;
    private String ffUpdateTime;

    public String getFfId() {
        return ffId;
    }

    public void setFfId(String ffId) {
        this.ffId = ffId;
    }

    public String getFfResidentId() {
        return ffResidentId;
    }

    public void setFfResidentId(String ffResidentId) {
        this.ffResidentId = ffResidentId;
    }

    public String getFfUserId() {
        return ffUserId;
    }

    public void setFfUserId(String ffUserId) {
        this.ffUserId = ffUserId;
    }

    public String getFfKey() {
        return ffKey;
    }

    public void setFfKey(String ffKey) {
        this.ffKey = ffKey;
    }

    public String getFfUrl() {
        return ffUrl;
    }

    public void setFfUrl(String ffUrl) {
        this.ffUrl = ffUrl;
    }

    public String getFfFeatureUrl() {
        return ffFeatureUrl;
    }

    public void setFfFeatureUrl(String ffFeatureUrl) {
        this.ffFeatureUrl = ffFeatureUrl;
    }

    public String getFfCreateTime() {
        return ffCreateTime;
    }

    public void setFfCreateTime(String ffCreateTime) {
        this.ffCreateTime = ffCreateTime;
    }

    public String getFfUpdateTime() {
        return ffUpdateTime;
    }

    public void setFfUpdateTime(String ffUpdateTime) {
        this.ffUpdateTime = ffUpdateTime;
    }
}
