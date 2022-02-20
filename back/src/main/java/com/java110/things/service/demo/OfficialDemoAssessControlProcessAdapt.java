package com.java110.things.service.demo;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.accessControl.IAssessControlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName OfficialDemoProcessAdapt
 * @Description TODO 官方门禁适配器演示类
 * @Author wuxw
 * @Date 2020/5/18 21:41
 * @Version 1.0
 * add by wuxw 2020/5/18
 **/
@Service("officialDemoAssessControlProcessAdapt")
public class OfficialDemoAssessControlProcessAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(OfficialDemoAssessControlProcessAdapt.class);

    @Override
    public void initAssessControlProcess() {

        logger.debug("这里初始化硬件，如连接相机等工作");
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        logger.debug("这里根据设备Ip获取设备人脸数量，如果设备不支持 则直接返回0");
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        logger.debug("这里根据设备Ip和人脸ID（一般用户ID）获取人脸，如果没有改功能直接返回空");
        return null;
    }

    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        return null;
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        return null;
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        return null;
    }

    @Override
    public void clearFace(MachineDto machineDto,HeartbeatTaskDto heartbeatTaskDto) {

    }

    @Override
    public List<MachineDto> scanMachine() {
        return null;
    }

    @Override
    public void mqttMessageArrived(String topic, String data) {

    }

    @Override
    public void restartMachine(MachineDto machineDto) {

    }

    @Override
    public void openDoor(MachineDto machineDto) {

    }

    @Override
    public String  httpFaceResult(String data) {
        return "";
    }
}
