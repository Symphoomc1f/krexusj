package com.java110.things.entity.parkingArea;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

public class ParkingAreaDto extends PageDto implements Serializable {

    private String paId;
    private String communityId;
    private String num;
    private String extPaId;
    private String createTime;
    private String statusCd;

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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getExtPaId() {
        return extPaId;
    }

    public void setExtPaId(String extPaId) {
        this.extPaId = extPaId;
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
