package com.java110.things.entity.accessControl;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

public class UserFaceDto extends PageDto implements Serializable {

    private String userId;

    private String faceBase64;

    private String startTime;

    private String endTime;

    private String name;

    private String idNumber;

    private String remark;

    private String facePath;

    private String machineId;

    private String machineCode;

    private String machineName;

    private String machineIp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFaceBase64() {
        return faceBase64;
    }

    public void setFaceBase64(String faceBase64) {
        this.faceBase64 = faceBase64;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
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
}
