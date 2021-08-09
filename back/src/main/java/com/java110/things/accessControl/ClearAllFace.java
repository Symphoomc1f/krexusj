package com.java110.things.accessControl;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ImageFactory;
import org.springframework.stereotype.Component;

/**
 * 添加更新人脸
 */
@Component
public class ClearAllFace extends BaseAccessControl {

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    void clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {

        //清空硬件下的人脸
        ImageFactory.clearImage(machineDto.getMachineCode());

        AccessControlProcessFactory.getAssessControlProcessImpl().clearFace(machineDto);
    }
}
