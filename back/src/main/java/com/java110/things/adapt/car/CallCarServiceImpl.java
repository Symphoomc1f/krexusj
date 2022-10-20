package com.java110.things.adapt.car;

import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarBlackWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 摄像头业务处理类
 */
@Service
public class CallCarServiceImpl implements ICallCarService {

    @Autowired
    private ICarBlackWhiteService carBlackWhiteServiceImpl;

    @Override
    public ResultDto ivsResult(String type, String carNum, MachineDto machineDto) throws Exception {

        String machineDirection = machineDto.getDirection();
        ResultDto resultDto = null;
        switch (machineDirection) {
            case MachineDto.MACHINE_DIRECTION_ENTER: // 车辆进场
                resultDto = enterParkingArea(type, carNum, machineDto);
                break;
            case MachineDto.MACHINE_DIRECTION_OUT://车辆出场
                resultDto = outParkingArea(type, carNum, machineDto);
                break;
            default:
                resultDto = new ResultDto(ResultDto.ERROR, "设备信息有误");
        }

        return resultDto;
    }

    /**
     * 车辆出场
     *
     * @param type       车牌类型
     * @param carNum     车牌号
     * @param machineDto 设备信息
     * @return
     */
    private ResultDto outParkingArea(String type, String carNum, MachineDto machineDto) {
        return new ResultDto(ResultDto.SUCCESS, "开门");
    }

    /**
     * 车辆进场
     *
     * @param type       车牌类型
     * @param carNum     车牌号
     * @param machineDto 设备信息
     * @return
     */
    private ResultDto enterParkingArea(String type, String carNum, MachineDto machineDto) throws Exception {

        //1.0 判断是否为黑名单
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaId(machineDto.getLocationObjId());
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_BLACK);
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //黑名单车辆不能进入
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            return new ResultDto(ResultDto.ERROR, "黑名单车辆(" + carNum + ")不能进入");
        }
        //2.0 进场


        return new ResultDto(ResultDto.SUCCESS, "开门");
    }


}
