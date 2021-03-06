package com.java110.things.entity.parkingArea;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.List;

public class ParkingBoxDto extends PageDto implements Serializable {
    private String boxId;
    private String boxName;
    private String communityId;
    private String tempCarIn;
    private String fee;
    private String blueCarIn;
    private String yelowCarIn;
    private String remark;
    private String createTime;
    private String statusCd;
    private String extBoxId;
    private String paId;
    private String paNum;
    private String defaultArea;
    private String num;
    private String extPaId;
    private List<ParkingBoxAreaDto> parkingBoxAreas;

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getTempCarIn() {
        return tempCarIn;
    }

    public void setTempCarIn(String tempCarIn) {
        this.tempCarIn = tempCarIn;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getBlueCarIn() {
        return blueCarIn;
    }

    public void setBlueCarIn(String blueCarIn) {
        this.blueCarIn = blueCarIn;
    }

    public String getYelowCarIn() {
        return yelowCarIn;
    }

    public void setYelowCarIn(String yelowCarIn) {
        this.yelowCarIn = yelowCarIn;
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

    public String getExtBoxId() {
        return extBoxId;
    }

    public void setExtBoxId(String extBoxId) {
        this.extBoxId = extBoxId;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getPaNum() {
        return paNum;
    }

    public void setPaNum(String paNum) {
        this.paNum = paNum;
    }

    public List<ParkingBoxAreaDto> getParkingBoxAreas() {
        return parkingBoxAreas;
    }

    public void setParkingBoxAreas(List<ParkingBoxAreaDto> parkingBoxAreas) {
        this.parkingBoxAreas = parkingBoxAreas;
    }

    public String getDefaultArea() {
        return defaultArea;
    }

    public void setDefaultArea(String defaultArea) {
        this.defaultArea = defaultArea;
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
}
