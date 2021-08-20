package com.java110.things.entity.openDoor;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName OpenDoor
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/22 23:12
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
public class OpenDoorDto extends PageDto implements Serializable {
    private String openId;
    private String machineId;
    private String machineCode;
    private String machineName;
    private String openTypeCd;
    private String openTypeCdName;
    private String userId;
    private String userName;
    private String machineIp;

    private String hat;
    private String hatName;

    private String modelFace;

    private String face;

    private String similarity;

    private String amountOwed;

    private String createTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getOpenTypeCd() {
        return openTypeCd;
    }

    public void setOpenTypeCd(String openTypeCd) {
        this.openTypeCd = openTypeCd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHat() {
        return hat;
    }

    public void setHat(String hat) {
        this.hat = hat;
    }


    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }

    public String getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(String amountOwed) {
        this.amountOwed = amountOwed;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getModelFace() {
        return modelFace;
    }

    public void setModelFace(String modelFace) {
        this.modelFace = modelFace;
    }

    public String getOpenTypeCdName() {
        return openTypeCdName;
    }

    public void setOpenTypeCdName(String openTypeCdName) {
        this.openTypeCdName = openTypeCdName;
    }

    public String getHatName() {
        return hatName;
    }

    public void setHatName(String hatName) {
        this.hatName = hatName;
    }
}
