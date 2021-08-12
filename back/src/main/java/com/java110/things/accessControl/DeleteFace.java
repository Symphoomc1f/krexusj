package com.java110.things.accessControl;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ImageFactory;
import org.springframework.stereotype.Component;

import java.io.File;

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
        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar +heartbeatTaskDto.getTaskinfo() + ".jpg");
        AccessControlProcessFactory.getAssessControlProcessImpl().deleteFace(machineDto,heartbeatTaskDto);
    }
}
