package com.java110.things.adapt.accessControl.chuangjiang;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.accessControl.AddUpdateFace;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 伊兰度 门禁设备 Mqtt 方式
 */
@Service("cjHttpAssessControlProcessAdapt")
public class CjHttpAssessControlProcessAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(CjHttpAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    private static final String DEFAULT_PORT = "8090"; //端口

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private RestTemplate restTemplate;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "CJ_";

    public static final String VERSION = "0.2";

    public static final String CMD_ADD_FACE = "/face"; // 创建人脸
    public static final String CMD_ADD_FACE_FIND = "/face/find"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "/device/openDoorControl"; // 开门

    public static final String CMD_REBOOT = "/restartDevice";// 重启设备

    public static final String CMD_ADD_USER = "/person/create"; // 添加人员

    public static final String CMD_DELETE_PERSION_FACE = "/face/deletePerson"; //修改人脸

    public static final String CMD_DELETE_FACE = "/person/delete"; //删除人脸
    public static final String CMD_RESET = "/device/reset"; //设备重置

    public static final String CMD_UI_TITLE = "set_ui_title";// 设置名称
    public static final String CMD_FACE_SEARCH = "face_search";// 搜素设备
    public static final String CMD_SET_PASSWORD = "/setPassWord";// 设置密码
    public static final String CMD_SET_SYSTEMMODE = "/device/systemMode";// 设置模式
    public static final String CMD_SET_IDENTIFY_CALLBACK = "/setIdentifyCallBack";// 设置回调地址


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
    public void initAssessControlProcess() {
        logger.debug("初始化是配置器");


    }

    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_SET_SYSTEMMODE;
        JSONObject param = new JSONObject();
        param.put("pass", password);
        param.put("systemMode", "2");
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_SYSTEMMODE, param.toJSONString(), responseEntity.getBody());

        //设置回调地址
        url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_SET_IDENTIFY_CALLBACK;
        param = new JSONObject();
        param.put("pass", password);
        param.put("callbackUrl", MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "IOT_URL") + "/api/accessControl/faceResult/" + machineDto.getMachineCode());
        param.put("base64Enable", "2");
        httpHeaders = new HttpHeaders();
        httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_IDENTIFY_CALLBACK, param.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "初始化设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_FACE_FIND;
        JSONObject param = new JSONObject();
        param.put("pass", password);
        param.put("personId", userFaceDto.getUserId());
        //添加人脸
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE_FIND, param.toJSONString(), responseEntity.getBody());


        if (HttpStatus.OK != responseEntity.getStatusCode()) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        JSONObject outParam = JSONObject.parseObject(responseEntity.getBody());

        if (!outParam.containsKey("data")) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        JSONArray data = outParam.getJSONArray("data");

        if (data == null || data.size() < 1) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        String personId = data.getJSONObject(0).getString("personId");

        if (StringUtil.isEmpty(personId)) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        return personId;
    }

    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_USER;

        JSONObject paramObj = JSONObject.parseObject("{\"person\":{}}");
        JSONObject param = paramObj.getJSONObject("person");
        param.put("id", userFaceDto.getUserId());
        param.put("name", userFaceDto.getName());
        param.put("idcardNum", "");
        param.put("iDNumber", userFaceDto.getIdNumber());
        paramObj.put("pass", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(paramObj.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, param.toJSONString(), responseEntity.getBody());

        url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_FACE;
        param = new JSONObject();
        param.put("pass", password);
        param.put("personId", userFaceDto.getUserId());
        param.put("faceId", userFaceDto.getUserId());
        param.put("url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        param.put("base64", userFaceDto.getFaceBase64());
        //添加人脸
        httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE, param.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));


    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "";
        JSONObject param = new JSONObject();
        url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_FACE;
        param = new JSONObject();
        param.put("pass", password);
        param.put("personId", userFaceDto.getUserId());
        param.put("faceId", userFaceDto.getUserId());
        param.put("url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        param.put("base64", userFaceDto.getFaceBase64());
        //添加人脸
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE, param.toJSONString(), responseEntity.getBody());


        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_DELETE_FACE;


        JSONObject param = new JSONObject();
        param.put("id", heartbeatTaskDto.getTaskid());
        param.put("pass", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), responseEntity.getBody());

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_RESET;
        JSONObject param = new JSONObject();
        param.put("delete", false);
        param.put("pass", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
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

        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        List<MachineDto> machineDtos = notifyAccessControlService.queryMachines();
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "";
        for (MachineDto machineDto : machineDtos) {
            url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_SET_PASSWORD;
            JSONObject param = new JSONObject();
            param.put("oldPass", password);
            param.put("newPass", password);
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_PASSWORD, param.toJSONString(), responseEntity.getBody());

            url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_SET_SYSTEMMODE;
            param = new JSONObject();
            param.put("pass", password);
            param.put("systemMode", "2");
            httpHeaders = new HttpHeaders();
            httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
            responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_SYSTEMMODE, param.toJSONString(), responseEntity.getBody());

            //设置回调地址
            url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_SET_IDENTIFY_CALLBACK;
            param = new JSONObject();
            param.put("pass", password);
            param.put("callbackUrl", MappingCacheFactory.getValue(MappingCacheFactory.COMMON_DOMAIN, "CJ_CALLBACK_URL") + "?machineCode=" + machineDto.getMachineCode());
            param.put("base64Enable", "2");
            httpHeaders = new HttpHeaders();
            httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_SET_IDENTIFY_CALLBACK, param.toJSONString(), responseEntity.getBody());

        }
        return null;
    }

    @Override
    public void mqttMessageArrived(String topic, String data) {


    }


    @Override
    public void restartMachine(MachineDto machineDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        JSONObject param = new JSONObject();
        param.put("pass", password);
        //
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_REBOOT;
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_REBOOT, param.toJSONString(), responseEntity.getBody());

    }

    @Override
    public void openDoor(MachineDto machineDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        JSONObject param = new JSONObject();
        param.put("pass", password);
        //
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_OPEN_DOOR;
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, param.toJSONString(), responseEntity.getBody());
    }

    @Override
    public String httpFaceResult(String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineIp(body.getString("ip"));
            List<MachineDto> machineDtos = notifyAccessControlService.queryMachines(machineDto);

            if (machineDtos.size() < 0) {
                resultParam.put("result", 1);
                resultParam.put("success", true);
                return resultParam.toJSONString();//未找到设备
            }

            String userId = body.containsKey("personId") ? body.getString("personId") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setUserId(userId);
                machineFaceDto.setMachineId(machineDtos.get(0).getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                }

            }


            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(body.getString("base64"));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDtos.get(0).getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.containsKey("identifyType") && "1".equals(body.getString("identifyType")) ? "100" : "0");


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
     * 查询费用信息
     *
     * @param openDoorDto
     */
    private void freshOwnerFee(OpenDoorDto openDoorDto) {

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


    private void openDoorResult(String data) {


    }

    /**
     * 设备上线
     *
     * @param data {
     *             "cmd": "mqtt_online",
     *             "sn": "fffffff",
     *             "result": "mqtt is online"
     *             }
     */
    private void machineOnline(String data) {


    }


    /**
     * 重启
     */
    private void setUiTitle(MachineDto machineDto) {

        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_UI_TITLE);
        JSONObject body = new JSONObject();
        body.put("title", machineDto.getMachineName());
        param.put("body", body);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(param.getString("cmd_id"), machineDto.getMachineId(), CMD_UI_TITLE, param.toJSONString(), "");


    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     */
    private void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam) {
        saveLog(logId, machineId, cmd, reqParam, resParam, "", "", "");
    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     */
    private void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam, String state) {
        saveLog(logId, machineId, cmd, reqParam, resParam, state, "", "");
    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     * @param userId    业主ID
     * @param userName  业主名称
     */
    private void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam, String state, String userId, String userName) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setLogId(logId);
        operateLogDto.setMachineId(machineId);
        operateLogDto.setOperateType(cmd);
        operateLogDto.setReqParam(reqParam);
        operateLogDto.setResParam(resParam);
        operateLogDto.setState(state);
        operateLogDto.setUserId(userId);
        operateLogDto.setUserName(userName);
        notifyAccessControlService.saveOrUpdateOperateLog(operateLogDto);
    }

}
