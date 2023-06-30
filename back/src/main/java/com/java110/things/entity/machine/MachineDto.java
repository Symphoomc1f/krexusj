package com.java110.things.entity.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName MachineDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 23:33
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class MachineDto extends PageDto implements Serializable {

    public static final String MACHINE_TYPE_CAR = "9996";
    public static final String MACHINE_TYPE_OTHER_CAR = "9995"; // 第三方道闸平台
    public static final String MACHINE_TYPE_ACCESS_CONTROL = "9999";
    public static final String LOCATION_TYPE_PARKING_AREA = "4000"; //停车场

    public static final String LOCATION_TYPE_DEPARTMENT = "5000"; //部门
    public static final String LOCATION_TYPE_COMMUNITY = "1000"; //停车场

    public static final String MACHINE_DIRECTION_ENTER = "3306"; // 进场
    public static final String MACHINE_DIRECTION_OUT = "3307"; // 出场


    // 考勤机
    public static final String MACHINE_TYPE_ATTENDANCE = "9997";
    private String machineMac;
    private String machineId;
    private String machineCode;
    private String authCode;
    private String machineVersion;
    private String communityId;
    private String machineName;
    private String machineTypeCd;
    private String machineTypeCdName;
    private String machineIp;
    private String statusCd;
    private String oem;
    private String extMachineId;
    private String hmId;
    private String taskId;
    private String heartbeatTime;
    private String locationObjId;
    private String locationType;
    private String direction;
    private String thirdMachineId;
    private String wsUrl;


    private List<MachineAttrDto> machineAttrDtos;

    public String getMachineMac() {
        return machineMac;
    }

    public void setMachineMac(String machineMac) {
        this.machineMac = machineMac;
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

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getMachineVersion() {
        return machineVersion;
    }

    public void setMachineVersion(String machineVersion) {
        this.machineVersion = machineVersion;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineTypeCd() {
        return machineTypeCd;
    }

    public void setMachineTypeCd(String machineTypeCd) {
        this.machineTypeCd = machineTypeCd;
    }

    public String getMachineTypeCdName() {
        return machineTypeCdName;
    }

    public void setMachineTypeCdName(String machineTypeCdName) {
        this.machineTypeCdName = machineTypeCdName;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public String getExtMachineId() {
        return extMachineId;
    }

    public void setExtMachineId(String extMachineId) {
        this.extMachineId = extMachineId;
    }

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(String heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public String getLocationObjId() {
        return locationObjId;
    }

    public void setLocationObjId(String locationObjId) {
        this.locationObjId = locationObjId;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }


    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<MachineAttrDto> getMachineAttrDtos() {
        return machineAttrDtos;
    }

    public void setMachineAttrDtos(List<MachineAttrDto> machineAttrDtos) {
        this.machineAttrDtos = machineAttrDtos;
    }

    public String getThirdMachineId() {
        return thirdMachineId;
    }

    public void setThirdMachineId(String thirdMachineId) {
        this.thirdMachineId = thirdMachineId;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }
}
