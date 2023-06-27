package com.java110.things.adapt.car;

import com.java110.things.adapt.car.compute.IComputeTempCarFee;
import com.java110.things.entity.car.*;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextCacheDto;
import com.java110.things.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.ParkingAreaTextFactory;
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
import com.java110.things.util.StringUtil;
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
    public ResultParkingAreaTextDto ivsResult(String type, String carNum, MachineDto machineDto) throws Exception {

        String machineDirection = machineDto.getDirection();
        ResultParkingAreaTextDto resultParkingAreaTextDto = null;
        switch (machineDirection) {
            case MachineDto.MACHINE_DIRECTION_ENTER: // 车辆进场
                resultParkingAreaTextDto = enterParkingArea(type, carNum, machineDto);
                break;
            case MachineDto.MACHINE_DIRECTION_OUT://车辆出场
                resultParkingAreaTextDto = outParkingArea(type, carNum, machineDto);
                break;
            default:
                resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, "系统异常");
        }

        return resultParkingAreaTextDto;
    }

    /**
     * 车辆出场
     *
     * @param type       车牌类型
     * @param carNum     车牌号
     * @param machineDto 设备信息
     * @return
     */
    private ResultParkingAreaTextDto outParkingArea(String type, String carNum, MachineDto machineDto) throws Exception {

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
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, carNum, "未进场", "", "", carNum + "未进场");
        }

        //判断是否为白名单
        if (judgeWhiteCar(machineDto, carNum, parkingAreaDtos.get(0), type, carInoutDtos)) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, carNum, "白名单车辆", "", "", carNum + "白名单车辆");
        }

        //判断车辆是否为月租车
        ParkingAreaTextCacheDto parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos.get(0).getPaId(), ParkingAreaTextFactory.TYPE_CD_MONTH_CAR_OUT);
        int day = judgeOwnerCar(machineDto, carNum, parkingAreaDtos.get(0), type, carInoutDtos);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", day + "");
        if (day > 0) {
            if (parkingAreaTextCacheDto != null) {
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, parkingAreaTextCacheDto);
            }
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, carNum, "月租车车辆,剩余" + day + "天", "", "", carNum + "月租车车辆");
        }

        //检查是否支付完成
        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos.get(0).getPaId(), ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_OUT);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", "");
        if (TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDtos.get(0))) {
            if (parkingAreaTextCacheDto != null) {
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, parkingAreaTextCacheDto);
            }
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, carNum, "临时车，欢迎光临", "", "", carNum + "临时车，欢迎光临");
        }

        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(carInoutDtos.get(0).getPaId());
        tempCarFeeConfigDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.size() < 1) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, "错误");
        }

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.get(0).getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos.get(0));

        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos.get(0).getPaId(), ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_NO_PAY);

        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, result.getHours() + "", result.getMin() + "", result.getPayCharge() + "", "");

        if (parkingAreaTextCacheDto != null) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, parkingAreaTextCacheDto);
        }
        return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, "停车" + result.getHours() + "小时" + result.getMin() + "分钟", "请缴费" + result.getPayCharge() + "元", "", "",
                carNum + "停车" + result.getHours() + "小时" + result.getMin() + "分钟,请缴费" + result.getPayCharge() + "元");
    }


    /**
     * 替换配置中的配置
     *
     * @param parkingAreaTextCacheDto
     * @param carNum
     * @param hours
     * @param min
     * @param payCharge
     */
    private void replaceParkingAreaTextCache(ParkingAreaTextCacheDto parkingAreaTextCacheDto, String carNum, String hours, String min, String payCharge, String day) {

        if (parkingAreaTextCacheDto == null) {
            return;
        }
        String replaceAfter = "";
        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText1())) {
            replaceAfter = parkingAreaTextCacheDto.getText1()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge);
            parkingAreaTextCacheDto.setText1(replaceAfter);
        }

        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText2())) {
            replaceAfter = parkingAreaTextCacheDto.getText2()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setText2(replaceAfter);
        }

        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText3())) {
            replaceAfter = parkingAreaTextCacheDto.getText3()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setText3(replaceAfter);
        }
        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText4())) {
            replaceAfter = parkingAreaTextCacheDto.getText4()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setText4(replaceAfter);
        }
        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getVoice())) {
            replaceAfter = parkingAreaTextCacheDto.getVoice()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setVoice(replaceAfter);
        }
    }

    /**
     * 判断是否为月租车
     *
     * @param machineDto
     * @param carNum
     * @param parkingAreaDto
     * @param type
     * @param carInoutDtos
     * @return -1 表示临时车
     */
    private int judgeOwnerCar(MachineDto machineDto, String carNum, ParkingAreaDto parkingAreaDto, String type, List<CarInoutDto> carInoutDtos) throws Exception {

        CarDto carDto = new CarDto();
        carDto.setPaId(parkingAreaDto.getPaId());
        carDto.setCarNum(carNum);
        List<CarDto> carDtos = carServiceImpl.queryCars(carDto);

        if (carDtos == null || carDtos.size() < 1) {
            return -1;
        }

        int day = DateUtil.differentDays(carDtos.get(0).getEndTime(), DateUtil.getCurrentDate());

        if (day < 0) {
            return 0;
        }
        return day;
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
    private ResultParkingAreaTextDto enterParkingArea(String type, String carNum, MachineDto machineDto) throws Exception {

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
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, "黑名单车辆", carNum + "不能进入", "", "", "黑名单车辆" + carNum + "不能进入");
        }


        //判断车辆是否在 场内
        CarInoutDto inoutDto = new CarInoutDto();
        inoutDto.setCarNum(carNum);
        inoutDto.setPaId(parkingAreaDtos.get(0).getPaId());
        inoutDto.setState("1");
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(inoutDto);
        //黑名单车辆不能进入
        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, carNum, "车辆已在场内", "", "", carNum + "车辆已在场内");
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
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, carNum, "入场失败", "", "", carNum + "入场失败");
        }

        //判断车辆是否为月租车
        int day = judgeOwnerCar(machineDto, carNum, parkingAreaDtos.get(0), type, carInoutDtos);
        ParkingAreaTextCacheDto parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos.get(0).getPaId(), ParkingAreaTextFactory.TYPE_CD_MONTH_CAR_IN);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", day + "");
        if (day > -1 && parkingAreaTextCacheDto != null) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, parkingAreaTextCacheDto);
        }
        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos.get(0).getPaId(), ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_IN);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", "");
        if (day < 0 && parkingAreaTextCacheDto != null) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_SUCCESS, parkingAreaTextCacheDto);
        }

        return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_ERROR, carNum, "欢迎光临", "", "", carNum + "欢迎光临");
    }


}
