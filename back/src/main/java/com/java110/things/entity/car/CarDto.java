package com.java110.things.entity.car;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.Date;

public class CarDto extends PageDto implements Serializable {

    private String carId;
    private String carNum;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private String communityId;
    private String statusCd;
    private String extCarId;
    private String personName;
    private String personTel;
    private String personId;
    private String paId;
    private String extPaId;
    private double cycles;
    private String cardId;
    private long parkingNum;


    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonTel() {
        return personTel;
    }

    public void setPersonTel(String personTel) {
        this.personTel = personTel;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public double getCycles() {
        return cycles;
    }

    public void setCycles(double cycles) {
        this.cycles = cycles;
    }

    public String getExtPaId() {
        return extPaId;
    }

    public void setExtPaId(String extPaId) {
        this.extPaId = extPaId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public long getParkingNum() {
        return parkingNum;
    }

    public void setParkingNum(long parkingNum) {
        this.parkingNum = parkingNum;
    }
}
