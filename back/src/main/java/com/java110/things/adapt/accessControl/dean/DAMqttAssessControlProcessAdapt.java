package com.java110.things.adapt.accessControl.dean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.accessControl.AddUpdateFace;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.constant.ResponseConstant;
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
import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 伊兰度 门禁设备 Mqtt 方式
 */
@Service("daMqttAssessControlProcessAdapt")
public class DAMqttAssessControlProcessAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(DAMqttAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    private static final String DEFAULT_PORT = "80"; //端口

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
    public static final String CMD_ADD_FACE_FIND = "/action/SearchPerson"; // 名单查询

    public static final String CMD_OPEN_DOOR = "/action/OpenDoor"; // 开门

    public static final String CMD_REBOOT = "/action/RebootDevice";// 重启设备

    public static final String CMD_ADD_USER = "/action/AddPerson"; // 添加人员
    public static final String CMD_EDIT_USER = "/action/EditPerson"; // 添加人员

    public static final String CMD_DELETE_PERSION_FACE = "/face/deletePerson"; //修改人脸

    public static final String CMD_DELETE_FACE = "/action/DeletePerson"; //删除人脸
    public static final String CMD_RESET = "/action/DeleteAllPerson"; //设备重置

    public static final String CMD_UI_TITLE = "set_ui_title";// 设置名称
    public static final String CMD_FACE_SEARCH = "face_search";// 搜素设备
    public static final String CMD_SET_PASSWORD = "/setPassWord";// 设置密码
    public static final String CMD_SET_SYSTEMMODE = "/device/systemMode";// 设置模式
    public static final String CMD_SET_IDENTIFY_CALLBACK = "/setIdentifyCallBack";// 设置回调地址


    //单设备处理
    public static final String TOPIC_FACE_SN_REQUEST = "mqtt/face/ID";

    //多设备处理
    public static final String TOPIC_FACE_REQUEST = "face.request";

    //接收设备处理
    public static final String TOPIC_FACE_SN_RESPONSE = "face.{sn}.response";

    //识别结果上报
    public static final String TOPIC_FACE_RESPONSE = "face.response";

    //硬件上线上报
    public static final String TOPIC_ONLINE_RESPONSE = "online/response";

    public static final String SN = "ID";

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
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    public HttpHeaders getHeaders() {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "DA_ASSESS_PASSWORD");
        String auth = "Basic ";
        auth += new BASE64Encoder().encode(("admin:" + password).getBytes());
        //添加人脸
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", auth);
        return httpHeaders;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_FACE_FIND;
        JSONObject param = new JSONObject();
        param.put("operator", "SearchPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("SearchType", 0);
        info.put("SearchID", userFaceDto.getUserId());
        info.put("Picture", 1);
        param.put("info", info);

        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE_FIND, param.toJSONString(), responseEntity.getBody());


        if (HttpStatus.OK != responseEntity.getStatusCode()) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        JSONObject outParam = JSONObject.parseObject(responseEntity.getBody());

        if (!outParam.containsKey("picinfo")) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        String picinfo = outParam.getString("picinfo");

        if (StringUtil.isEmpty(picinfo)) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        String personId = outParam.getJSONObject("info").getString("CustomizeID");

        if (StringUtil.isEmpty(personId)) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        return personId;
    }

    /**
     * { "
     * operator": "AddPerson",
     * "info": {
     * <p>
     * },
     * "picinfo":"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQAB......"
     * }
     *
     * @param machineDto  硬件信息
     * @param userFaceDto 用户人脸信息
     * @return
     */
    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        JSONObject param = new JSONObject();
        param.put("messageId", userFaceDto.getTaskId());
        param.put("operator", "EditPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("PersonType", 0);
        info.put("IdType", 0);
        info.put("CustomizeID", userFaceDto.getUserId());
        info.put("PersonUUID", userFaceDto.getUserId());
        info.put("Name", userFaceDto.getName());
        info.put("CardType", 0);
        info.put("IdCard", userFaceDto.getIdNumber());
        info.put("Tempvalid", 0);
        info.put("isCheckSimilarity", 0);
        param.put("info", info);
        //param.put("picinfo", userFaceDto.getFaceBase64());
        param.put("picURI", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, param.toJSONString(), "");

        String msg = "同步成功";

        return new ResultDto(ResultDto.SUCCESS, "上传mqtt成功");


    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        JSONObject param = new JSONObject();
        param.put("messageId", userFaceDto.getTaskId());
        param.put("operator", "EditPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("PersonType", 0);
        info.put("IdType", 0);
        info.put("CustomizeID", userFaceDto.getUserId());
        info.put("PersonUUID", userFaceDto.getUserId());
        info.put("Name", userFaceDto.getName());
        info.put("CardType", 0);
        info.put("IdCard", userFaceDto.getIdNumber());
        info.put("Tempvalid", 0);
        info.put("isCheckSimilarity", 0);
        param.put("info", info);
        //param.put("picinfo", userFaceDto.getFaceBase64());
        param.put("picURI", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_EDIT_USER, param.toJSONString(), "");
        return new ResultDto(ResultDto.SUCCESS, "上传mqtt成功");
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_DELETE_FACE;
        JSONArray userIds = new JSONArray();
        userIds.add(heartbeatTaskDto.getTaskid());
        JSONObject param = new JSONObject();
        param.put("messageId", heartbeatTaskDto.getTaskid());
        param.put("operator", "DelPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("TotalNum", 1);
        info.put("IdType", 0);
        info.put("CustomizeID", userIds);
        param.put("info", info);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "");

        return new ResultDto(ResultDto.SUCCESS, "上传mqtt成功");

    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_RESET;
        JSONObject param = new JSONObject();
        param.put("operator", "DeleteAllPerson");
        param.put("messageId", heartbeatTaskDto.getTaskid());
        JSONObject info = new JSONObject();
        info.put("deleteall", "1");
        param.put("info", info);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_RESET, param.toJSONString(), "");

        return new ResultDto(ResultDto.SUCCESS, "上传mqtt成功");
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
    public void mqttMessageArrived(String topic, String data) {
        JSONObject param = JSONObject.parseObject(data);

        switch (topic) {
            case TOPIC_FACE_RESPONSE:
                openDoorResult(data);
                break;
            case TOPIC_ONLINE_RESPONSE: //硬件上线
                machineOnline(data);
                break;
            default:
                machineCmdResult(data);
                break;
        }

        if (!param.containsKey("cmd_id")) {
            return;
        }
        String state = param.containsKey("code") && "0".equals(param.getString("code")) ? "10002" : "10003";
        String marchineId = "-1";
        if (param.containsKey("sn")) {
            ResultDto resultDto = null;
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(param.getString("sn"));
            machineDto.setMachineTypeCd("9999"); // 标识门禁 以防万一和道闸之类冲突
            try {
                resultDto = machineServiceImpl.getMachine(machineDto);
                if (resultDto != null && resultDto.getCode() == ResponseConstant.SUCCESS) {
                    List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
                    if (machineDtos.size() > 0) {
                        marchineId = machineDtos.get(0).getMachineId();
                    }
                }
            } catch (Exception e) {

            }


        }
        saveLog(param.getString("cmd_id"), marchineId, topic, "", data, state);

    }

    private void machineCmdResult(String data) {
        JSONObject resultCmd = JSONObject.parseObject(data);
        if (!resultCmd.containsKey("cmd")) {
            return;
        }
        String cmd = resultCmd.getString("cmd");
//        switch (cmd) {
//            case CMD_ADD_FACE:
//                doCmdResultCloud(resultCmd);
//                break;
//            case CMD_UPDATE_FACE:
//                break;
//            case CMD_DELETE_FACE:
//                break;
//            default:
//                break;
//        }
    }


    @Override
    public void restartMachine(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("operator", "RebootDevice");
        param.put("messageId", SeqUtil.getId());
        JSONObject info = new JSONObject();
        param.put("info", info);
        //
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_REBOOT, param.toJSONString(), "");

    }

    @Override
    public void openDoor(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("operator", "Unlock");
        param.put("messageId", SeqUtil.getId());

        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("Chn", 0);
        info.put("status", 0);
        info.put("msg", "请通行");
        param.put("info", info);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, param.toJSONString(), "");
    }

    @Override
    public String httpFaceResult(String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);

            JSONObject info = body.getJSONObject("info");


            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(info.getString("DeviceID"));
            List<MachineDto> machineDtos = notifyAccessControlService.queryMachines(machineDto);

            if (machineDtos.size() < 0) {
                resultParam.put("code", 404);
                resultParam.put("desc", "设备不存在");
                return resultParam.toJSONString();//未找到设备
            }

            String userId = info.containsKey("PersonUUID") ? info.getString("PersonUUID") : "";
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
            openDoorDto.setFace(body.getString("SanpPic").replace("data:image/jpeg;base64,", ""));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDtos.get(0).getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(info.getString("Similarity1"));


            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
            resultParam.put("code", 404);
            resultParam.put("desc", "异常");
            return resultParam.toJSONString();//未找到设备
        }
        resultParam.put("code", 200);
        resultParam.put("desc", "OK");
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
