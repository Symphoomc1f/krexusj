package com.java110.things.service.car.aiCar;


import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.service.car.ICarProcess;
import org.springframework.stereotype.Service;

/**
 * 智能停车系统
 */
@Service("aiCarSocketProcessAdapt")
public class AiCarSocketProcessAdapt implements ICarProcess {
    @Override
    public void initCar() {

    }

    @Override
    public int getCarNum() {
        return 0;
    }

    @Override
    public String getCar(UserFaceDto userFaceDto) {
        return null;
    }

    @Override
    public void addAndUpdateCar(UserFaceDto userFaceDto) {

    }

    @Override
    public void deleteCar(HeartbeatTaskDto heartbeatTaskDto) {

    }

    @Override
    public void restartMachine(MachineDto machineDto) {

    }

    @Override
    public void openDoor(MachineDto machineDto) {

    }

    @Override
    public String httpFaceResult(String data) {
        return null;
    }
}
