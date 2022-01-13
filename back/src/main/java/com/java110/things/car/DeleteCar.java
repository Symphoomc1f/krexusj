package com.java110.things.car;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.service.machine.IMachineFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 添加更新人脸
 */
@Component
public class DeleteCar extends BaseCar {

    @Autowired
    private IMachineFaceService machineFaceService;

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    public void deleteCar(HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {
        CarProcessFactory.getCarImpl().deleteCar(heartbeatTaskDto);

//        MachineFaceDto machineFaceDto = new MachineFaceDto();
//        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
//        machineFaceDto.setMachineId(machineDto.getMachineId());
//        machineFaceDto.setTaskId(heartbeatTaskDto.getTaskid());
//        //machineFaceDto.set
//        machineFaceService.deleteMachineFace(machineFaceDto);
    }
}
