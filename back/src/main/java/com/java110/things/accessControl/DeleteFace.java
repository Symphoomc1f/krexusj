package com.java110.things.accessControl;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.service.machine.IMachineFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 添加更新人脸
 */
@Component
public class DeleteFace extends BaseAccessControl {

    @Autowired
    private IMachineFaceService machineFaceService;

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    public void deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {
        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + heartbeatTaskDto.getTaskinfo() + ".jpg");
        AccessControlProcessFactory.getAssessControlProcessImpl().deleteFace(machineDto, heartbeatTaskDto);


        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState("W");
        machineFaceDto.setState("删除人脸待同步设备");

        machineFaceService.updateMachineFace(machineFaceDto);

        machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setTaskId(heartbeatTaskDto.getTaskid());
        //machineFaceDto.set
        ResultDto resultDto = machineFaceService.deleteMachineFace(machineFaceDto);

        machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState(resultDto.getCode() == ResultDto.SUCCESS ? "S" : "F");
        machineFaceDto.setState(resultDto.getMsg());
        machineFaceService.updateMachineFace(machineFaceDto);
    }
}
