package com.java110.things.entity.accessControl;

import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;

import java.io.Serializable;

/**
 * @ClassName SyncGetTaskResultDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/6 17:39
 * @Version 1.0
 * add by wuxw 2020/6/6
 **/
public class SyncGetTaskResultDto implements Serializable {

    private String cmd;

    private String taskId;

    private String taskInfo;

    private CommunityDto communityDto;

    private MachineDto machineDto;

    private UserFaceDto userFaceDto;


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }

    public UserFaceDto getUserFaceDto() {
        return userFaceDto;
    }

    public void setUserFaceDto(UserFaceDto userFaceDto) {
        this.userFaceDto = userFaceDto;
    }

    public CommunityDto getCommunityDto() {
        return communityDto;
    }

    public void setCommunityDto(CommunityDto communityDto) {
        this.communityDto = communityDto;
    }

    public MachineDto getMachineDto() {
        return machineDto;
    }

    public void setMachineDto(MachineDto machineDto) {
        this.machineDto = machineDto;
    }
}
