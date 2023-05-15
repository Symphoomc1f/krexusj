package com.java110.things.adapt.accessControl.bisen;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.*;
import com.java110.things.util.DateUtil;
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

import java.util.Date;
import java.util.List;

/**
 * bisen
 * 文档：http://www.bisen-iot.com/docs/a45b755218844fd892f230a492cdcf45
 * 作者：吴学文
 * QQ:928255095
 */
@Service("bisenHttpAssessControlProcessAdapt")
public class BisenHttpAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(BisenHttpAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    private static final String DEFAULT_PORT = "8090"; //端口


    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private ICallAccessControlService callAccessControlServiceImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "CJ_";

    public static final String VERSION = "0.2";

    public static final String GET_TOKEN = "/api/auth/apply/token";
    public static final String ADD_MACHINE = "/api/device/bs/face/save";
    public static final String UPDATE_MACHINE = "/api/device/bs/face/modify";
    public static final String DELETE_MACHINE = "/api/device/bs/face/remoteOpenDoor";

    public static final String CMD_ADD_FACE = "/face"; // 创建人脸
    public static final String CMD_ADD_FACE_FIND = "/face/find"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "/api/device/bs/face/remoteOpenDoor"; // 开门

    public static final String CMD_REBOOT = "/api/device/bs/face/deviceReboot";// 重启设备

    public static final String CMD_ADD_USER = "/api/device/hqvtPerson/save"; // 添加人员
    public static final String CMD_SINGLE_PERSON_AUTH = "/api/device/bs/face/auth/singlePersonAuth"; // 添加人员
    public static final String CMD_UPDATE_USER = "/api/device/hqvtPerson/alter"; // 修改人员

    public static final String CMD_DELETE_PERSION_FACE = "/face/deletePerson"; //修改人脸

    public static final String CMD_DELETE_FACE = "/person/delete"; //删除人脸
    public static final String CMD_RESET = "/device/reset"; //设备重置

    public static final String CMD_UI_TITLE = "set_ui_title";// 设置名称
    public static final String CMD_FACE_SEARCH = "face_search";// 搜素设备
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
//        String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_SET_IDENTIFY_CALLBACK;
//        String appId = MappingCacheFactory.getValue("appId");
//        JSONObject postParameters = new JSONObject();
//        postParameters.put("appId", appId);
//        postParameters.put("callbackUrl", MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "IOT_URL") + "/api/accessControl/faceResult/" + machineDto.getMachineCode());
//        postParameters.put("cbType", 8);
//
//        HttpHeaders httpHeaders = getHeader();
//        HttpEntity httpEntity = new HttpEntity(postParameters.toJSONString(), httpHeaders);
//        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
//        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_IDENTIFY_CALLBACK, postParameters.toJSONString(), responseEntity.getBody());
//
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            return new ResultDto(ResultDto.ERROR, "初始化设备失败");
//        }
//
//        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
//        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));
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
        try {
            String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_ADD_USER;
            String appId = MappingCacheFactory.getValue("appId");

            postParameters.put("appId", appId);
            postParameters.put("personName", userFaceDto.getName());
            postParameters.put("tag", userFaceDto.getUserId());
            postParameters.put("certificateType", 0);
            postParameters.put("personIdCard", userFaceDto.getIdNumber());
            postParameters.put("rfid", getIdNumber(userFaceDto));
            postParameters.put("faceUrl", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

//            HttpHeaders httpHeaders = getHeader(postParameters.toJSONString().length());
//            HttpEntity httpEntity = new HttpEntity(postParameters.toJSONString(), httpHeaders);

            logger.debug("人员创建请求：,url:" + url);
            //responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
            logger.debug("人员创建返回：" + paramOutString);

            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, postParameters.toJSONString(), paramOutString);

            JSONObject paramOut = JSONObject.parseObject(paramOutString);
            if (paramOut.getIntValue("code") == 0) {
                userFaceDto.setExtUserId(paramOut.getJSONObject("data").getString("personGuid"));
            } else {
                //刷入外部编码
                throw new IllegalArgumentException(paramOut.getString("msg"));
//                MachineFaceDto machineFaceDto = new MachineFaceDto();
//                machineFaceDto.setUserId(userFaceDto.getUserId());
//                List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
//                if (faceDtos.size() < 1) {
//                    throw new IllegalArgumentException(paramOut.getString("msg"));
//                }
//                userFaceDto.setExtUserId(faceDtos.get(0).getExtUserId());
            }

            url = MappingCacheFactory.getValue("BISEN_URL") + CMD_SINGLE_PERSON_AUTH;
            postParameters = new JSONObject();
            postParameters.put("appId", appId);
            postParameters.put("deviceNo", machineDto.getMachineCode());
            postParameters.put("personGuid", userFaceDto.getExtUserId());
//            httpHeaders = getHeader(postParameters.toJSONString().length());
//            httpEntity = new HttpEntity(postParameters.toJSONString(), httpHeaders);
//            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");
            paramOut = JSONObject.parseObject(paramOutString);
            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SINGLE_PERSON_AUTH, postParameters.toJSONString(), paramOutString);
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
        } catch (Exception e) {
            logger.error("出现异常了" + postParameters + ",返回" + paramOutString, e);

            throw e;
        }
    }


    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

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

        String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_UPDATE_USER;
        String appId = MappingCacheFactory.getValue("appId");
        JSONObject postParameters = new JSONObject();
        postParameters.put("appId", appId);
        postParameters.put("personName", userFaceDto.getName());
        postParameters.put("personGuid", userFaceDto.getExtUserId());
        postParameters.put("tag", userFaceDto.getUserId());
        postParameters.put("certificateType", 0);
        postParameters.put("personIdCard", userFaceDto.getIdNumber());
        postParameters.put("faceUrl", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "PUT");
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_UPDATE_USER, postParameters.toJSONString(), paramOutString);

        JSONObject paramOut = JSONObject.parseObject(paramOutString);
        return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        //刷入外部编码
        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }
        String appId = MappingCacheFactory.getValue("appId");
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        //判断人员是否存在
        String url = MappingCacheFactory.getValue("BISEN_URL")
                + "/api/device/hqvtPerson/page?appId=" + appId + "&personGuid=" + faceDtos.get(0).getExtUserId();
        logger.debug("判断人员是否存在" + url);

        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getIntValue("code") != 0) {
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
        }

        int total = paramOut.getJSONObject("data").getIntValue("total");

        if (total < 1) {
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
        }

        url = MappingCacheFactory.getValue("BISEN_URL")
                + "/api/device/hqvtPerson/delete/appId/" + appId + "/personGuid/" + faceDtos.get(0).getExtUserId();
        logger.debug("删除人员" + url);
        responseEntity = outRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_UPDATE_USER, url, responseEntity.getBody());
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
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
        String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_REBOOT;
        String appId = MappingCacheFactory.getValue("appId");
        JSONObject postParameters = new JSONObject();
        postParameters.put("appId", appId);
        postParameters.put("deviceNo", machineDto.getMachineCode());
        HttpHeaders httpHeaders = getHeader();
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_REBOOT, postParameters.toJSONString(), responseEntity.getBody());
    }

    @Override
    public void openDoor(MachineDto machineDto) {
        String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_OPEN_DOOR;
        String appId = MappingCacheFactory.getValue("appId");

        JSONObject postParameters = new JSONObject();
        postParameters.put("appId", appId);
        postParameters.put("deviceNo", machineDto.getMachineCode());

        HttpHeaders httpHeaders = getHeader();
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, postParameters.toJSONString(), responseEntity.getBody());
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

    public static void main(String[] args) {
        String img = ImageFactory.getBase64ByImgUrl("https://bisen-temp-iot.oss-cn-beijing.aliyuncs.com/bisen-temp-iot/bs/face/40230b92623d42b59446a07185a1df2f.jpg");
        System.out.println(img);
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
//        ResultDto resultDto = null;
//        JSONObject postParameters = new JSONObject();
//        String paramOutString = "";
//        try {
//            String url = MappingCacheFactory.getValue("BISEN_URL") + ADD_MACHINE;
//            String appId = MappingCacheFactory.getValue("appId");
//
//            postParameters.put("appId", appId);
//            postParameters.put("deviceNo", machineDto.getMachineCode());
//            postParameters.put("deviceName", machineDto.getMachineName());
//            postParameters.put("tag", machineDto.getMachineId());
//
//            HttpHeaders httpHeaders = getHeader();
//            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
//            ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
//            saveLog(SeqUtil.getId(), machineDto.getMachineId(), ADD_MACHINE, postParameters.toJSONString(), responseEntity.getBody());
//
//            if (responseEntity.getStatusCode() != HttpStatus.OK) {
//                resultDto = new ResultDto(ResultDto.ERROR, "请求百胜获取token失败" + responseEntity);
//            }
//
//            JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
//            if (paramOut.getIntValue("code") != 0) {
//                resultDto = new ResultDto(ResultDto.ERROR, paramOut.getString("msg"));
//            }
//
//            url = MappingCacheFactory.getValue("BISEN_URL") + CMD_SET_IDENTIFY_CALLBACK;
//
//            postParameters = new JSONObject();
//            postParameters.put("appId", appId);
//            postParameters.put("callbackUrl", MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "IOT_URL") + "/api/accessControl/faceResultBisen");
//            postParameters.put("cbType", 8);
//
////            httpHeaders = getHeader();
////            httpEntity = new HttpEntity(postParameters.toJSONString(), httpHeaders);
////            responseEntity = outRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
//            paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "PUT");
//            paramOut = JSONObject.parseObject(paramOutString);
//            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_IDENTIFY_CALLBACK, paramOutString, responseEntity.getBody());
//
//            if (paramOut.getIntValue("code") != 0) {
//                resultDto = new ResultDto(ResultDto.ERROR, paramOut.getString("msg"));
//            }
//        } catch (Exception e) {
//            logger.error("推送报错" + postParameters, e);
//            throw e;
//        }
//        return resultDto;
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto updateMachine(MachineDto machineDto) {

//        ResultDto resultDto = null;
//        String url = MappingCacheFactory.getValue("BISEN_URL") + UPDATE_MACHINE;
//        String appId = MappingCacheFactory.getValue("appId");
//
//        JSONObject postParameters = new JSONObject();
//        postParameters.put("appId", appId);
//        postParameters.put("deviceNo", machineDto.getMachineCode());
//        postParameters.put("deviceName", machineDto.getMachineName());
//        postParameters.put("tag", machineDto.getMachineId());
//
//        HttpHeaders httpHeaders = getHeader();
//        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
//
//        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            resultDto = new ResultDto(ResultDto.ERROR, "请求百胜获取token失败" + responseEntity);
//        }
//
//        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
//        if (paramOut.getIntValue("code") != 0) {
//            resultDto = new ResultDto(ResultDto.ERROR, paramOut.getString("msg"));
//        }
//        return resultDto;
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) {
//        ResultDto resultDto = null;
//        String url = MappingCacheFactory.getValue("BISEN_URL") + DELETE_MACHINE;
//        String appId = MappingCacheFactory.getValue("appId");
//
//        url += ("?appId=" + appId + "&deviceNo=" + machineDto.getMachineCode());
//        HttpHeaders httpHeaders = getHeader();
//        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity("", httpHeaders);
//        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
//        if (responseEntity.getStatusCode() != HttpStatus.OK) {
//            resultDto = new ResultDto(ResultDto.ERROR, "请求百胜获取token失败" + responseEntity);
//        }
//
//        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
//        if (paramOut.getIntValue("code") != 0) {
//            resultDto = new ResultDto(ResultDto.ERROR, paramOut.getString("msg"));
//        }
//        return resultDto;
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }


    /**
     * 获取accessToken
     *
     * @return
     */
    private String getToken() {

        String token = LocalCacheFactory.getValue("bisen_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String url = MappingCacheFactory.getValue("BISEN_URL") + GET_TOKEN;
        String appId = MappingCacheFactory.getValue("appId");
        String appKey = MappingCacheFactory.getValue("appKey");
        String appSecret = MappingCacheFactory.getValue("appSecret");

        long timestamp = DateUtil.getCurrentDate().getTime();

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("appId", appId);
        postParameters.add("appKey", appKey);
        postParameters.add("timestamp", timestamp + "");
        String sign = AuthenticationFactory.md5(appKey + timestamp + appSecret);
        postParameters.add("sign", sign.toLowerCase());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        logger.debug("------请求信息：", httpEntity.toString());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求百胜获取token失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (paramOut.getIntValue("code") != 0) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        token = paramOut.getString("data");
        LocalCacheFactory.setValue("bisen_token", token, 10 * 24 * 60);
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
