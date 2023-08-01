package com.java110.things.entity.parkingArea;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

public class ParkingBoxAreaDto extends PageDto implements Serializable {
    public static final String DEFAULT_AREA_TRUE = "T";
    public static final String DEFAULT_AREA_FALSE = "F";
    private String baId;
    private String boxId;
    private String paId;
    private String communityId;
    private String defaultArea;
    private String remark;
    private String createTime;
    private String statusCd;

    public String getBaId() {
        return baId;
    }

    public void setBaId(String baId) {
        this.baId = baId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getDefaultArea() {
        return defaultArea;
    }

    public void setDefaultArea(String defaultArea) {
        this.defaultArea = defaultArea;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
