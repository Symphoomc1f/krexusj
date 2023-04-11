package com.java110.things.adapt.car.zeroOne;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.Assert;
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

@Service
public class ZeroOneQueryCarInoutLogAdapt {
    private static Logger logger = LoggerFactory.getLogger(ZeroOneCarSocketProcessAdapt.class);

    public static final String SPEC_EXT_PARKING_ID = "6185-17861";
    private static final String PULLCARENTRY_URL = "/yihao01-park-opendata/outapi/dataQuery/pullCarEntry";
    private static final String PULLTEMPCAREXITDETAIL_URL = "/yihao01-park-opendata/outapi/dataQuery/pullTempCarExitDetail";
    private static final String PULLMONTHCAREXITDETAIL_URL = "/yihao01-park-opendata/outapi/dataQuery/pullMonthCarExitDetail";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private IParkingAreaService parkingAreaService;

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private ICarService carService;

    public void query(String pId) {
        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + PULLCARENTRY_URL;
        String appKey = MappingCacheFactory.getValue("ZERO_ONE_APP_KEY");
        int min = Integer.parseInt(MappingCacheFactory.getValue("ZERO_ONE_MIN"));
        //拉去场内车辆

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(pId);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");

        MachineDto machineDto = new MachineDto();
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        machineDto.setLocationObjId(parkingAreaDtos.get(0).getExtPaId());
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("停车场没有设备");
        }
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("appKey", appKey);
        postParameters.put("endTime", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, min * -1);
        postParameters.put("startTime", DateUtil.getFormatTimeString(endTime.getTime(), DateUtil.DATE_FORMATE_STRING_A));
        postParameters.put("pageNo", "1");
        postParameters.put("pageSize", "100");
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        url += HttpFactory.mapToUrlParam(postParameters);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        logger.debug("请求参数：" + httpEntity + ",返回参数：" + responseEntity);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("查询场内车辆失败" + responseEntity);
        }


        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        String msg = "成功";

        if (!"200".equals(paramOut.getString("code"))) {
            throw new IllegalStateException("查询场内车辆失败" + paramOut.getString("desc"));
        }

        JSONArray list =paramOut.getJSONObject("data").getJSONArray("list");

        JSONObject acceptJson = null;

        JSONObject zeroOneObj = null;
        if (list != null && list.size() > 1) {
            for (int listIndex = 0; listIndex < list.size(); listIndex++) {
                try {
                    acceptJson = new JSONObject();
                    zeroOneObj = list.getJSONObject(listIndex);
                    acceptJson.put("passType", "1");
                    acceptJson.put("plateNum", zeroOneObj.getString("plateCn"));
                    acceptJson.put("carType", zeroOneObj.getString("parkType"));
                    acceptJson.put("parkName", zeroOneObj.getString("channelName"));
                    acceptJson.put("recordId", zeroOneObj.getString("id"));
                    acceptJson.put("inTime", zeroOneObj.getString("enterTimeStr"));
                    acceptJson.put("remark", zeroOneObj.getString("operatorName"));
                    accept(machineDtos.get(0), acceptJson);
                } catch (Exception e) {
                    logger.error("处理失败", e);
                }
            }
        }

        //临时车出场
        url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + PULLTEMPCAREXITDETAIL_URL;
        url += HttpFactory.mapToUrlParam(postParameters);
        responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        logger.debug("临时车出场请求参数：" + httpEntity + ",返回参数：" + responseEntity);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("查询场内车辆失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!"200".equals(paramOut.getString("code"))) {
            throw new IllegalStateException("查询场内车辆失败" + paramOut.getString("desc"));
        }

        list = paramOut.getJSONObject("data").getJSONArray("list");
        if (list != null && list.size() > 1) {
            for (int listIndex = 0; listIndex < list.size(); listIndex++) {
                try {
                    acceptJson = new JSONObject();
                    zeroOneObj = list.getJSONObject(listIndex);
                    acceptJson.put("passType", "2");
                    acceptJson.put("plateNum", zeroOneObj.getString("plate"));
                    acceptJson.put("carType", zeroOneObj.getString("parkType"));
                    acceptJson.put("parkName", zeroOneObj.getString("channelName"));
                    acceptJson.put("recordId", zeroOneObj.getString("id"));
                    acceptJson.put("outTime", zeroOneObj.getString("outTimeStr"));
                    acceptJson.put("remark", zeroOneObj.getString("operatorName"));
                    acceptJson.put("charge", zeroOneObj.getString("payCharge"));
                    accept(machineDtos.get(0), acceptJson);
                } catch (Exception e) {
                    logger.error("处理失败", e);
                }
            }
        }

        //月租车出场
        url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + PULLMONTHCAREXITDETAIL_URL;
        url += HttpFactory.mapToUrlParam(postParameters);
        responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        logger.debug("月租车出场请求参数：" + httpEntity + ",返回参数：" + responseEntity);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("查询场内车辆失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!"200".equals(paramOut.getString("code"))) {
            throw new IllegalStateException("查询场内车辆失败" + paramOut.getString("desc"));
        }

        list = paramOut.getJSONObject("data").getJSONArray("list");
        if (list != null && list.size() > 1) {
            for (int listIndex = 0; listIndex < list.size(); listIndex++) {
                try {
                    acceptJson = new JSONObject();
                    zeroOneObj = list.getJSONObject(listIndex);
                    acceptJson.put("passType", "2");
                    acceptJson.put("plateNum", zeroOneObj.getString("plate"));
                    acceptJson.put("carType", zeroOneObj.getString("parkType"));
                    acceptJson.put("parkName", zeroOneObj.getString("channelName"));
                    acceptJson.put("recordId", zeroOneObj.getString("id"));
                    acceptJson.put("outTime", zeroOneObj.getString("outTimeStr"));
                    acceptJson.put("remark", zeroOneObj.getString("operatorName"));
                    acceptJson.put("charge", "0");
                    accept(machineDtos.get(0), acceptJson);
                } catch (Exception e) {
                    logger.error("处理失败", e);
                }
            }
        }

    }


    /**
     * 开门记录上报
     *
     * @param machineDto
     * @return
     */
    public Java110CarProtocol accept(MachineDto machineDto, JSONObject acceptJson) {

        //JSONObject acceptJson = JSONObject.parseObject(content);
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
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState(CarInoutDto.STATE_OUT);

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
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState(CarInoutDto.STATE_IN);

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
