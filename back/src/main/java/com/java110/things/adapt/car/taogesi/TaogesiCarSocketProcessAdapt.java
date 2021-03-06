package com.java110.things.adapt.car.taogesi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.adapt.car.DefaultAbstractCarProcessAdapt;
import com.java110.things.adapt.car.zeroOne.ZeroOneCarSocketProcessAdapt;
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
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * ??????????????????
 * ???????????? http://doc.yunucms.com/s/88072478/JIwJRJoQ/X7nAeoaQ
 */
@Service("taogesiCarSocketProcessAdapt")
public class TaogesiCarSocketProcessAdapt extends DefaultAbstractCarProcessAdapt {
    private static Logger logger = LoggerFactory.getLogger(ZeroOneCarSocketProcessAdapt.class);

    /**
     * ??????????????????
     */
    public static final String SPEC_EXT_COMMUNITY_ID = "1152000286";

    /**
     * ??????????????????
     */
    public static final String SPEC_EXT_PARKING_ID = "6185-17861";

    /**
     * ??????????????????
     */
    public static final String SPEC_EXT_GROUP_ID = "9477000598";

    /**
     * ??????????????????
     */
    public static final String SPEC_EXT_FEE_ID = "3490000403";

    /**
     * ??????????????????
     */
    public static final String SPEC_EXT_PACKAGE_ID = "5928000406";

    /**
     * ??????????????????
     */
    public static final String SPEC_EXT_DEFAULT_PWD = "0277000409";

    public static final String GET_TOKEN = "/park/sys/login";
    public static final String QUERY_CAR_PERSON = "/park/zyb/zybVehicleOwner/queryById";
    public static final String ADD_CAR_PERSON = "/park/zyb/zybVehicleOwner/add";
    public static final String UPDATE_CAR_PERSON = "/park/zyb/zybVehicleOwner/edit";
    public static final String DELETE_CAR_PERSON = "/park/zyb/zybVehicleOwner/delete";
    public static final String QUERY_CAR_URL = "/park/zyb/zybVehicle/queryById";
    public static final String ADD_CAR_URL = "/park/zyb/zybVehicle/add";
    public static final String UPDATE_CAR_URL = "/park/zyb/zybVehicle/edit";
    public static final String CHARGE_CAR_URL = "/park/zyb/zybVehicle/customRecharge";
    public static final String DELETE_CAR_URL = "/park/zyb/zybVehicle/delete";
    public static final String GET_NEED_PAY_ORDER_URL = "/Api/Inquire/GetCarNoOrderFee";
    public static final String NOTIFY_NEED_PAY_ORDER_URL = "/Api/Inform/PayNotify";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private IParkingAreaService parkingAreaService;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private ICarService carService;

    @Override
    public String getCar(CarResultDto carResultDto) {
        return null;
    }

    public String getParkingId(ParkingAreaDto parkingAreaDto, String specCd) {
        List<ParkingAreaAttrDto> parkingAreaAttrDtos = parkingAreaDto.getAttrs();

        if (parkingAreaAttrDtos == null || parkingAreaAttrDtos.size() < 1) {
            return "";
        }

        for (ParkingAreaAttrDto parkingAreaAttrDto : parkingAreaAttrDtos) {
            if (specCd.equals(parkingAreaAttrDto.getSpecCd())) {
                return parkingAreaAttrDto.getValue();
            }
        }

        return "";
    }

    /**
     * @param carResultDto ???????????????
     */
    @Override
    public ResultDto synBookingCar(MachineDto machineDto, CarDto carResultDto) {
        return new ResultDto(0 , "sucess");
    }

    /**
     * ????????????????????????
     *
     * @param personId
     * @return
     */
    private JSONObject getTaogesiPerson(String personId) {
        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + QUERY_CAR_PERSON;
        url += ("?id=" + personId);
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }
        String result = responseEntity.getBody();
        logger.debug("????????????" + result);
        JSONObject paramOut = JSONObject.parseObject(result);
        String msg = "??????";
        if (!"200".equals(paramOut.getString("code"))) {
            return null;
        }

        return paramOut.getJSONObject("result");
    }

    /**
     * ????????????
     *
     * @param machineDto
     * @param carResultDto
     */
    private void addTaogesiPerson(MachineDto machineDto, CarDto carResultDto) {
        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + ADD_CAR_PERSON;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, Object> postParameters = new HashMap<>();
        postParameters.put("id", carResultDto.getPersonId());
        postParameters.put("depId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_COMMUNITY_ID));
        postParameters.put("groupId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_GROUP_ID));
        postParameters.put("idcard", carResultDto.getCarId());
        postParameters.put("memo", "??????????????????");
        postParameters.put("money", "0.00");
        postParameters.put("name", carResultDto.getPersonName());
        postParameters.put("normalEnterStatus", "1");
        postParameters.put("parkingNum", carResultDto.getParkingNum());
        postParameters.put("password", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_DEFAULT_PWD));
        postParameters.put("phone", carResultDto.getPersonTel());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), ADD_CAR_PERSON, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }
        String result = responseEntity.getBody();
        logger.debug("????????????" + result);
        JSONObject paramOut = JSONObject.parseObject(result);
        String msg = "??????";
        if (!"200".equals(paramOut.getString("code")) || !paramOut.getBoolean("success")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }
    }

    /**
     * ????????????
     *
     * @param machineDto
     * @param carResultDto
     */
    private void updateTaogesiPerson(MachineDto machineDto, CarDto carResultDto) {
        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + UPDATE_CAR_PERSON;

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, Object> postParameters = new HashMap<>();
        postParameters.put("id", carResultDto.getPersonId());
        postParameters.put("depId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_COMMUNITY_ID));
        postParameters.put("groupId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_GROUP_ID));
        postParameters.put("idcard", carResultDto.getCarId());
        postParameters.put("memo", "??????????????????");
        postParameters.put("money", "0");
        postParameters.put("name", carResultDto.getPersonName());
        postParameters.put("normalEnterStatus", "1");
        postParameters.put("parkingNum", carResultDto.getParkingNum());
        postParameters.put("password", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_DEFAULT_PWD));
        postParameters.put("phone", carResultDto.getPersonTel());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), UPDATE_CAR_PERSON, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }
        String result = responseEntity.getBody();
        logger.debug("????????????" + result);
        JSONObject paramOut = JSONObject.parseObject(result);

        if (!"200".equals(paramOut.getString("code")) || !paramOut.getBoolean("success")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }
    }

    /**
     * ????????????????????????
     *
     * @param machineDto   ????????????
     * @param carResultDto ????????????
     * @return ??????????????????
     */
    @Override
    public ResultDto addCar(MachineDto machineDto, CarDto carResultDto) {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = null;

        communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "????????????????????????????????????");

        // TODO: 2021/6/1
        // 1.??????carResultDto.getPersonId()?????????????????????????????????QUERY_CAR_PERSON
        JSONObject person = getTaogesiPerson(carResultDto.getPersonId());

        // 2.?????????????????????????????????????????????ADD_CAR_PERSON???parkingNum??????????????????????????????
        if (person == null) {
            addTaogesiPerson(machineDto, carResultDto);
        } else { // 3.??????????????????????????????????????????UPDATE_CAR_PERSON???parkingNum??????????????????????????????
            updateTaogesiPerson(machineDto, carResultDto);
        }

        // TODO: 2021/6/1
        // 1.??????carResultDto.getCarId()?????????????????????????????????QUERY_CAR_URL
        // 2.?????????????????????????????????????????????ADD_CAR_URL
        // 3.?????????????????????????????????????????????UPDATE_CAR_URL?????????????????????????????????????????????CHARGE_CAR_URL
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + ADD_CAR_URL;
        Map<String, Object> postParameters = new HashMap<>();
        postParameters.put("depId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_COMMUNITY_ID));
        postParameters.put("chargeType", "1");
        postParameters.put("nickname", carResultDto.getPersonName());
        postParameters.put("startTime", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("endTime", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("feesId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_FEE_ID));
        postParameters.put("isOpen", "1");
        postParameters.put("licensePlate", carResultDto.getCarNum());
        postParameters.put("ownerId", carResultDto.getPersonId());
        postParameters.put("parkId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_PARKING_ID));
        postParameters.put("status", "1");
        JSONArray packageList = new JSONArray();
        Arrays.stream(Objects.requireNonNull(StringUtils.split(getParkingId(parkingAreaDtos.get(0), SPEC_EXT_PACKAGE_ID), ",")))
                .map(str -> {
                    JSONObject item = new JSONObject();
                    item.put("id", str);
                    return item;
                }).forEach(packageList::add);
        postParameters.put("packageList", packageList);
        postParameters.put("id", carResultDto.getCarId());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), ADD_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!"200".equals(paramOut.getString("code")) || !paramOut.getBoolean("success")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : -1, "??????", carResultDto.getCarId());
    }


    @Override
    public ResultDto updateCar(MachineDto machineDto, CarDto carResultDto) {
        // TODO: 2021/6/1

        // 1.??????carResultDto.getPersonId()?????????????????????????????????QUERY_CAR_PERSON
        JSONObject person = getTaogesiPerson(carResultDto.getPersonId());

        // 2.?????????????????????????????????????????????ADD_CAR_PERSON???parkingNum??????????????????????????????
        if (person == null) {
            addTaogesiPerson(machineDto, carResultDto);
        } else { // 3.??????????????????????????????????????????UPDATE_CAR_PERSON???parkingNum??????????????????????????????
            updateTaogesiPerson(machineDto, carResultDto);
        }

        // TODO: 2021/6/1
        // 1.??????carResultDto.getCarId()?????????????????????????????????QUERY_CAR_URL
        // 2.?????????????????????????????????????????????ADD_CAR_URL
        // 3.?????????????????????????????????????????????UPDATE_CAR_PERSON?????????????????????????????????????????????CHARGE_CAR_URL
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(carResultDto.getPaId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);

        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + UPDATE_CAR_URL;
        String msg = "??????";

        Map<String, Object> postParameters = new HashMap<>();
        postParameters = new HashMap<>();
        postParameters.put("depId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_COMMUNITY_ID));
        postParameters.put("chargeType", "1");
        postParameters.put("nickname", carResultDto.getPersonName());
        postParameters.put("startTime", DateUtil.getFormatTimeString(carResultDto.getStartTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("endTime", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_B));
        postParameters.put("feesId", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_FEE_ID));
        postParameters.put("isOpen", "1");
        postParameters.put("licensePlate", carResultDto.getCarNum());
        postParameters.put("status", "1");
        JSONArray packageList = new JSONArray();
        Arrays.stream(Objects.requireNonNull(StringUtils.split(getParkingId(parkingAreaDtos.get(0), SPEC_EXT_PACKAGE_ID), ",")))
                .map(str -> {
                    JSONObject item = new JSONObject();
                    item.put("id", str);
                    return item;
                }).forEach(packageList::add);
        postParameters.put("packageList", packageList);
        postParameters.put("id", carResultDto.getCarId());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), UPDATE_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!"200".equals(paramOut.getString("code")) || !paramOut.getBoolean("success")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        //????????????
        url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + CHARGE_CAR_URL;

        postParameters = new HashMap<>();
        postParameters.put("veId", carResultDto.getCarId());
        postParameters.put("rechargeMoney", 0);
        postParameters.put("memo", "??????????????????");
        postParameters.put("endDateStr", DateUtil.getFormatTimeString(carResultDto.getEndTime(), DateUtil.DATE_FORMATE_STRING_B));
        httpHeaders = getHeader();
        httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CHARGE_CAR_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!"200".equals(paramOut.getString("code")) || !paramOut.getBoolean("success")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : -1, msg, carResultDto.getCarId());
    }

    @Override
    public ResultDto deleteCar(MachineDto machineDto, CarDto carResultDto) {
        // TODO: 2021/6/1
        // 1.??????carResultDto.getCarId()?????????????????????????????????QUERY_CAR_PERSON
        // 2.??????????????????????????????????????????DELETE_CAR_PERSON

        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + DELETE_CAR_URL;
        url += ("?id=" + carResultDto.getCarId());

        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), DELETE_CAR_URL, url, responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("????????????????????????" + responseEntity);
        }

        String msg = "??????";
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!"200".equals(paramOut.getString("code")) || !paramOut.getBoolean("success")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : -1, msg, carResultDto.getCarId());
    }


    /**
     * ??????????????????
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
     * ????????????
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
            logger.error("????????????????????????", e);
        }

        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("code", "0");
        postParameters.put("msg", "??????");
        postParameters.put("sign", getSign(postParameters));
        return JSONObject.parseObject(JSONObject.toJSONString(postParameters));
    }

    /**
     * ??????????????????
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
            logger.error("????????????????????????", e);
        }

        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("code", "0");
        postParameters.put("msg", "??????");
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
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_PARKING_ID));
        postParameters.put("carNo", carDto.getCarNum());
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), GET_NEED_PAY_ORDER_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("???????????????????????????????????????" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "??????";
        if (!"1".equals(paramOut.getString("code"))) {
            return null;
        }

        JSONObject data = paramOut.getJSONObject("data");

        if (data.getDouble("totalAmount") == 0) {
            //???????????????
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
     * ??????????????????
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
        postParameters.put("parkKey", getParkingId(parkingAreaDtos.get(0), SPEC_EXT_PARKING_ID));
        postParameters.put("payOrderNo", tempCarPayOrderDto.getOrderId());
        postParameters.put("payedSN", tempCarPayOrderDto.getOrderId());
        postParameters.put("payedMoney", tempCarPayOrderDto.getAmount() + "");
        postParameters.put("version", "v1.0");
        postParameters.put("appid", appId);
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), NOTIFY_NEED_PAY_ORDER_URL, JSONObject.toJSONString(postParameters), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("???????????????????????????????????????" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        String msg = "??????";
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
     * ????????????
     */
    private JSONObject heartbeat(MachineDto machineDto) {
        JSONObject jsonObject = new JSONObject();
        try {
            //??????ID
            //String machineCode = info.getString("deviceKey");
            String heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
            MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineDto.getMachineCode(), heartBeatTime);
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        } catch (Exception e) {
            logger.error("????????????", e);
        }

        jsonObject.put("service", "heartbeat");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "??????");
        return jsonObject;
    }

    /**
     * ??????accessToken
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
                continue; // ???????????????????????????sign?????????????????????
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
        logger.debug("???????????????={}", buf.toString());
        return sign.toUpperCase();
    }

    /**
     * ??????accessToken
     *
     * @return
     */
    private String getToken() {

        String token = LocalCacheFactory.getValue("TAOGESI_CAR_TOKEN");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String url = MappingCacheFactory.getValue("TAOGESI_CAR_URL") + GET_TOKEN;
        String clientId = MappingCacheFactory.getValue("TAOGESI_USERNAME");
        String clientSecret = MappingCacheFactory.getValue("TAOGESI_PASSWORD");

        JSONObject paramIn = new JSONObject();
        paramIn.put("username", clientId);
        paramIn.put("password", clientSecret);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(paramIn.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("???????????????????????????token??????" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 200) {
            throw new IllegalArgumentException(paramOut.getString("message"));
        }

        JSONObject result = paramOut.getJSONObject("result");
        if (!result.containsKey("token")) {
            throw new IllegalStateException("???????????????????????? ?????????token");
        }

        token = result.getString("token");
        LocalCacheFactory.setValue("TAOGESI_CAR_TOKEN", token, 300); //??????5??????
        return token;
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Access-Token", getToken());
        httpHeaders.add("Content-Type", "application/json");
        return httpHeaders;
    }
}
