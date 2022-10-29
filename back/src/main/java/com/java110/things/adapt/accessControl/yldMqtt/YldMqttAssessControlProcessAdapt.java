package com.java110.things.adapt.accessControl.yldMqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineCmdResultDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 伊兰度 门禁设备 Mqtt 方式
 */
@Service("yldMqttAssessControlProcessAdapt")
public class YldMqttAssessControlProcessAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(YldMqttAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    @Autowired
    private IMachineService machineServiceImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "YLD_";

    public static final String VERSION = "0.2";

    public static final String CMD_ADD_FACE = "create_face"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "gpio control"; // 开门

    public static final String CMD_REBOOT = "reboot_cam";// 重启设备

    public static final String CMD_UPDATE_FACE = "update_face"; //修改人脸

    public static final String CMD_DELETE_FACE = "delete_face"; //删除人脸

    public static final String CMD_UI_TITLE = "set_ui_title";// 设置名称
    public static final String CMD_FACE_SEARCH = "face_search";// 搜素设备

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

        //注册设备上线 topic
        MqttFactory.subscribe("online.response");

        //推送人脸识别结果
        MqttFactory.subscribe("face.response");
    }

    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {


        return null;
    }

    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String cmdId = SeqUtil.getId();
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", userFaceDto.getTaskId());
        param.put("version", VERSION);
        param.put("cmd", CMD_ADD_FACE);
        param.put("per_id", userFaceDto.getUserId());
        param.put("face_id", userFaceDto.getUserId());
        param.put("per_name", userFaceDto.getName());
        param.put("idcardNum", userFaceDto.getIdNumber());
        param.put("img_url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getMachineCode() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        param.put("idcardper", userFaceDto.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(cmdId, machineDto.getMachineId(), CMD_ADD_FACE, param.toJSONString(), "", "", userFaceDto.getUserId(), userFaceDto.getName());

        return null;
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String cmdId = SeqUtil.getId();

        //CMD_UPDATE_FACE
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", userFaceDto.getTaskId());
        param.put("version", VERSION);
        param.put("cmd", CMD_UPDATE_FACE);
        param.put("per_id", userFaceDto.getUserId());
        param.put("face_id", userFaceDto.getUserId());
        param.put("per_name", userFaceDto.getName());
        param.put("idcardNum", userFaceDto.getIdNumber());
        param.put("img_url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getMachineCode() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        param.put("idcardper", userFaceDto.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(cmdId, machineDto.getMachineId(), CMD_UPDATE_FACE, param.toJSONString(), "", "", userFaceDto.getUserId(), userFaceDto.getName());
        return null;
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String cmdId = SeqUtil.getId();

        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", heartbeatTaskDto.getTaskid());
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        param.put("type", 0);
        param.put("per_id", heartbeatTaskDto.getTaskinfo());
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(cmdId, machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "", "", heartbeatTaskDto.getTaskinfo(), "");
        return null;
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", heartbeatTaskDto.getTaskid());
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        param.put("type", 4);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(param.getString("cmd_id"), machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "");
        return null;
    }

    /**
     * 扫描设备
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() throws Exception {

        MachineDto machineDto = new MachineDto();

        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);

        if (resultDto == null || resultDto.getCode() != ResponseConstant.SUCCESS) {
            logger.error("查询设备信息失败" + machineDto.toString());
            return null;
        }

        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        JSONObject param = null;
        for (MachineDto tmpMachineDto : machineDtos) {
            setUiTitle(tmpMachineDto);
        }
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
        switch (cmd) {
            case CMD_ADD_FACE:
                doCmdResultCloud(resultCmd);
                break;
            case CMD_UPDATE_FACE:
                break;
            case CMD_DELETE_FACE:
                break;
            default:
                break;
        }
    }

    private void doCmdResultCloud(JSONObject resultCmd) {
        try {
            String taskId = resultCmd.getString("cmd_id");
            String machineCode = resultCmd.getString("sn");
            int code = -1;
            if (!resultCmd.containsKey("code")) {
                code = -1;
            } else {
                code = resultCmd.getIntValue("code") != 0 ? -1 : 0;
            }
            String msg = resultCmd.containsKey("reply") ? resultCmd.getString("reply") : "";
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            MachineCmdResultDto machineCmdResultDto = new MachineCmdResultDto(code, msg, taskId, machineCode,resultCmd.toJSONString());
            notifyAccessControlService.machineCmdResult(machineCmdResultDto);
        } catch (Exception e) {
            logger.error("上报执行命令失败", e);
        }
    }

    @Override
    public void restartMachine(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_REBOOT);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(param.getString("cmd_id"), machineDto.getMachineId(), CMD_REBOOT, param.toJSONString(), "");

    }

    @Override
    public void openDoor(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_OPEN_DOOR);
        param.put("ctrl_type", "on");
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(param.getString("cmd_id"), machineDto.getMachineId(), CMD_OPEN_DOOR, param.toJSONString(), "");

    }

    /**
     * {
     * "body" : {
     * "code" : 101,
     * "face_imgdata" : "/9j/4AAQSkZJRghDk+tAH/2Q==",
     * "face_imgsize" : 27178,
     * "hat" : 1,
     * "matched" : 91,
     * "model_imgdata" : "/9j/4AAQSkZJRgABAQAAKDKUT/2Q==",
     * "model_imgsize" : 6001,
     * "name" : "吴学文",
     * "per_id" : "772020051963050001",
     * "pic_name" : "9f15b229-0422fd6b_1590397611_91_101.jpg",
     * "role" : 0,
     * "sec" : 1590397611,
     * "sequence" : 2085,
     * "sn" : "9f15b229-0422fd6b",
     * "usec" : 778083
     * },
     * "type" : "face_result"
     * }
     *
     * @param data 这个为设备人脸推送协议，请参考设备协议文档
     * @return
     */
    @Override
    public String httpFaceResult(MachineDto machineDto,String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        try {
            JSONObject param = JSONObject.parseObject(data);

            if (param.containsKey("type") && !FACE_RESULT.equals(param.getString("type"))) {
                return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG).toString();
            }

            JSONObject body = param.getJSONObject("body");
            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(body.getString("face_imgdata"));
            openDoorDto.setUserName(body.containsKey("name") ? body.getString("name") : "");
            openDoorDto.setHat(body.getString("hat"));
            openDoorDto.setMachineCode(body.getString("sn"));
            openDoorDto.setUserId(body.containsKey("per_id") ? body.getString("per_id") : "");
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.containsKey("matched") ? body.getString("matched") : "0");


            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG).toString();
    }

    @Override
    public String heartbeat(String data, String machineCode) throws Exception {
        return null;
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

    /**
     * 开门记录
     *
     * @param data 设备推送结果
     *             {
     *             "type" : "face_result" ,
     *             "body" : {
     *             "e_imgurl" : "http://jupiter-1251895221.cos.ap-guangzhou.myqcloud.
     *             com/20190907/8080078b-c30d4f7b_1567816001_95_101.jpg",
     *             //人脸图url（若识别后不存在全图数据以及上传img_data失败，不推送此字段）
     *             "e_imgsize" : 159792, //人脸图大小
     *             "hat" : 255,
     *             "matched" : 95, //比对结果(100分制)，0：未比对。-1：比对失败。大于0的取值
     *             "name" : "k", //人员姓名
     *             "per_id" : "20190906135824899",
     *             "role" : 1, //人员角色，0：普通人员。 1：白名单人员。 2：黑名单人员
     *             "usec" : 1567816001,
     *             "sn" : "ffffffff" //设备的SN信息
     *             }
     *             }
     */
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
        JSONObject param = JSONObject.parseObject(data);

        String machineCode = param.getString("sn");
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        machineDto.setMachineMac(machineCode);
        ResultDto resultDto = null;
        MqttFactory.subscribe(TOPIC_FACE_SN_RESPONSE.replace(SN, machineDto.getMachineCode()));

        try {
            resultDto = machineServiceImpl.getMachine(machineDto);
        } catch (Exception e) {
            logger.error("查询设备失败", machineDto);
        }

        if (resultDto == null || resultDto.getCode() != ResponseConstant.SUCCESS) {
            logger.error("查询设备信息失败" + machineDto.toString());
            return;
        }

        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();

        if (machineDtos.size() > 0) {
            logger.debug("门禁已经注册过，无需再次注册");
            setUiTitle(machineDtos.get(0));
            return;
        }
        String machineName = MANUFACTURER + SeqUtil.getMachineSeq();

        try {
            //设备上报
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            machineDto = new MachineDto();
            machineDto.setMachineId(UUID.randomUUID().toString());
            machineDto.setMachineIp("设备未上报");
            machineDto.setMachineMac(machineCode);
            machineDto.setMachineCode(machineCode);
            machineDto.setMachineName(machineName);
            machineDto.setMachineVersion("v1.0");
            machineDto.setOem("伊兰度");
            notifyAccessControlService.uploadMachine(machineDto);
            //上报标题
            setUiTitle(machineDto);
        } catch (Exception e) {
            logger.error("上报设备失败", e);
        }


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
