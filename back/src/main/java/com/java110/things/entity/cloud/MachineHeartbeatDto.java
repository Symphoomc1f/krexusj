package com.java110.things.entity.cloud;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName MachineUploadFaceDto
 * @Description TODO 设备心跳对象
 * @Author wuxw
 * @Date 2020/5/27 8:31
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class MachineHeartbeatDto implements Serializable {


    private String machineCode;

    private String taskId;

    private String heartbeatTime;

    private String extCommunityId;

    public MachineHeartbeatDto(String machineCode, String heartbeatTime) {
        this.machineCode = machineCode;
        this.heartbeatTime = heartbeatTime;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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

    public String getExtCommunityId() {
        return extCommunityId;
    }

    public void setExtCommunityId(String extCommunityId) {
        this.extCommunityId = extCommunityId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
