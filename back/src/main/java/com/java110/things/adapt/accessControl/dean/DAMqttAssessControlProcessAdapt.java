package com.java110.things.adapt.accessControl.dean;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineCmdResultDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
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
import org.springframework.http.HttpHeaders;
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
public class DAMqttAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

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
    public static final String TOPIC_FACE_SN_RESPONSE = "mqtt/face/ID/Ack";

    //识别结果上报
    public static final String TOPIC_FACE_RESPONSE = "mqtt/face/ID/Rec";

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
        //推送人脸识别结果
        MqttFactory.subscribe(TOPIC_FACE_SN_RESPONSE.replace(SN, machineDto.getMachineCode()));
        MqttFactory.subscribe(TOPIC_FACE_RESPONSE.replace(SN, machineDto.getMachineCode()));
        MqttFactory.subscribe("mqtt/face/heartbeat");
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

        return "-1";
    }

    /**
     * { "messageId":"ID:localhost-637050272518414388:79346:87:5",
     * "operator":"EditPerson",
     * "info": { "personId":"",
     * "customId":"772020122840610978",
     * "name":"王工",
     * "nation":1,
     * "gender":0,
     * "birthday":"1995-06-12",
     * "address":"",
     * "idCard":"632126199109163355",
     * "tempCardType":0,
     * "EffectNumber":3,
     * "cardValidBegin":"2019-10-10 10:00:00",
     * "cardValidEnd":"2021-10-10 16:00:00",
     * "telnum1":"18888888888",
     * "native":"广东深圳",
     * "cardType2":0,
     * "cardNum2":"",
     * "notes":"",
     * "personType":0,
     * "cardType":0, "dwidentity":0, "picURI":"http://proxy.homecommunity.cn:9006/face/e01c90be-f923-4afe-831a-5b2e1051c987/772020122840610978.jpg"} }
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
        //info.put("personId", "");
        info.put("customId", userFaceDto.getUserId());
        info.put("name", userFaceDto.getName());
        info.put("nation", 1);
        info.put("gender", 0);
        info.put("birthday", "1995-06-12");
        info.put("address", "");
        info.put("idCard", userFaceDto.getIdNumber());
        info.put("tempCardType", 0);
        info.put("EffectNumber", 3);
        info.put("cardValidBegin", userFaceDto.getStartTime());
        info.put("cardValidEnd", userFaceDto.getEndTime());
        info.put("telnum1", "18888888888");
        info.put("native", "广东深圳");
        info.put("cardType2", 0);
        info.put("cardNum2", "");
        info.put("notes", "");
        info.put("personType", 0);
        info.put("cardType", 0);
        info.put("dwidentity", 0);
        info.put("picURI", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        param.put("info", info);
        //param.put("picinfo", userFaceDto.getFaceBase64());


        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(userFaceDto.getTaskId(), machineDto.getMachineId(), CMD_ADD_USER, param.toJSONString(), "");

        String msg = "同步成功";

        return new ResultDto(ResultDto.SUCCESS, "上传mqtt成功");


    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        JSONObject param = new JSONObject();
        param.put("messageId", userFaceDto.getTaskId());
        param.put("operator", "EditPerson");
        JSONObject info = new JSONObject();
        //info.put("personId", "");
        info.put("customId", userFaceDto.getUserId());
        info.put("name", userFaceDto.getName());
        info.put("nation", 1);
        info.put("gender", 0);
        info.put("birthday", "1995-06-12");
        info.put("address", "");
        info.put("idCard", userFaceDto.getIdNumber());
        info.put("tempCardType", 0);
        info.put("EffectNumber", 3);
        info.put("cardValidBegin", userFaceDto.getStartTime());
        info.put("cardValidEnd", userFaceDto.getEndTime());
        info.put("telnum1", "18888888888");
        info.put("native", "广东深圳");
        info.put("cardType2", 0);
        info.put("cardNum2", "");
        info.put("notes", "");
        info.put("personType", 0);
        info.put("cardType", 0);
        info.put("dwidentity", 0);
        info.put("picURI", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        param.put("info", info);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(userFaceDto.getTaskId(), machineDto.getMachineId(), CMD_EDIT_USER, param.toJSONString(), "");
        return new ResultDto(ResultDto.SUCCESS, "上传mqtt成功");
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {

        JSONObject param = new JSONObject();
        param.put("messageId", heartbeatTaskDto.getTaskid());
        param.put("operator", "DelPerson");
        JSONObject info = new JSONObject();
        info.put("customId", heartbeatTaskDto.getTaskinfo());
        param.put("info", info);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(heartbeatTaskDto.getTaskid(), machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "");

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
        saveLog(heartbeatTaskDto.getTaskid(), machineDto.getMachineId(), CMD_RESET, param.toJSONString(), "");

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

        if (topic.contains("Ack")) {
            machineCmdResult(data);
        } else if (topic.contains("Rec")) {
            openDoorResult(data);
        } else if ("mqtt/face/heartbeat".equals(topic)) { //心跳处理
            heartbeat(data, "");
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
        if (!resultCmd.containsKey("messageId")) {
            return;
        }
        String operator = resultCmd.getString("operator");
        switch (operator) {
            case "EditPerson-Ack":
                doCmdResultCloud(resultCmd);
                break;
            case "DelPerson-Ack":
                doCmdResultCloud(resultCmd);
                break;
            case CMD_DELETE_FACE:
                break;
            default:
                break;
        }
    }

    private void doCmdResultCloud(JSONObject resultCmd) {
        try {
            String taskId = resultCmd.getString("messageId");

            JSONObject info = resultCmd.getJSONObject("info");
            int code = -1;
            if (!info.containsKey("result")) {
                code = -1;
            } else {
                code = !"ok".equals(info.getString("result")) ? -1 : 0;
            }
            String msg = info.containsKey("result") ? info.getString("result") : "未知";
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            MachineCmdResultDto machineCmdResultDto = new MachineCmdResultDto(code, msg, taskId, "", resultCmd.toJSONString());
            notifyAccessControlService.machineCmdResult(machineCmdResultDto);
        } catch (Exception e) {
            logger.error("上报执行命令失败", e);
        }
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
    public String httpFaceResult(MachineDto machineDto,String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);

            JSONObject info = body.getJSONObject("info");




            String userId = info.containsKey("PersonUUID") ? info.getString("PersonUUID") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setUserId(userId);
                machineFaceDto.setMachineId(machineDto.getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                }

            }


            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(body.getString("SanpPic").replace("data:image/jpeg;base64,", ""));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDto.getMachineCode());
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

    @Override
    public String heartbeat(String data, String machineCode) {
        JSONObject info = JSONObject.parseObject(data).getJSONObject("info");

        //设备ID
        machineCode = info.getString("facesluiceId");
        String heartBeatTime = null;
        heartBeatTime = info.getString("time");
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        try {
            notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询费用信息
     *
     * @param openDoorDto
     */
    public void freshOwnerFee(OpenDoorDto openDoorDto) {

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


    public void openDoorResult(String data) {


        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject info = JSONObject.parseObject(data).getJSONObject("info");
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(info.getString("facesluiceId"));
            List<MachineDto> machineDtos = notifyAccessControlService.queryMachines(machineDto);

            if (machineDtos.size() < 0) {
                return;//未找到设备
            }

            if (!info.containsKey("pic") || StringUtil.isEmpty(info.getString("pic"))) {
                JSONObject param = new JSONObject();
                param.put("operator", "PushAck");
                param.put("messageId", SeqUtil.getId());

                JSONObject result = new JSONObject();
                result.put("PushAckType", "2");
                result.put("SnapOrRecordID", info.getString("RecordID"));
                param.put("info", result);
                MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

                return;
            }

            String userId = info.containsKey("customId") ? info.getString("customId") : "";
            String userName = info.containsKey("persionName") ? info.getString("persionName") : "";


            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(info.getString("pic").replace("data:image/jpeg;base64,", ""));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setSimilarity(info.getString("similarity1"));
            openDoorDto.setMachineCode(machineDtos.get(0).getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setIdNumber(info.getString("idCard"));
            openDoorDto.setTel(info.getString("tel"));
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);


            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

            JSONObject param = new JSONObject();
            param.put("operator", "PushAck");
            param.put("messageId", SeqUtil.getId());

            JSONObject result = new JSONObject();
            result.put("PushAckType", "2");
            result.put("SnapOrRecordID", info.getString("RecordID"));
            param.put("info", result);
            MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }

    }




    /**
     * 重启
     */
    public void setUiTitle(MachineDto machineDto) {

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
     * @param logId    日志ID
     * @param resParam 返回报文
     * @param state    状态
     */
    private void updateLog(String logId, String resParam, String state) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setLogId(logId);
        operateLogDto.setResParam(resParam);
        operateLogDto.setState(state);
        notifyAccessControlService.saveOrUpdateOperateLog(operateLogDto);
    }

}
