package com.java110.things.service.yld04;

import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.service.IAssessControlProcess;
import org.springframework.stereotype.Service;

/**
 * 伊兰度 门禁设备
 */
@Service
public class Yld04AssessControlProcessAdapt implements IAssessControlProcess {
    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        return null;
    }

    @Override
    public void addFee(MachineDto machineDto, UserFaceDto userFaceDto) {

    }

    @Override
    public void updateFee(MachineDto machineDto, UserFaceDto userFaceDto) {

    }
}
