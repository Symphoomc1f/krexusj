package com.java110.things.entity.car;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.Date;

public class CarBlackWhiteDto extends PageDto implements Serializable {


    //黑白名单 黑名单
    public static final String BLACK_WHITE_BLACK = "1111";
    //白名单
    public static final String BLACK_WHITE_WHITE = "2222";

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
    private String[] paIds;
    private String extPaId;
    private String blackWhite;


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

    public String getBlackWhite() {
        return blackWhite;
    }

    public void setBlackWhite(String blackWhite) {
        this.blackWhite = blackWhite;
    }

    public String[] getPaIds() {
        return paIds;
    }

    public void setPaIds(String[] paIds) {
        this.paIds = paIds;
    }
}
