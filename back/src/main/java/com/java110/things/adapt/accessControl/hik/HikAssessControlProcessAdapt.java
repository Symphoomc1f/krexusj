package com.java110.things.adapt.accessControl.hik;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.LocalCacheFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.util.*;
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
import java.util.Date;
import java.util.List;

/**
 * bisen
 * 文档：http://www.bisen-iot.com/docs/a45b755218844fd892f230a492cdcf45
 * 作者：吴学文
 * QQ:928255095
 */
@Service("hikAssessControlProcessAdapt")
public class HikAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(HikAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    private static final String DEFAULT_PORT = "8090"; //端口


    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private ICallAccessControlService callAccessControlServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "CJ_";

    public static final String VERSION = "0.2";

    public static final String GET_TOKEN = "/oauth/token";
    public static final String ADD_MACHINE = "/api/v1/estate/devices";
    public static final String UPDATE_MACHINE = "/api/device/bs/face/modify";
    public static final String DELETE_MACHINE = "/api/v1/estate/devices/actions/deleteDevice";

    public static final String CMD_ADD_FACE = "/face"; // 创建人脸
    public static final String CMD_ADD_FACE_FIND = "/face/find"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "/api/v1/estate/entranceGuard/remoteControl/actions/gateControl"; // 开门

    public static final String CMD_REBOOT = "/api/device/bs/face/deviceReboot";// 重启设备

    public static final String CMD_ADD_USER = "/api/v1/estate/system/person"; // 添加人员
    public static final String CMD_SINGLE_PERSON_AUTH = "/api/v1/estate/entranceGuard/permissions/actions/authorityIssued"; // 添加人员
    public static final String CMD_UPDATE_USER = "/api/v1/estate/system/person/actions/updatePerson"; // 修改人员

    public static final String CMD_DELETE_PERSION_FACE = "/api/v1/estate/entranceGuard/permissions/actions/authorityDelete"; //修改人脸

    public static final String CMD_DELETE_FACE = "/person/delete"; //删除人脸
    public static final String CMD_RESET = "/device/reset"; //设备重置

    public static final String CMD_CHANGE_CARD = "/api/v1/estate/system/cards/actions/changeCard";// 设置名称
    public static final String CMD_QRCODE = "/api/v1/estate/entranceGuard/permissions/actions/getQRcode";// 获取二维码
    public static final String CMD_SET_PASSWORD = "/setPassWord";// 设置密码
    public static final String CMD_SET_SYSTEMMODE = "/device/systemMode";// 设置模式
    public static final String CMD_SET_IDENTIFY_CALLBACK = "/api/callback/deviceCallback/updateCallback";// 设置回调地址
    public static final String CMD_SET_HEARTBREAT = "/setDeviceHeartBeat";// 设置回调地址


    //单设备处理
    public static final String TOPIC_FACE_SN_REQUEST = "face.{sn}.request";

    //多设备处理
    public static final String TOPIC_FACE_REQUEST = "face.request";

    //接收设备处理
    public static final String TOPIC_FACE_SN_RESPONSE = "face.{sn}.response";

    //识别结果上报
    public static final String TOPIC_FACE_RESPONSE = "face.response";

    //硬件上线上报
    public static final String TOPIC_ONLINE_RESPONSE = "online/response";

    public static final String SN = "{sn}";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";

    public static final String FACE_RESULT = "face_result";

    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";


    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        return null;
    }


    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        JSONObject postParameters = new JSONObject();
        String paramOutString = "";
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = null;

        communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "添加车辆时未查到小区信息");

        //查询用户信息

        try {
            String url = MappingCacheFactory.getValue("HIK_URL") + CMD_ADD_USER;

            postParameters.put("personName", userFaceDto.getName());
            postParameters.put("credentialType", 1);
            postParameters.put("credentialNumber", userFaceDto.getIdNumber());
            postParameters.put("mobile", userFaceDto.getLink());
            postParameters.put("faceUrl", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

            List<String> cardNumbers = new ArrayList<>();
            cardNumbers.add(getIdNumber(userFaceDto));
            postParameters.put("cardNumbers", cardNumbers);
            logger.debug("人员创建请求：,url:" + url);
            //responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
            logger.debug("人员创建返回：" + paramOutString);

            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, postParameters.toJSONString(), paramOutString);

            JSONObject paramOut = JSONObject.parseObject(paramOutString);
            if (paramOut.getIntValue("code") == 200) {
                JSONObject data = paramOut.getJSONObject("data");
                userFaceDto.setExtUserId(data.getString("personId"));
                userFaceDto.setCardId(data.getJSONArray("cardIds").getString(0));
                userFaceDto.setCardNumber(getIdNumber(userFaceDto));
            } else if (paramOut.getIntValue("code") == 511021) { // 说明业主已经存在
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setUserId(userFaceDto.getUserId());
                List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
                if (faceDtos.size() < 1) {
                    throw new IllegalArgumentException("该人脸还没有添加");
                }
                userFaceDto.setExtUserId(faceDtos.get(0).getExtUserId());
                userFaceDto.setCardId(faceDtos.get(0).getCardId());
                userFaceDto.setCardNumber(faceDtos.get(0).getCardNumber());
            } else {
                //刷入外部编码
                throw new IllegalArgumentException(paramOut.getString("msg"));
            }

            url = MappingCacheFactory.getValue("HIK_URL") + CMD_SINGLE_PERSON_AUTH;
            postParameters = new JSONObject();
            postParameters.put("communityId", communityDtos.get(0).getThirdCommunityId());
            postParameters.put("deviceId", machineDto.getThirdMachineId());
            postParameters.put("personType", 1);
            postParameters.put("personId", userFaceDto.getExtUserId());

            paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
            paramOut = JSONObject.parseObject(paramOutString);
            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SINGLE_PERSON_AUTH, postParameters.toJSONString(), paramOutString);
            return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
        } catch (Exception e) {
            logger.error("出现异常了" + postParameters + ",返回" + paramOutString, e);

            throw e;
        }
    }


    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = null;
        communityDtos = communityServiceImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "添加车辆时未查到小区信息");

        //刷入外部编码
        String paramOutString = "";
        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(userFaceDto.getUserId());
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }
        userFaceDto.setExtUserId(faceDtos.get(0).getExtUserId());

        String url = MappingCacheFactory.getValue("HIK_URL") + CMD_UPDATE_USER;
        JSONObject postParameters = new JSONObject();
        postParameters.put("personId", userFaceDto.getExtUserId());
        postParameters.put("personName", userFaceDto.getName());
        postParameters.put("credentialType", 1);
        postParameters.put("credentialNumber", userFaceDto.getIdNumber());
        postParameters.put("mobile", userFaceDto.getLink());
        postParameters.put("faceUrl", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_UPDATE_USER, postParameters.toJSONString(), paramOutString);
        JSONObject paramOut = JSONObject.parseObject(paramOutString);

        if (paramOut.getIntValue("code") != 200) {
            return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
        }

        // 判断 门禁卡是否 有变动
        String cardNumber = getIdNumber(userFaceDto);
        if (cardNumber.equals(faceDtos.get(0).getCardNumber())) { // 没有变动
            return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
        }

        //换卡
        url = MappingCacheFactory.getValue("HIK_URL") + CMD_CHANGE_CARD;
        postParameters = new JSONObject();
        postParameters.put("cardId", faceDtos.get(0).getCardId());
        postParameters.put("cardNumber", getIdNumber(userFaceDto));

        paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_CHANGE_CARD, postParameters.toJSONString(), paramOutString);
        paramOut = JSONObject.parseObject(paramOutString);

        if (paramOut.getIntValue("code") != 200) {
            return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
        }

        JSONObject data = paramOut.getJSONObject("data");

        userFaceDto.setCardId(data.getString("cardId"));
        userFaceDto.setCardNumber(getIdNumber(userFaceDto));
        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = null;
        communityDtos = communityServiceImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "添加车辆时未查到小区信息");
        //刷入外部编码
        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }

        //判断人员是否存在
        String url = MappingCacheFactory.getValue("HIK_URL")
                + CMD_DELETE_PERSION_FACE;
        logger.debug("判断人员是否存在" + url);
        JSONObject postParameters = new JSONObject();
        postParameters = new JSONObject();
        postParameters.put("communityId", communityDtos.get(0).getThirdCommunityId());
        postParameters.put("deviceId", machineDto.getThirdMachineId());
        postParameters.put("personType", 1);
        postParameters.put("personId", faceDtos.get(0).getExtUserId());

        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
        JSONObject paramOut = JSONObject.parseObject(paramOutString);

        if (paramOut.getIntValue("code") != 200) {
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("message"));
        }

        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + CMD_RESET;
        JSONObject param = new JSONObject();
        param.put("delete", false);
        param.put("pass", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_RESET, param.toJSONString(), responseEntity.getBody());
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));
    }


    /**
     * 扫描设备
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() throws Exception {
        return null;
    }


    @Override
    public void restartMachine(MachineDto machineDto) {

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setPage(1);
        machineFaceDto.setRow(1);
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }

        String url = MappingCacheFactory.getValue("HIK_URL") + CMD_REBOOT;

        JSONObject postParameters = new JSONObject();
        postParameters.put("personId", faceDtos.get(0).getExtUserId());
        postParameters.put("deviceId", machineDto.getMachineCode());
        postParameters.put("command", "open");
        HttpHeaders httpHeaders = getHeader();
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_REBOOT, postParameters.toJSONString(), responseEntity.getBody());
    }

    @Override
    public void openDoor(MachineDto machineDto) {
        String url = MappingCacheFactory.getValue("HIK_URL") + CMD_OPEN_DOOR;
        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setPage(0);
        machineFaceDto.setRow(1);
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        machineFaceDto.setHasExtUserId("Y");
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }
        JSONObject postParameters = new JSONObject();
        postParameters.put("personId", faceDtos.get(0).getExtUserId());
        postParameters.put("deviceId", machineDto.getThirdMachineId());
        postParameters.put("command", "open");

        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");

        logger.debug("请求信息 ： " + postParameters + "，返回信息:" + paramOutString);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, postParameters.toJSONString(), paramOutString);
    }

    @Override
    public ResultDto getQRcode(UserFaceDto userFaceDto) {
        String url = MappingCacheFactory.getValue("HIK_URL") + CMD_QRCODE;
        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setPage(0);
        machineFaceDto.setRow(1);
        machineFaceDto.setMachineCode(userFaceDto.getMachineCode());
        machineFaceDto.setHasExtUserId("Y");
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }
        JSONObject postParameters = new JSONObject();
        postParameters.put("personId", faceDtos.get(0).getExtUserId());
        postParameters.put("personType", 1);

        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");

        logger.debug("请求信息 ： " + postParameters + "，返回信息:" + paramOutString);
        JSONObject paramOut = JSONObject.parseObject(paramOutString);
        //saveLog(SeqUtil.getId(), userFaceDto.getMachineId(), CMD_OPEN_DOOR, postParameters.toJSONString(), paramOutString);
        if (paramOut.getIntValue("code") != 200) {
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("message"));
        }

        return new ResultDto(ResultDto.SUCCESS, paramOut.getString("message"), paramOut.getJSONObject("data").getString("qrCode"));
    }

    @Override
    public String httpFaceResult(MachineDto machineDto, String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);

            String userId = body.containsKey("customId") ? body.getString("customId") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setExtUserId(userId);
                machineFaceDto.setMachineId(machineDto.getMachineId());
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
            openDoorDto.setFace(ImageFactory.encodeImageToBase64(body.getString("picture")));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDto.getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.getString("similarityList"));

            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备

    }

    @Override
    public String heartbeat(String data, String machineCode) throws Exception {
        JSONObject info = JSONObject.parseObject(data);

        //设备ID
        //String machineCode = info.getString("deviceKey");
        String heartBeatTime = null;
        //heartBeatTime = info.getString("time");
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        JSONObject resultParam = new JSONObject();
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备
    }

    @Override
    public ResultDto addMachine(MachineDto machineDto) {
        String url = MappingCacheFactory.getValue("HIK_URL") + ADD_MACHINE;
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = null;
        communityDtos = communityServiceImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "添加车辆时未查到小区信息");

        JSONObject postParameters = new JSONObject();
        postParameters.put("communityId", communityDtos.get(0).getThirdCommunityId());
        postParameters.put("deviceSerial", machineDto.getMachineCode());
        postParameters.put("validateCode", machineDto.getMachineVersion());
        postParameters.put("deviceName", machineDto.getMachineName());

        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
        JSONObject paramOut = JSONObject.parseObject(paramOutString);

        if (paramOut.getIntValue("code") != 200) {
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("message"));
        }

        JSONObject data = paramOut.getJSONObject("data");
        machineDto.setThirdMachineId(data.getString("deviceId"));
        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
    }

    @Override
    public ResultDto updateMachine(MachineDto machineDto) {
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) {
        String url = MappingCacheFactory.getValue("HIK_URL") + DELETE_MACHINE;

        List<MachineDto> machineDtos = null;
        try {
            machineDtos = callAccessControlServiceImpl.queryMachines(machineDto);
        } catch (Exception e) {
            throw new IllegalArgumentException("设备不存在");
        }
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("设备不存在");
        }
        JSONObject postParameters = new JSONObject();
        postParameters.put("deviceId", machineDtos.get(0).getThirdMachineId());

        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
        JSONObject paramOut = JSONObject.parseObject(paramOutString);

        if (paramOut.getIntValue("code") != 200) {
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("message"));
        }
        return new ResultDto(paramOut.getIntValue("code") == 200 ? 0 : paramOut.getIntValue("code"), paramOut.getString("message"));
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

    private HttpHeaders getHeader() {
        return getHeader(0);
    }

    private HttpHeaders getHeader(int length) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + getToken());
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Host", "www.bisen-iot.com");
        if (length > 0) {
            httpHeaders.add("Content-Length", length + "");
        }
        return httpHeaders;
    }
}
