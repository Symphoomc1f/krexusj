package com.java110.things.adapt.car.szymzh;


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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 深圳市云盟智慧科技有限公司停车场对接适配器
 * 参考文档 http://doc.szymzh.com/
 */
@Service("szymzhCarSocketProcessAdapt")
public class SzymzhCarSocketProcessAdapt extends DefaultAbstractCarProcessAdapt {

    private static Logger logger = LoggerFactory.getLogger(SzymzhCarSocketProcessAdapt.class);

    public static final String SPEC_EXT_PARKING_ID = "6185-17861";

    public static final String GET_TOKEN = "/auth/oauth/token";
    public static final String CAR_URL = "/Api/Inform/AddMthCar";
    //修改车辆
    private static final String UPDATE_CAR_URL = "/Api/Inform/ChargeMonthCar";
    private static final String DELETE_CAR_URL = "/Api/Inform/FailMonthCar";
    public static final String GET_NEED_PAY_ORDER_URL = "/Api/Inquire/GetCarNoOrderFee";
    public static final String NOTIFY_NEED_PAY_ORDER_URL = "/Api/Inform/PayNotify";

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

        String url = MappingCacheFactory.getValue("YM_CAR_URL") + CAR_URL;
        String appId = MappingCacheFactory.getValue("YM_APP_ID");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("carNo", carResultDto.getCarNum());
        postParameters.put("userNo", carResultDto.getPersonName());
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("rand", getRand());
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆添加失败" + responseEntity);
        }

        String result = responseEntity.getBody();

        logger.debug("返回内容" + result);
        try {
            logger.debug("返回内容gbk" + new String(result.getBytes(),"GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";
        if (!"1".equals(paramOut.getString("code"))) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        //延期处理
        url = MappingCacheFactory.getValue("YM_CAR_URL") + UPDATE_CAR_URL;
        postParameters = new HashMap<>();
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("carNo", carResultDto.getCarNum());
        postParameters.put("mthChargeMoney", "0");
        postParameters.put("beginTime", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("endTime", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("isUpdateBeginTime", "1");
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("rand", getRand());
        postParameters.put("sign", getSign(postParameters));
        httpHeaders = getHeader();
        httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), UPDATE_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆延期失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!"1".equals(paramOut.getString("code"))) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        return new ResultDto(paramOut.getIntValue("code") == 1 ? 0 : -1, msg, carResultDto.getCarId());
    }

    public String getRand() {

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long midSeed = (long) (timeSeed * randSeed); // mixing up the time and

        String s = midSeed + "";
        String subStr = s.substring(0, 9);

        int finalSeed = Integer.parseInt(subStr); // integer value

        return "9." + finalSeed;

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
        String url = MappingCacheFactory.getValue("YM_CAR_URL") + UPDATE_CAR_URL;
        String appId = MappingCacheFactory.getValue("YM_APP_ID");
        String msg = "成功";
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters = new HashMap<>();
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("carNo", carResultDto.getCarNum());
        postParameters.put("mthChargeMoney", "0");
        postParameters.put("beginTime", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("endTime", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("isUpdateBeginTime", "1");
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("rand", getRand());
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), UPDATE_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求车辆延期失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!"1".equals(paramOut.getString("code"))) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        return new ResultDto(paramOut.getIntValue("code") == 1 ? 0 : -1, msg, carResultDto.getCarId());
    }

    @Override
    public ResultDto deleteCar(MachineDto machineDto, CarDto carResultDto) {
        String url = MappingCacheFactory.getValue("YM_CAR_URL") + DELETE_CAR_URL;
        String appId = MappingCacheFactory.getValue("YM_APP_ID");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("carNo", carResultDto.getCarNum());
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("rand", getRand());
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
        if (!"1".equals(paramOut.getString("code"))) {
            msg = paramOut.getString("msg");
        }

        return new ResultDto(paramOut.getIntValue("code") == 1 ? 0 : -1, msg, carResultDto.getCarId());
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
        if ("Car_OutCar".equals(acceptJson.getString("method"))) {
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
        JSONObject data = acceptJson.getJSONObject("data");

        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setCarNum(data.getString("carNo"));
        carInoutDto.setCarType(data.getString("carType"));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(data.getString("gateName"));
        carInoutDto.setInoutId(data.getString("orderNo"));
        carInoutDto.setOpenTime(data.getString("outTime"));
        carInoutDto.setRemark(data.containsKey("remark") ? acceptJson.getString("remark") : "");
        carInoutDto.setPayCharge(data.getString("totalAmount"));
        carInoutDto.setRealCharge(data.getString("totalAmount"));
        carInoutDto.setPayType("1");
        carInoutDto.setMachineCode(machineDto.getMachineCode());

        try {
            carInoutService.saveCarInout(carInoutDto);
        } catch (Exception e) {
            logger.error("保存车辆进场异常", e);
        }

        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("code", "0");
        postParameters.put("msg", "成功");
        postParameters.put("rand", getRand());
        postParameters.put("sign", getSign(postParameters));
        return JSONObject.parseObject(JSONObject.toJSONString(postParameters));
    }

    /**
     * 车辆进场记录
     *
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarin(MachineDto machineDto, JSONObject acceptJson) {

        CarInoutDto carInoutDto = new CarInoutDto();

        JSONObject data = acceptJson.getJSONObject("data");

        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        carInoutDto.setCarNum(data.getString("carNo"));
        carInoutDto.setCarType(data.getString("carType"));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(data.getString("gateName"));
        carInoutDto.setInoutId(data.getString("orderNo"));
        carInoutDto.setOpenTime(data.getString("enterTime"));
        carInoutDto.setRemark(data.containsKey("remark") ? acceptJson.getString("remark") : "");
        carInoutDto.setMachineCode(machineDto.getMachineCode());

        try {
            carInoutService.saveCarInout(carInoutDto);
        } catch (Exception e) {
            logger.error("保存车辆进场异常", e);
        }

        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("code", "0");
        postParameters.put("msg", "成功");
        postParameters.put("rand", getRand());
        postParameters.put("sign", getSign(postParameters));
        return JSONObject.parseObject(JSONObject.toJSONString(postParameters));
    }

    @Override
    public TempCarPayOrderDto getNeedPayOrder(MachineDto machineDto, CarDto carDto) {
        String url = MappingCacheFactory.getValue("YM_CAR_URL") + GET_NEED_PAY_ORDER_URL;
        String appId = MappingCacheFactory.getValue("YM_APP_ID");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carDto.getExtPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("carNo", carDto.getCarNum());
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("rand", getRand());
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
        if (!"1".equals(paramOut.getString("code"))) {
            return null;
        }

        JSONObject data = paramOut.getJSONObject("data");

        if (data.getDouble("totalAmount") == 0) {
            //已经支付了
            return null;
        }

        TempCarPayOrderDto tempCarPayOrderDto = new TempCarPayOrderDto();
        tempCarPayOrderDto.setCarNum(carDto.getCarNum());
        tempCarPayOrderDto.setOrderId(data.getString("orderNo"));
        tempCarPayOrderDto.setPaId(carDto.getPaId());
        tempCarPayOrderDto.setExtPaId(carDto.getExtPaId());
        tempCarPayOrderDto.setQueryTime(DateUtil.getCurrentDate());


        tempCarPayOrderDto.setPayCharge(data.getDouble("totalAmount"));
        try {
            tempCarPayOrderDto.setInTime(DateUtil.getDateFromString(data.getString("enterTime"), DateUtil.DATE_FORMATE_STRING_A));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long min = (DateUtil.getTime() - tempCarPayOrderDto.getInTime().getTime()) / 1000 / 60;
        tempCarPayOrderDto.setStopTimeTotal(min);

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
        String appId = MappingCacheFactory.getValue("YM_APP_ID");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(tempCarPayOrderDto.getExtPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("payOrderNo", tempCarPayOrderDto.getOrderId());
        postParameters.put("payedSN", tempCarPayOrderDto.getOrderId());
        postParameters.put("payedMoney", tempCarPayOrderDto.getAmount() + "");
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("rand", getRand());
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
        if (paramOut.getIntValue("code") != 1) {
            msg = paramOut.getString("msg");
        }

        return new ResultDto(paramOut.getIntValue("code") == 1 ? 0 : -1, msg);
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

        String signKey = MappingCacheFactory.getValue("YM_SIGN_KEY");

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
        buf.append(signKey);
        String sign = DigestUtils.md5Hex(buf.toString());
        logger.debug("签名字符串={}", buf.toString());
        return sign.toUpperCase();
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("appKey", "Bearer " + getToken());
//        httpHeaders.add("sign", "Bearer " + getSign());
        httpHeaders.add("Content-Type", "application/json");
        return httpHeaders;
    }
}
