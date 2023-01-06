package com.java110.things.adapt.car;

import com.java110.things.adapt.car.compute.IComputeTempCarFee;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.car.TempCarFeeResult;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.TempCarFeeFactory;
import com.java110.things.service.car.ICarBlackWhiteService;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.fee.ITempCarFeeConfigService;
import com.java110.things.service.hc.ICarCallHcService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.Assert;
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
    private ICarService carServiceImpl;

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private ICarCallHcService carCallHcServiceImpl;

    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;

    @Autowired
    private IParkingAreaService parkingAreaServiceImpl;

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

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(machineDto.getLocationObjId());
        parkingAreaDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");

        //查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(parkingAreaDtos.get(0).getPaId());
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos == null || carInoutDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "车辆未进场");
        }

        //判断是否为白名单
        if (judgeWhiteCar(machineDto, carNum, parkingAreaDtos.get(0), type, carInoutDtos)) {
            return new ResultDto(ResultDto.SUCCESS, "车辆为白名单");
        }

        //判断车辆是否为月租车
        if (judgeOwnerCar(machineDto, carNum, parkingAreaDtos.get(0), type, carInoutDtos)) {
            return new ResultDto(ResultDto.SUCCESS, "车辆为白名单");
        }

        //检查是否支付完成
        if (TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDtos.get(0))) {
            return new ResultDto(ResultDto.SUCCESS, "已经支付完成");
        }

        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(carInoutDtos.get(0).getPaId());
        tempCarFeeConfigDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.size() < 1) {
            return new ResultDto(ResultDto.SUCCESS, "未找到收费标准");
        }

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.get(0).getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos.get(0));

        return new ResultDto(ResultDto.NO_PAY, "未支付", result);
    }

    /**
     * 判断是否为月租车
     *
     * @param machineDto
     * @param carNum
     * @param parkingAreaDto
     * @param type
     * @param carInoutDtos
     * @return
     */
    private boolean judgeOwnerCar(MachineDto machineDto, String carNum, ParkingAreaDto parkingAreaDto, String type, List<CarInoutDto> carInoutDtos) throws Exception {

        CarDto carDto = new CarDto();
        carDto.setPaId(parkingAreaDto.getPaId());
        carDto.setCarNum(carNum);
        List<CarDto> carDtos = carServiceImpl.queryCars(carDto);

        if (carDtos == null || carDtos.size() < 1) {
            return false;
        }

        if (carDtos.get(0).getEndTime().getTime() > DateUtil.getCurrentDate().getTime()) {
            return true;
        }

        return false;
    }


    /**
     * 白名单车辆
     */
    private Boolean judgeWhiteCar(MachineDto machineDto, String carNum,
                                  ParkingAreaDto parkingAreaDto,
                                  String type, List<CarInoutDto> carInoutDtos) throws Exception {

        //1.0 判断是否为黑名单
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaId(machineDto.getLocationObjId());
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_WHITE);
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //白名单直接出场
        CarInoutDto carInoutDto = null;
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            //2.0 进场
            carInoutDto = new CarInoutDto();
            carInoutDto.setCarNum(carNum);
            carInoutDto.setCarType(type);
            carInoutDto.setCommunityId(machineDto.getCommunityId());
            carInoutDto.setGateName(machineDto.getMachineName());
            carInoutDto.setInoutId(SeqUtil.getId());
            carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
            carInoutDto.setMachineCode(machineDto.getMachineCode());
            carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
            carInoutDto.setPaId(parkingAreaDto.getPaId());
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutServiceImpl.saveCarInout(carInoutDto);
            carInoutDto = new CarInoutDto();
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutDto.setInoutId(carInoutDtos.get(0).getInoutId());
            carInoutServiceImpl.updateCarInout(carInoutDto);
            //异步上报HC小区管理系统
            carCallHcServiceImpl.carInout(carInoutDto);
            return true;
        }

        return false;
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

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(machineDto.getLocationObjId());
        parkingAreaDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");

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


        //判断车辆是否在 场内
        CarInoutDto inoutDto = new CarInoutDto();
        inoutDto.setCarNum(carNum);
        inoutDto.setPaId(parkingAreaDtos.get(0).getPaId());
        inoutDto.setState("1");
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(inoutDto);
        //黑名单车辆不能进入
        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            return new ResultDto(ResultDto.ERROR, carNum + "车辆已在场内");
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
        carInoutDto.setPaId(parkingAreaDtos.get(0).getPaId());
        carInoutDto.setState("1");
        ResultDto resultDto = carInoutServiceImpl.saveCarInout(carInoutDto);

        //异步上报HC小区管理系统
        carCallHcServiceImpl.carInout(carInoutDto);


        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }
        return new ResultDto(ResultDto.SUCCESS, "开门");
    }


}
