package com.java110.things.adapt.car.aiCar;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.adapt.car.ICarProcess;
import com.java110.things.entity.accessControl.CarResultDto;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.netty.NettySocketHolder;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 智能停车系统
 */
@Service("aiCarSocketProcessAdapt")
public class AiCarSocketProcessAdapt implements ICarProcess {

    private static Logger logger = LoggerFactory.getLogger(AiCarSocketProcessAdapt.class);

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private ICarService carService;

    @Override
    public void initCar() {

    }

    @Override
    public void initCar(MachineDto machineDto) {

    }


    @Override
    public int getCarNum() {
        return 0;
    }

    @Override
    public String getCar(CarResultDto carResultDto) {
        return null;
    }

    /**
     * {
     * "service": "whitelist_sync",
     * "parkid": "20180001",
     * "car_number": "粤B99999",
     * "car_type": 0,
     * "card_type": 1,
     * "startdate": "2019-05-25 00:00:01",
     * "validdate": "2019-06-24 23:59:59",
     * "cardmoney": 230.50,
     * "period": "月",
     * "carusername": "老陈",
     * "carusertel": "13822220222",
     * "drive_no": "NO111111222",
     * "address": "xxxx市xxx区xxx路112号",
     * "carlocate": "A-1-10",
     * "create_time": "2019-05-25 20:11:48",
     * "modify_time": "2019-05-25 20:11:48",
     * "operator": "李四",
     * "operate_type": 1,
     * "limitdaytype": 0,
     * "remark": ""
     * }
     *
     * @param carResultDto 用户人脸信息
     */
    @Override
    public ResultDto addCar(MachineDto machineDto, CarDto carResultDto) {
        JSONObject data = new JSONObject();
        data.put("service", "whitelist_sync");
        data.put("parkid", carResultDto.getExtPaId());
        data.put("card_id", carResultDto.getExtCarId());
        data.put("car_number", carResultDto.getCarNum());
        data.put("car_type", "0");
        data.put("startdate", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_A));
        data.put("validdate", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_A));
        data.put("cardmoney", 0.00);
        data.put("period", "月");
        data.put("card_type", "1");
        data.put("carusername", carResultDto.getPersonName());
        data.put("create_time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        data.put("modify_time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        data.put("operate_type", "1");
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(machineDto.getMachineId());
        java110CarProtocol.setContent(data.toJSONString());
        JSONObject resultJson = NettySocketHolder.sendMsgSync(java110CarProtocol, carResultDto.getCarNum());
        logger.debug("添加车辆返回：" + resultJson);
        int code = resultJson.getIntValue("result_code");
        String message = resultJson.getString("message");
        String cardId = resultJson.getString("card_id");

        return new ResultDto(code, message, cardId);

    }

    /**
     * {
     * "service": "whitelist_pay_sync",
     * "parkid": "20180001",
     * "car_number": "粤B99999",
     * "period": "月",
     * "pay_count": 1,
     * "pay_money": 230.50,
     * "pay_time": "2018-07-26 20:11:48",
     * "trade_no": "PZ000020190528172533",
     * "remark": "延期一个月"
     * }
     *
     * @param carResultDto
     */
    @Override
    public ResultDto updateCar(MachineDto machineDto, CarDto carResultDto) {
        JSONObject data = new JSONObject();
        data.put("service", "whitelist_pay_sync");
        data.put("parkid", carResultDto.getExtPaId());
        data.put("card_id", carResultDto.getCardId());
        data.put("car_number", carResultDto.getCarNum());
        data.put("period", "月");
        data.put("pay_count", (int) Math.floor(carResultDto.getCycles()));
        data.put("pay_money", 0.00);
        data.put("pay_time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        data.put("trade_no", SeqUtil.getId());
        data.put("remark", "");
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(machineDto.getMachineId());
        java110CarProtocol.setContent(data.toJSONString());
        JSONObject resultJson = NettySocketHolder.sendMsgSync(java110CarProtocol, carResultDto.getCarNum());
        logger.debug("修改车辆返回：" + resultJson);
        int code = resultJson.getIntValue("result_code");
        String message = resultJson.getString("message");

        return new ResultDto(code, message);
    }

    @Override
    public ResultDto deleteCar(MachineDto machineDto, CarDto carResultDto) {
        JSONObject data = new JSONObject();
        data.put("service", "whitelist_sync");
        data.put("parkid", carResultDto.getExtPaId());
        data.put("card_id", carResultDto.getCardId());
        data.put("car_number", carResultDto.getCarNum());
        data.put("operate_type", "3");
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(machineDto.getMachineId());
        java110CarProtocol.setContent(data.toJSONString());
        JSONObject resultJson = NettySocketHolder.sendMsgSync(java110CarProtocol, carResultDto.getCarNum());
        logger.debug("修改车辆返回：" + resultJson);
        int code = resultJson.getIntValue("result_code");
        String message = resultJson.getString("message");

        return new ResultDto(code, message);
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

    @Override
    public Java110CarProtocol accept(MachineDto machineDto, String content) {
        JSONObject acceptJson = JSONObject.parseObject(content);

        String parkId = acceptJson.getString("parkid");
        String service = acceptJson.getString("service");

        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        JSONObject resObj = null;
        switch (service) {
            case "checkKey":
                resObj = checkKey();
                break;
            case "heartbeat":
                resObj = heartbeat(machineDto);
                break;
            case "uploadcarin":
                resObj = uploadcarin(machineDto, acceptJson);
                break;
            case "uploadcarout":
                resObj = uploadcarout(machineDto, acceptJson);
                break;
//            case "whitelist_sync":
//                resObj = whitelistSync(machineDto, acceptJson);
//                break;

            default:
                resObj = new JSONObject();
                resObj.put("service", service);
                resObj.put("result_code", -1);
                resObj.put("message", "当前不支持");
                break;
        }
        java110CarProtocol.setContent(resObj.toJSONString());
        java110CarProtocol.setId(machineDto.getMachineId());
        return java110CarProtocol;
    }

    /***
     * {"service":"whitelist_sync","car_number":"贵119","result_code":0,"message":"注册固定车成功","card_id":"201106181136297"}
     * @param acceptJson
     * @return
     */
    private JSONObject whitelistSync(MachineDto machineDto, JSONObject acceptJson) {
        System.out.printf("acceptJson" + acceptJson.toJSONString());

        int code = acceptJson.getIntValue("result_code");

        if (code != 0) {
            JSONObject resObj = new JSONObject();
            resObj.put("service", "whitelist_sync");
            resObj.put("result_code", code);
            resObj.put("message", "失败");
            return resObj;
        }

        CarDto carDto = new CarDto();
        carDto.setCarNum(acceptJson.getString("car_number"));
        carDto.setCardId(acceptJson.getString("card_id"));

        try {
            carService.updateCarByMachine(carDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject resObj = new JSONObject();
        resObj.put("service", "whitelist_sync");
        resObj.put("result_code", 0);
        resObj.put("message", "成功");
        return resObj;
    }

    private JSONObject uploadcarout(MachineDto machineDto, JSONObject acceptJson) {
        CarInoutDto carInoutDto = new CarInoutDto();

        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setCarNum(acceptJson.getString("car_number"));
        carInoutDto.setCarType(acceptJson.getString("car_type"));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(acceptJson.getString("gateoutname"));
        carInoutDto.setInoutId(acceptJson.getString("order_id"));
        carInoutDto.setOpenTime(acceptJson.getString("out_time"));
        carInoutDto.setRemark(acceptJson.containsKey("remark") ? acceptJson.getString("remark") : "");
        carInoutDto.setPayCharge(acceptJson.getString("paycharge"));
        carInoutDto.setRealCharge(acceptJson.getString("realcharge"));
        carInoutDto.setPayType(acceptJson.getString("pay_type"));
        carInoutDto.setMachineCode(machineDto.getMachineCode());

        try {
            carInoutService.saveCarInout(carInoutDto);
        } catch (Exception e) {
            logger.error("保存车辆进场异常", e);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", "uploadcarout");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "认证成功");
        jsonObject.put("order_id", acceptJson.getString("order_id"));
        return jsonObject;
    }

    /**
     * 车辆进场记录
     *
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarin(MachineDto machineDto, JSONObject acceptJson) {

        CarInoutDto carInoutDto = new CarInoutDto();

        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        carInoutDto.setCarNum(acceptJson.getString("car_number"));
        carInoutDto.setCarType(acceptJson.getString("car_type"));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(acceptJson.getString("gateinname"));
        carInoutDto.setInoutId(acceptJson.getString("order_id"));
        carInoutDto.setOpenTime(acceptJson.getString("in_time"));
        carInoutDto.setRemark(acceptJson.containsKey("remark") ? acceptJson.getString("remark") : "");
        carInoutDto.setMachineCode(machineDto.getMachineCode());

        try {
            carInoutService.saveCarInout(carInoutDto);
        } catch (Exception e) {
            logger.error("保存车辆进场异常", e);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", "uploadcarin");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "认证成功");
        jsonObject.put("order_id", acceptJson.getString("order_id"));
        return jsonObject;
    }

    @Override
    public String getNeedPayOrder() {
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId("20180001L");
        java110CarProtocol.setContent("{\n" +
                "    \"service\": \"query_price\",\n" +
                "    \"parkid\": \"20180001\",\n" +
                "    \"car_number\": \"浙CBB123\",\n" +
                "    \"pay_scene\": 0\n" +
                "  }");
        JSONObject data = NettySocketHolder.sendMsgSync(java110CarProtocol, "浙CBB123");
        return data.toJSONString();
    }

    @Override
    public ResultDto addCarBlackWhite(MachineDto tmpMachineDto, CarBlackWhiteDto carBlackWhiteDto) {
        return null;
    }

    @Override
    public ResultDto deleteCarBlackWhite(MachineDto tmpMachineDto, CarBlackWhiteDto carBlackWhiteDto) {
        return null;
    }

    /**
     * 认证接口
     */
    private JSONObject checkKey() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", "checkKey");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "认证成功");
        return jsonObject;
    }

    /**
     * 心跳接口
     */
    private JSONObject heartbeat(MachineDto machineDto) {
        JSONObject jsonObject = new JSONObject();
        try {
            //设备ID
            //String machineCode = info.getString("deviceKey");
            String heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
            MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineDto.getMachineCode(), heartBeatTime);
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        } catch (Exception e) {
            logger.error("心跳失败", e);
        }

        jsonObject.put("service", "heartbeat");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "在线");
        return jsonObject;
    }
}
