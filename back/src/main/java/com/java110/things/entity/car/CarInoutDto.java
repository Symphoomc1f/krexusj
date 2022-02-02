package com.java110.things.entity.car;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

public class CarInoutDto extends PageDto implements Serializable {

    // 进场
    public static final String INOUT_TYPE_IN = "1001";
    public static final String INOUT_TYPE_OUT = "2002";

    private String inoutId;
    private String communityId;
    private String carNum;
    private String carType;
    private String openTime;
    private String gateName;
    private String createTime;
    private String payCharge;
    private String realCharge;
    private String payType;
    private String remark;
    private String inoutType;
    private String statusCd;

    public String getInoutId() {
        return inoutId;
    }

    public void setInoutId(String inoutId) {
        this.inoutId = inoutId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getGateName() {
        return gateName;
    }

    public void setGateName(String gateName) {
        this.gateName = gateName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPayCharge() {
        return payCharge;
    }

    public void setPayCharge(String payCharge) {
        this.payCharge = payCharge;
    }

    public String getRealCharge() {
        return realCharge;
    }

    public void setRealCharge(String realCharge) {
        this.realCharge = realCharge;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInoutType() {
        return inoutType;
    }

    public void setInoutType(String inoutType) {
        this.inoutType = inoutType;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
