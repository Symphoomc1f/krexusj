package com.java110.things.adapt.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.car.compute.IComputeTempCarFee;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.app.AppAttrDto;
import com.java110.things.entity.app.AppDto;
import com.java110.things.entity.car.*;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextCacheDto;
import com.java110.things.entity.parkingArea.ParkingBoxDto;
import com.java110.things.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.ParkingAreaTextFactory;
import com.java110.things.factory.TempCarFeeFactory;
import com.java110.things.service.app.IAppService;
import com.java110.things.service.car.ICarBlackWhiteService;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.fee.ITempCarFeeConfigService;
import com.java110.things.service.hc.ICarCallHcService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.service.parkingBox.IParkingBoxService;
import com.java110.things.util.Assert;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import com.java110.things.ws.BarrierGateControlWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 摄像头业务处理类
 */
@Service
public class CallCarServiceImpl implements ICallCarService {
    Logger logger = LoggerFactory.getLogger(CallCarServiceImpl.class);
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

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IParkingBoxService parkingBoxServiceImpl;


    @Autowired
    private IAppService appServiceImpl;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResultParkingAreaTextDto ivsResult(String type, String carNum, MachineDto machineDto) throws Exception {


        String machineDirection = machineDto.getDirection();
        ResultParkingAreaTextDto resultParkingAreaTextDto = null;
        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setExtBoxId(machineDto.getLocationObjId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        BarrierGateControlDto barrierGateControlDto = new BarrierGateControlDto(BarrierGateControlDto.ACTION_INOUT, carNum, machineDto);
        sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
        switch (machineDirection) {
            case MachineDto.MACHINE_DIRECTION_ENTER: // 车辆进场
                resultParkingAreaTextDto = enterParkingArea(type, carNum, machineDto, parkingAreaDtos);
                break;
            case MachineDto.MACHINE_DIRECTION_OUT://车辆出场
                resultParkingAreaTextDto = outParkingArea(type, carNum, machineDto, parkingAreaDtos);
                if (resultParkingAreaTextDto.getCode()
                        == ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS
                        || resultParkingAreaTextDto.getCode()
                        == ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS
                        || resultParkingAreaTextDto.getCode()
                        == ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS
                        || resultParkingAreaTextDto.getCode()
                        == ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS
                ) {
                    carOut(carNum, machineDto);
                }
                break;
            default:
                resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, "系统异常");
        }

        return resultParkingAreaTextDto;
    }

    /**
     * 车辆出场 记录
     *
     * @param carNum
     */
    private void carOut(String carNum, MachineDto machineDto) throws Exception {
        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutServiceImpl.updateCarInout(carInoutDto);
        }
        carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState("3");
        carInoutDto.setRemark("正常出场");
        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setPayCharge(carInoutDtos.get(0).getPayCharge());
            carInoutDto.setRealCharge(carInoutDtos.get(0).getRealCharge());
            carInoutDto.setPayType(carInoutDtos.get(0).getPayType());
        } else {
            carInoutDto.setPayCharge("0");
            carInoutDto.setRealCharge("0");
            carInoutDto.setPayType("1");
        }
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutServiceImpl.saveCarInout(carInoutDto);
//        //异步上报HC小区管理系统
//        carCallHcServiceImpl.carInout(carInoutDto);
    }

    /**
     * 车辆出场
     *
     * @param type       车牌类型
     * @param carNum     车牌号
     * @param machineDto 设备信息
     * @return
     */
    private ResultParkingAreaTextDto outParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos) throws Exception {


        //查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);
        int day = judgeOwnerCar(machineDto, carNum, parkingAreaDtos);

        if ((carInoutDtos == null || carInoutDtos.size() < 1) && day < 1) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, null, carNum + ",车未入场", "开门失败");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_IN, carNum, "车未入场", "", "", carNum + ",车未入场", carNum);
        }

        //判断是否为白名单
        if (judgeWhiteCar(machineDto, carNum, parkingAreaDtos, type, carInoutDtos)) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",免费车辆", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS, carNum, "免费车辆", "", "", carNum + ",免费车辆", carNum);
        }

        //判断车辆是否为月租车
        ParkingAreaTextCacheDto parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_MONTH_CAR_OUT);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", day + "");
        if (day > 0) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",月租车剩余" + day + "天", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            if (parkingAreaTextCacheDto != null) {
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS, parkingAreaTextCacheDto,carNum);
            }
            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS, carNum,
                    "月租车剩余" + day + "天", "", "", carNum + ",月租车", carNum);
            resultParkingAreaTextDto.setDay(day);
            return resultParkingAreaTextDto;
        }

        //判断岗亭是否收费
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setExtBoxId(machineDto.getLocationObjId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingBoxDto> parkingBoxDtos = parkingBoxServiceImpl.queryParkingBoxs(parkingBoxDto);

        Assert.listOnlyOne(parkingBoxDtos, "设备不存在 岗亭");
        if (!"Y".equals(parkingBoxDtos.get(0).getFee())) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",一路平安,该岗亭不收费", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, carNum, "欢迎光临", "", "", carNum + ",一路平安", carNum);
        }

        //检查是否支付完成
        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_OUT);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", "");
        if (TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDtos.get(0))) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",临时车,欢迎光临", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            if (parkingAreaTextCacheDto != null) {
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS, parkingAreaTextCacheDto,carNum);
            }
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS, carNum, "临时车辆,欢迎光临", "", "", carNum + ",临时车,欢迎光临", carNum);
        }

        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(getDefaultPaId(parkingAreaDtos));
        tempCarFeeConfigDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.size() < 1) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), "未配置临时车收费规则", "开门失败");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_PRI, "临时车无权限");
        }

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.get(0).getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos.get(0));

        //不收费，直接出场
        if (result.getPayCharge() == 0) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + "停车" + result.getHours() + "时" + result.getMin() + "分", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, carNum, "停车" + result.getHours() + "时" + result.getMin() + "分", "", "", carNum + ",临时车,欢迎光临", carNum);
        }

        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_NO_PAY);

        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, result.getHours() + "", result.getMin() + "", result.getPayCharge() + "", "");

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, result.getPayCharge(), carInoutDtos.get(0),
                carNum + "停车" + result.getHours() + "小时" + result.getMin() + "分钟,请缴费" + result.getPayCharge() + "元", "开门失败");
        sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);

        if (parkingAreaTextCacheDto != null) {
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, parkingAreaTextCacheDto,carNum);
        }
        ResultParkingAreaTextDto resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, "停车" + result.getHours() + "时" + result.getMin() + "分", "请交费" + result.getPayCharge() + "元", "", "",
                carNum + ",停车" + result.getHours() + "时" + result.getMin() + "分,请交费" + result.getPayCharge() + "元", carNum);

        resultParkingAreaTextDto.setHours(result.getHours());
        resultParkingAreaTextDto.setMin(result.getMin());
        resultParkingAreaTextDto.setPayCharge(result.getPayCharge());
        return resultParkingAreaTextDto;
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
     * @param parkingAreaDtos
     * @return -1 表示临时车
     */
    private int judgeOwnerCar(MachineDto machineDto, String carNum, List<ParkingAreaDto> parkingAreaDtos) throws Exception {

        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        CarDto carDto = new CarDto();
        carDto.setPaIds(paIds.toArray(new String[paIds.size()]));
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
                                  List<ParkingAreaDto> parkingAreaDtos,
                                  String type, List<CarInoutDto> carInoutDtos) throws Exception {
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        //1.0 判断是否为黑名单
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_WHITE);
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //白名单直接出场
        CarInoutDto carInoutDto = null;
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
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
    private ResultParkingAreaTextDto enterParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos) throws Exception {


        //1.0 判断是否为黑名单
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());

        }

        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_BLACK);
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //黑名单车辆不能进入
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "此车为黑名单车辆" + carNum + ",禁止通行", "开门失败");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK, "此车为黑名单车辆", carNum + ",禁止通行", "", "", "此车为黑名单车辆," + carNum + ",禁止通行", carNum);
        }

        //判断车辆是否为月租车
        int day = judgeOwnerCar(machineDto, carNum, parkingAreaDtos);


        //判断车辆是否在 场内
        CarInoutDto inoutDto = new CarInoutDto();
        inoutDto.setCarNum(carNum);
        inoutDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        inoutDto.setState("1");
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(inoutDto);
        // 临时车再场内 不让进 需要工作人员处理 手工出场
        if (carInoutDtos != null && carInoutDtos.size() > 0 && day < 1) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, carNum + ",车已在场", "开门失败");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_INED, carNum, "车已在场", "", "", carNum + ",车已在场", carNum);
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
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setState("1");
        ResultDto resultDto = carInoutServiceImpl.saveCarInout(carInoutDto);


        if (resultDto.getCode() != ResultDto.SUCCESS) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, resultDto.getMsg(), "开门失败");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_ERROR, carNum, "禁止入场", "", "", carNum + ",禁止入场", carNum);
        }


        ParkingAreaTextCacheDto parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_MONTH_CAR_IN);

        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", day + "");
        if (day > -1 && parkingAreaTextCacheDto != null) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "月租车," + carNum + ",欢迎光临", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS, parkingAreaTextCacheDto,carNum);
        }
        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_IN);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", "");
        if (day < 0 && parkingAreaTextCacheDto != null) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "临时车," + carNum + ",欢迎光临", "开门成功");
            sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS, parkingAreaTextCacheDto,carNum);
        }

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, carNum + ",欢迎光临", "开门成功");
        sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
        return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, carNum, "欢迎光临", "", "", carNum + ",欢迎光临", carNum);
    }

    private String getDefaultPaId(List<ParkingAreaDto> parkingAreaDtos) {
        String defaultPaId = "";
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            if ("T".equals(parkingAreaDto.getDefaultArea())) {
                defaultPaId = parkingAreaDto.getPaId();
            }

        }
        if (StringUtil.isEmpty(defaultPaId)) {
            defaultPaId = parkingAreaDtos.get(0).getPaId();
        }

        return defaultPaId;
    }

    public void sendInfo(BarrierGateControlDto barrierGateControlDto, String extBoxId, MachineDto machineDto) throws Exception {

        BarrierGateControlWebSocketServer.sendInfo(barrierGateControlDto.toString(), extBoxId);

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "小区不存在");

        barrierGateControlDto.setExtCommunityId(communityDtos.get(0).getExtCommunityId());
        barrierGateControlDto.setExtBoxId(extBoxId);
        barrierGateControlDto.setExtMachineId(machineDto.getExtMachineId());
        //上报第三方系统
        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_OPEN_PARKING_AREA_DOOR_CONTROL_LOG);

        if (appAttrDto == null) {
            return;
        }
        String value = appAttrDto.getValue();
        String upLoadAppId = "";
        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_APP_ID);

        if (appAttrDto != null) {
            upLoadAppId = appAttrDto.getValue();
        }

        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put(SystemConstant.HTTP_APP_ID, upLoadAppId);
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, value, JSONObject.toJSONString(barrierGateControlDto), headers, HttpMethod.POST, securityCode);
        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("执行结果失败" + tmpResponseEntity.getBody());
        }
    }

}
