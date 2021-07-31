package com.java110.things.service.yld04;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.service.IAssessControlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 伊兰度 门禁设备
 */
@Service("yld04AssessControlProcessAdapt")
public class Yld04AssessControlProcessAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(Yld04AssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    @Override
    public void initAssessControlProcess() {
        Function.Init();
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        //连接相机
        Function.connectCamera(machineDto.getMachineIp());

        return null;
    }

    @Override
    public void addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        Function.connectCamera(machineDto.getMachineIp());
        try {
            String image = userFaceDto.getFaceBase64();
            image = image.substring(image.indexOf("base64,") + 7);
            userFaceDto.setFaceBase64(image);
            Function.AddFace(machineDto.getMachineIp(), userFaceDto.getName(), userFaceDto.getUserId(), userFaceDto.getFaceBase64());
        } catch (Exception e) {
            logger.error("添加人脸失败", e);
        }

    }

    @Override
    public void updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {
//        Function.connectCamera(machineDto.getMachineIp());
        addFace(machineDto, userFaceDto);
    }

    @Override
    public void deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        Function.connectCamera(machineDto.getMachineIp());
        Function.deleteFace(machineDto.getMachineIp(), heartbeatTaskDto.getTaskinfo());
    }

    @Override
    public void clearFace(MachineDto machineDto) {
        Function.connectCamera(machineDto.getMachineIp());
        Function.clearFace(machineDto.getMachineIp());
    }

    /**
     * 扫描设备
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() {
        Function.searchcerme();
        logger.debug("硬件扫描完了");
        return null;
    }

}
