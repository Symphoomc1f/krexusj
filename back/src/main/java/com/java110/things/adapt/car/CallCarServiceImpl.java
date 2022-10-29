package com.java110.things.adapt.car;

import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarBlackWhiteService;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.fee.ITempCarFeeConfigService;
import com.java110.things.service.hc.ICarCallHcService;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
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

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private ICarCallHcService carCallHcServiceImpl;

    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;

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
    private ResultDto outParkingArea(String type, String carNum, MachineDto machineDto) throws Exception {

        //查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos == null || carInoutDtos.size() != 1) {
            return new ResultDto(ResultDto.ERROR, "车辆未进场");
        }

        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(carInoutDtos.get(0).getPaId());
        tempCarFeeConfigDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.size() < 1) {
            return new ResultDto(ResultDto.SUCCESS, "未找到收费标准");
        }


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
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setCarType(type);
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        ResultDto resultDto = carInoutServiceImpl.saveCarInout(carInoutDto);

        //异步上报HC小区管理系统
        carCallHcServiceImpl.carInout(carInoutDto);


        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }
        return new ResultDto(ResultDto.SUCCESS, "开门");
    }


}
