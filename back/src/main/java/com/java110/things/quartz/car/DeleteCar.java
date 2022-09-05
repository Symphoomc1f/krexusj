package com.java110.things.quartz.car;

import com.java110.things.entity.accessControl.CarResultDto;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.adapt.car.ICarService;
import com.java110.things.service.machine.IMachineFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 添加更新人脸
 */
@Component
public class DeleteCar extends BaseCar {

    @Autowired
    private IMachineFaceService machineFaceService;

    @Autowired
    private ICarService carService;

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    public void deleteCar(HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {
        CarDto tmpCarDto = new CarDto();
        tmpCarDto.setCarId(heartbeatTaskDto.getTaskinfo());
        ResultDto resultDto = carService.getCar(tmpCarDto);

        if (resultDto.getTotal() < 1) {
            throw new IllegalArgumentException("未找到 车辆记录");
        }
        List<CarDto> carDtoList = (List<CarDto>) resultDto.getData();
        CarDto carDto = carDtoList.get(0);
        CarResultDto carResultDto = new CarResultDto();
        carResultDto.setCarNum(carDto.getCarNum());
        carResultDto.setCarId(carDto.getCardId());

        carResultDto.setPaId(carDto.getPaId());

        CarProcessFactory.getCarImpl().deleteCar(carResultDto);

//        MachineFaceDto machineFaceDto = new MachineFaceDto();
//        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
//        machineFaceDto.setMachineId(machineDto.getMachineId());
//        machineFaceDto.setTaskId(heartbeatTaskDto.getTaskid());
//        //machineFaceDto.set
//        machineFaceService.deleteMachineFace(machineFaceDto);

         carDto = new CarDto();
        carDto.setCarId(heartbeatTaskDto.getTaskinfo());
        carDto.setCommunityId(communityDto.getCommunityId());

        carService.deleteCar(carDto);

    }
}
