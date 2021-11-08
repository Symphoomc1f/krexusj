package com.java110.things.entity.machine;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName MachineDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 23:33
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class MachineCmdDto extends PageDto implements Serializable {
    private String cmdId;
    private String machineId;
    private String machineCode;
    private String machineTypeCd;
    private String communityId;
    private String cmdCode;
    private String cmdName;
    private String state;
    private String createTime;
    private String readTime;
    private String finishTime;

    private String objType;

    private String objTypeValue;

    public String getCmdId() {
        return cmdId;
    }

    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
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

    public String getMachineTypeCd() {
        return machineTypeCd;
    }

    public void setMachineTypeCd(String machineTypeCd) {
        this.machineTypeCd = machineTypeCd;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(String cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getCmdName() {
        return cmdName;
    }

    public void setCmdName(String cmdName) {
        this.cmdName = cmdName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getObjTypeValue() {
        return objTypeValue;
    }

    public void setObjTypeValue(String objTypeValue) {
        this.objTypeValue = objTypeValue;
    }
}
