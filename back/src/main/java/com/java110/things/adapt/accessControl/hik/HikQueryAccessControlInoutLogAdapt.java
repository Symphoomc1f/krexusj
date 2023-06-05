package com.java110.things.adapt.accessControl.hik;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.adapt.car.zeroOne.ZeroOneCarSocketProcessAdapt;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.LocalCacheFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.HttpClient;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class HikQueryAccessControlInoutLogAdapt {
    private static Logger logger = LoggerFactory.getLogger(ZeroOneCarSocketProcessAdapt.class);

    public static final String GET_TOKEN = "/oauth/token";
    private static final String CREATE_CONSUMER = "/api/v1/mq/consumer/group1";
    private static final String MESSAGES = "/api/v1/mq/consumer/messages";
    private static final String MSG_TYPE_EVENT_ACCESS = "community_event_access";

    @Autowired
    private ICallAccessControlService callAccessControlServiceImpl;

    private static String consumerId = "";

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    @Autowired
    private RestTemplate outRestTemplate;


    @Autowired
    private IMachineService machineServiceImpl;


    public void query() {
        String url = MappingCacheFactory.getValue("HIK_URL") + MESSAGES;

        if (StringUtil.isEmpty(consumerId)) {
            //创建消费者通道
            createConsumer();
        }

//        JSONObject postParameters = new JSONObject();
//        postParameters.put("consumerId", consumerId);
//        postParameters.put("autoCommit", true);
//
//        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("consumerId", consumerId);
        postParameters.add("autoCommit", "true");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.add("Authorization", "Bearer " + getToken());
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        logger.debug("------请求信息："+httpEntity.toString());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getIntValue("code") != 200) {
            throw new IllegalArgumentException("创建通道失败");
        }
        logger.debug("------返回信息："+paramOut);

        JSONArray data = paramOut.getJSONArray("data");

        if (data == null || data.size() < 1) {
            return;
        }

        JSONObject dataObj = null;
        JSONObject content = null;
        for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
            dataObj = data.getJSONObject(dataIndex);

            if (!MSG_TYPE_EVENT_ACCESS.equals(dataObj.getString("msgType"))) {
                continue;
            }
            content = dataObj.getJSONObject("content");

            httpFaceResult(content);

        }

    }

    private void createConsumer() {
        String url = MappingCacheFactory.getValue("HIK_URL") + CREATE_CONSUMER;

        JSONObject postParameters = new JSONObject();
        postParameters.put("consumerName", "group1");

        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
        JSONObject paramOut = JSONObject.parseObject(paramOutString);

        if (paramOut.getIntValue("code") != 200) {
            throw new IllegalArgumentException("创建通道失败");
        }

        JSONObject data = paramOut.getJSONObject("data");

        consumerId = data.getString("consumerId");
    }


    public String httpFaceResult(JSONObject content) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        List<MachineDto> machineDtos = null;
        try {
            JSONObject body = content;

            MachineDto machineDto = new MachineDto();
            machineDto.setThirdMachineId(body.getString("deviceId"));
            machineDtos = callAccessControlServiceImpl.queryMachines(machineDto);

            Assert.listOnlyOne(machineDtos, "设备不存在");

            String userId = body.containsKey("personId") ? body.getString("personId") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setExtUserId(userId);
                machineFaceDto.setMachineId(machineDtos.get(0).getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                    userId = machineFaceDtos.get(0).getUserId();
                }
            } else {
                userName = body.getString("personName");
            }
            if (StringUtil.isEmpty(userId)) {
                userId = "-1";
            }
            if (StringUtil.isEmpty(userName)) {
                userName = "门禁未上报";
            }

            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(ImageFactory.encodeImageToBase64(body.getString("pictureURL")));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDto.getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity("1");
            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备

    }


    /**
     * 获取accessToken
     *
     * @return
     */
    private String getToken() {

        String token = LocalCacheFactory.getValue("hik_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String url = MappingCacheFactory.getValue("HIK_URL") + GET_TOKEN;
        String appId = MappingCacheFactory.getValue("client_id");
        String appKey = MappingCacheFactory.getValue("client_secret");

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("client_id", appId);
        postParameters.add("client_secret", appKey);
        postParameters.add("grant_type", "client_credentials");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        logger.debug("------请求信息：", httpEntity.toString());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求百胜获取token失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!paramOut.containsKey("access_token")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        token = paramOut.getString("access_token");
        LocalCacheFactory.setValue("hik_token", token, 10 * 24 * 60);
        return token;
    }


    /**
     * 查询费用信息
     *
     * @param openDoorDto
     */
    protected void freshOwnerFee(OpenDoorDto openDoorDto) {

        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        List<FeeDto> feeDtos = new ArrayList<>();
        try {
            //查询业主房屋信息
            UserFaceDto userFaceDto = new UserFaceDto();
            userFaceDto.setUserId(openDoorDto.getUserId());
            List<RoomDto> roomDtos = notifyAccessControlService.getRooms(userFaceDto);

            if (roomDtos == null || roomDtos.size() < 1) {
                return;
            }

            for (RoomDto roomDto : roomDtos) {
                List<FeeDto> tmpFeeDtos = notifyAccessControlService.getFees(roomDto);
                if (tmpFeeDtos == null || tmpFeeDtos.size() < 1) {
                    continue;
                }
                feeDtos.addAll(tmpFeeDtos);
            }
        } catch (Exception e) {
            logger.error("云端查询物业费失败", e);
        }

        if (feeDtos.size() < 1) {
            openDoorDto.setAmountOwed("0");
            return;
        }
        double own = 0.00;
        for (FeeDto feeDto : feeDtos) {
            logger.debug("查询费用信息" + JSONObject.toJSONString(feeDto));
            own += feeDto.getAmountOwed();
        }

        openDoorDto.setAmountOwed(own + "");
    }
}
