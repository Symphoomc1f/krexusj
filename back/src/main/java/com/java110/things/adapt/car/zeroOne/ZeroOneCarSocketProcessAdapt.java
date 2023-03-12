package com.java110.things.adapt.car.zeroOne;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.adapt.car.DefaultAbstractCarProcessAdapt;
import com.java110.things.entity.accessControl.CarResultDto;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.fee.TempCarPayOrderDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 零壹公司停车场对接适配器  一代、二代车场云平台标准API接口文档
 */
@Service("zeroOneCarSocketProcessAdapt")
public class ZeroOneCarSocketProcessAdapt extends DefaultAbstractCarProcessAdapt {

    private static Logger logger = LoggerFactory.getLogger(ZeroOneCarSocketProcessAdapt.class);

    public static final String SPEC_EXT_PARKING_ID = "6185-17861";

    public static final String GET_TOKEN = "/auth/oauth/token";
    public static final String CAR_URL = "/yihao01-park-opendata/outapi/monthcarmanage/addMonthCar";
    //修改车辆
    private static final String UPDATE_CAR_URL = "/yihao01-park-opendata/outapi/monthcarmanage/diyMonthCarPayment";
    private static final String DELETE_CAR_URL = "/yihao01-park-opendata/outapi/monthcarmanage/cancelMonthCar";
    public static final String GET_NEED_PAY_ORDER_URL = "/yihao01-park-opendata/outapi/tempcar/getChargeInfo";
    public static final String NOTIFY_NEED_PAY_ORDER_URL = "/yihao01-park-opendata/outapi/tempcar/payCallBack";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private IParkingAreaService parkingAreaService;

    @Autowired
    private ICarService carService;

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

    /**
     * @param carResultDto 用户人脸信息
     */
    @Override
    public ResultDto addCar(MachineDto machineDto, CarDto carResultDto) {

        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + CAR_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plate", carResultDto.getCarNum());
        postParameters.put("endDateString", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("chargeNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("telephone", carResultDto.getPersonTel());
        postParameters.put("userName", carResultDto.getPersonName());
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆添加失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";
        if (!"200".equals(paramOut.getString("code"))) {
            msg = paramOut.getString("desc");
        }

        return new ResultDto(paramOut.getIntValue("code"), msg, carResultDto.getCarId());

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
        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + UPDATE_CAR_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plate", carResultDto.getCarNum());
        postParameters.put("newValidEndDate", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("newValidStartDate", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("depositMoney", "0");
        postParameters.put("type", "1");
        postParameters.put("operationId", "1");
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), UPDATE_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆添加失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";
        if (!"200".equals(paramOut.getString("code"))) {
            msg = paramOut.getString("desc");
        }

        return new ResultDto(paramOut.getIntValue("code"), msg, carResultDto.getCarId());
    }

    @Override
    public ResultDto deleteCar(MachineDto machineDto, CarDto carResultDto) {
        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + DELETE_CAR_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plate", carResultDto.getCarNum());
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), DELETE_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆添加失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";
        if (!"200".equals(paramOut.getString("code"))) {
            msg = paramOut.getString("desc");
        }

        return new ResultDto(paramOut.getIntValue("code"), msg, carResultDto.getCarId());
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
        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + GET_NEED_PAY_ORDER_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carDto.getExtPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plate", carDto.getCarNum());
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), GET_NEED_PAY_ORDER_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆查询待支付订单失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";
        if (!"200".equals(paramOut.getString("code"))) {
            return null;
        }

        JSONObject data = paramOut.getJSONObject("data");

        if ("2".equals(data.getString("orderStatus"))) {
            //已经支付了
            return null;
        }

        TempCarPayOrderDto tempCarPayOrderDto = new TempCarPayOrderDto();
        tempCarPayOrderDto.setCarNum(data.getString("plate"));
        tempCarPayOrderDto.setOrderId(data.getString("serialNumber"));
        tempCarPayOrderDto.setPaId(carDto.getPaId());
        tempCarPayOrderDto.setExtPaId(carDto.getExtPaId());
        tempCarPayOrderDto.setQueryTime(DateUtil.getCurrentDate());

        tempCarPayOrderDto.setStopTimeTotal(data.getDouble("stayMins"));
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
        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + NOTIFY_NEED_PAY_ORDER_URL;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(tempCarPayOrderDto.getExtPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("plate", tempCarPayOrderDto.getCarNum());
        postParameters.put("serialNumber", tempCarPayOrderDto.getOrderId());
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), NOTIFY_NEED_PAY_ORDER_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

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
    private String getSign(Map<String, String> reqParamMap) {

        String signKey = MappingCacheFactory.getValue("ZERO_ONE_SIGN_KEY");

        Map<String, String> paramsMap = reqParamMap instanceof TreeMap ? reqParamMap : new TreeMap<>(reqParamMap);
        StringBuilder buf = new StringBuilder(paramsMap.size() * 20);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            if ("sign".equals(key)) {
                continue; // 不在此处添加签名，sign参数必须放最后
            }
            try {
                key = URLEncoder.encode(key, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            buf.append(key).append('=').append(entry.getValue()).append('&');
        }
        buf.append("key=").append(signKey);
        String sign = DigestUtils.md5Hex(buf.toString());
        logger.debug("签名字符串={}", buf.toString());
        return sign;
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("appKey", "Bearer " + getToken());
//        httpHeaders.add("sign", "Bearer " + getSign());
        httpHeaders.add("Content-Type", "application/json");
        return httpHeaders;
    }
}
