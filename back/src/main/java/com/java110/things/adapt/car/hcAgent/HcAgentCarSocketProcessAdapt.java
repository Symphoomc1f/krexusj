package com.java110.things.adapt.car.hcAgent;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.adapt.car.DefaultAbstractCarProcessAdapt;
import com.java110.things.entity.accessControl.CarResultDto;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.fee.TempCarPayOrderDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.LocalCacheFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.Assert;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * http://www.bisen-iot.com/
 * 百胜云停车系统对接适配器
 */
@Service("hcAgentCarSocketProcessAdapt")
public class HcAgentCarSocketProcessAdapt extends DefaultAbstractCarProcessAdapt {

    private static Logger logger = LoggerFactory.getLogger(HcAgentCarSocketProcessAdapt.class);

    public static final String SPEC_EXT_PARKING_ID = "6185-17861";
    //postParameters.put("carTypeId","5720194218816441884");
    //postParameters.put("carModelID","4692197192354628555");
    public static final String SPEC_EXT_VID_ID = "7185-17861";
    public static final String SPEC_EXT_CAR_TYPE_ID = "8185-17861";
    public static final String SPEC_EXT_CAR_MODEL_ID = "9185-17861";



    public static final String GET_TOKEN = "/auth/oauth/token";
    public static final String CAR_URL = "/api/park/freecar";
    public static final String GET_NEED_PAY_ORDER_URL = "/api/pay/car";
    public static final String NOTIFY_NEED_PAY_ORDER_URL = "/api/pay/park";

    //单设备处理
    public static final String TOPIC_REQUEST = "mqtt/java110/parking";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private IParkingAreaService parkingAreaService;

    @Autowired
    private ICarService carService;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Override
    public String getCar(CarResultDto carResultDto) {
        return null;
    }

    public String getParkingId(ParkingAreaDto parkingAreaDto) {
        List<ParkingAreaAttrDto> parkingAreaAttrDtos = parkingAreaDto.getAttrs();

        if (parkingAreaAttrDtos == null || parkingAreaAttrDtos.size() < 1) {
            return "";
        }

        for (ParkingAreaAttrDto parkingAreaAttrDto : parkingAreaAttrDtos) {
            if (SPEC_EXT_PARKING_ID.equals(parkingAreaAttrDto.getSpecCd())) {
                return parkingAreaAttrDto.getValue();
            }
        }

        return "";
    }

    public String getVId(ParkingAreaDto parkingAreaDto,String ext) {
        List<ParkingAreaAttrDto> parkingAreaAttrDtos = parkingAreaDto.getAttrs();

        if (parkingAreaAttrDtos == null || parkingAreaAttrDtos.size() < 1) {
            return "";
        }

        for (ParkingAreaAttrDto parkingAreaAttrDto : parkingAreaAttrDtos) {
            if (ext.equals(parkingAreaAttrDto.getSpecCd())) {
                return parkingAreaAttrDto.getValue();
            }
        }

        return "";
    }

    /**
     * @param carResultDto 用户人脸信息
     */
    @Override
    public ResultDto addCar(MachineDto machineDto, CarDto carResultDto) {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = null;

        communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "添加车辆时未查到小区信息");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        JSONObject postParameters = new JSONObject();
        postParameters.put("adaptName", "smartServiceAdapt");
        postParameters.put("action", "ADD_CAR");
        postParameters.put("parkingId", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("personId", carResultDto.getPersonId());
        postParameters.put("personName", carResultDto.getPersonName());
        postParameters.put("personTel", carResultDto.getPersonTel());
        postParameters.put("carNum", carResultDto.getCarNum());
        postParameters.put("carId", carResultDto.getCarId());
        postParameters.put("startTime", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("endTime", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("communityId",getVId(parkingAreaDtos.get(0),SPEC_EXT_VID_ID));
        postParameters.put("carTypeId",getVId(parkingAreaDtos.get(0),SPEC_EXT_CAR_TYPE_ID));
        postParameters.put("carModelID",getVId(parkingAreaDtos.get(0),SPEC_EXT_CAR_MODEL_ID));
        MqttFactory.publish(TOPIC_REQUEST,postParameters.toJSONString());
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG,carResultDto.getCarId());
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
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        JSONObject postParameters = new JSONObject();
        postParameters.put("adaptName", "smartServiceAdapt");
        postParameters.put("action", "EDIT_CAR");
        postParameters.put("parkingId", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("personId", carResultDto.getPersonId());
        postParameters.put("personName", carResultDto.getPersonName());
        postParameters.put("personTel", carResultDto.getPersonTel());
        postParameters.put("carNum", carResultDto.getCarNum());
        postParameters.put("carId", carResultDto.getCarId());
        postParameters.put("startTime", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("endTime", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_A));
        MqttFactory.publish(TOPIC_REQUEST,postParameters.toJSONString());
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG,carResultDto.getCarId());
    }

    @Override
    public ResultDto deleteCar(MachineDto machineDto, CarDto carResultDto) {
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        JSONObject postParameters = new JSONObject();
        postParameters.put("adaptName", "smartServiceAdapt");
        postParameters.put("action", "DELETE_CAR");
        postParameters.put("parkingId", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("carNum", carResultDto.getCarNum());
        postParameters.put("carId", carResultDto.getCarId());
        MqttFactory.publish(TOPIC_REQUEST,postParameters.toJSONString());
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG,carResultDto.getCarId());
    }


    /**
     * 开门记录上报
     *
     * @param machineDto
     * @param content
     * @return
     */
    @Override
    public Java110CarProtocol accept(MachineDto machineDto, String content) {

        JSONObject acceptJson = JSONObject.parseObject(content);
        JSONObject outJson = null;
        if ("2".equals(acceptJson.getString("passType"))) {
            outJson = uploadcarout(machineDto, acceptJson);
        } else {
            outJson = uploadcarin(machineDto, acceptJson);
        }

        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setContent(outJson.toJSONString());
        java110CarProtocol.setId(SeqUtil.getId());
        return java110CarProtocol;
    }

    /**
     * 出场上报
     *
     * @param machineDto
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarout(MachineDto machineDto, JSONObject acceptJson) {
        CarInoutDto carInoutDto = new CarInoutDto();

        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setCarNum(acceptJson.getString("plateNum"));
        carInoutDto.setCarType(acceptJson.getString("carType"));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(acceptJson.getString("parkName"));
        carInoutDto.setInoutId(acceptJson.getString("recordId"));
        carInoutDto.setOpenTime(acceptJson.getString("outTime"));
        carInoutDto.setRemark(acceptJson.containsKey("remark") ? acceptJson.getString("remark") : "");
        carInoutDto.setPayCharge(acceptJson.getString("charge"));
        carInoutDto.setRealCharge(acceptJson.getString("charge"));
        carInoutDto.setPayType("1");
        carInoutDto.setMachineCode(machineDto.getMachineCode());

        try {
            carInoutService.saveCarInout(carInoutDto);
        } catch (Exception e) {
            logger.error("保存车辆进场异常", e);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
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
        carInoutDto.setCarNum(acceptJson.getString("plateNum"));
        carInoutDto.setCarType(acceptJson.getString("carType"));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(acceptJson.getString("parkName"));
        carInoutDto.setInoutId(acceptJson.getString("recordId"));
        carInoutDto.setOpenTime(acceptJson.getString("inTime"));
        carInoutDto.setRemark(acceptJson.containsKey("remark") ? acceptJson.getString("remark") : "");
        carInoutDto.setMachineCode(machineDto.getMachineCode());

        try {
            carInoutService.saveCarInout(carInoutDto);
        } catch (Exception e) {
            logger.error("保存车辆进场异常", e);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }

    @Override
    public TempCarPayOrderDto getNeedPayOrder(MachineDto machineDto, CarDto carDto) {
        String url = MappingCacheFactory.getValue("BISEN_CAR_URL") + GET_NEED_PAY_ORDER_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carDto.getExtPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        JSONObject postParameters = new JSONObject();
        postParameters.put("parkingId", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plateNum", carDto.getCarNum());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(postParameters.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), GET_NEED_PAY_ORDER_URL, postParameters.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆查询待支付订单失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";
        if (paramOut.getIntValue("code") != 0) {
            return null;
        }

        JSONObject data = paramOut.getJSONObject("data");

        TempCarPayOrderDto tempCarPayOrderDto = new TempCarPayOrderDto();
        tempCarPayOrderDto.setCarNum(data.getString("plateNum"));
        tempCarPayOrderDto.setOrderId(data.getString("orderId"));
        tempCarPayOrderDto.setPaId(carDto.getPaId());
        tempCarPayOrderDto.setExtPaId(carDto.getExtPaId());
        try {
            tempCarPayOrderDto.setQueryTime(DateUtil.getDateFromString(data.getString("queryTime"), DateUtil.DATE_FORMATE_STRING_A));
        } catch (ParseException e) {
            logger.error("查询日期转换出错", e);
        }
        tempCarPayOrderDto.setStopTimeTotal(data.getDouble("stopTimeTotal"));
        tempCarPayOrderDto.setPayCharge(data.getDouble("payCharge"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, new Double(tempCarPayOrderDto.getStopTimeTotal()).intValue() * -1);
        tempCarPayOrderDto.setInTime(calendar.getTime());


        return tempCarPayOrderDto;
    }

    /**
     * 支付通知接口
     *
     * @param machineDto
     * @param tempCarPayOrderDto
     * @return
     */
    @Override
    public ResultDto notifyTempCarFeeOrder(MachineDto machineDto, TempCarPayOrderDto tempCarPayOrderDto) {
        String url = MappingCacheFactory.getValue("BISEN_CAR_URL") + NOTIFY_NEED_PAY_ORDER_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(tempCarPayOrderDto.getExtPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        JSONObject postParameters = new JSONObject();
        postParameters.put("parkingId", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plateNum", tempCarPayOrderDto.getCarNum());
        postParameters.put("orderId", tempCarPayOrderDto.getOrderId());
        postParameters.put("amount", tempCarPayOrderDto.getAmount());
        postParameters.put("payTime", tempCarPayOrderDto.getPayTime());
        postParameters.put("payType", tempCarPayOrderDto.getPayType());
        postParameters.put("timestamp", DateUtil.getCurrentDate().getTime());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(postParameters.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), GET_NEED_PAY_ORDER_URL, postParameters.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆查询待支付订单失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        String msg = "成功";
        if (paramOut.getIntValue("code") != 0) {
            msg = paramOut.getJSONObject("data").getString("message");
        }

        if (paramOut.getJSONObject("data").getIntValue("status") != 0) {
            paramOut.put("code", paramOut.getJSONObject("data").getIntValue("status"));
            msg = paramOut.getJSONObject("data").getString("message");
        }

        return new ResultDto(paramOut.getIntValue("code"), msg);
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

    /**
     * 获取accessToken
     *
     * @return
     */
    private String getToken() {

        String token = LocalCacheFactory.getValue("bisen_car_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String url = MappingCacheFactory.getValue("BISEN_CAR_URL") + GET_TOKEN;
        String clientId = MappingCacheFactory.getValue("client_id");
        String clientSecret = MappingCacheFactory.getValue("client_secret");

        url += ("randomStr=" + SeqUtil.getId() + "&scope=server&grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret);

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求百胜获取token失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (paramOut.containsKey("access_token")) {
            throw new IllegalStateException(paramOut.getString("error_description"));
        }

        token = paramOut.getString("access_token");
        LocalCacheFactory.setValue("bisen_token", token, paramOut.getIntValue("expires_in") - 200);
        return token;
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + getToken());
        httpHeaders.add("Content-Type", "application/json");
        return httpHeaders;
    }
}
