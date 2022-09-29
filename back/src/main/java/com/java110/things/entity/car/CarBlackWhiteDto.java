package com.java110.things.entity.car;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.Date;

public class CarBlackWhiteDto extends PageDto implements Serializable {

    private String bwId;
    private String carNum;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private String communityId;
    private String statusCd;
    private String extCarId;
    private String extBwId;
    private String paId;
    private String extPaId;


    public String getBwId() {
        return bwId;
    }

    public void setBwId(String bwId) {
        this.bwId = bwId;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getExtCarId() {
        return extCarId;
    }

    public void setExtCarId(String extCarId) {
        this.extCarId = extCarId;
    }

    public String getExtBwId() {
        return extBwId;
    }

    public void setExtBwId(String extBwId) {
        this.extBwId = extBwId;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getExtPaId() {
        return extPaId;
    }

    public void setExtPaId(String extPaId) {
        this.extPaId = extPaId;
    }
}
