package com.java110.things.accessControl;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import org.springframework.stereotype.Component;

/**
 * 添加更新人脸
 */
@Component
public class DeleteFace extends BaseAccessControl{

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    void deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto,CommunityDto communityDt) throws Exception {
        getAssessControlProcessImpl().deleteFace(machineDto,heartbeatTaskDto);
    }
}
