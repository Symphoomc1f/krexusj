package com.java110.things.adapt.accessControl.luozhi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineCmdResultDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 罗之 派 门禁设备 Mqtt 方式
 */
@Service("luozhiMqttAssessControlProcessAdapt")
public class LuozhiMqttAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(LuozhiMqttAssessControlProcessAdapt.class);
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
    public static final String TOPIC_FACE_SN_REQUEST = "/hiot/{sn}/request_setting";

    //多设备处理
    public static final String TOPIC_FACE_REQUEST = "face.request";

    //接收设备处理
    public static final String TOPIC_FACE_SN_RESPONSE = "/hiot/people_send_reply";

    //识别结果上报
    public static final String TOPIC_FACE_RESPONSE = "/hiot/record_message";

    //硬件上线上报
    public static final String TOPIC_ONLINE_RESPONSE = "/hiot/connection";

    public static final String SN = "{sn}";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";

    public static final String FACE_RESULT = "identification_record";

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
    public String qrCode(MachineDto machineDto, String param) {
        JSONObject paramObj = JSONObject.parseObject(param);
        String qrCode = paramObj.getString("content");
        ResultDto resultDto = super.checkQRCode(qrCode, machineDto.getMachineCode());
        JSONObject paramOut = new JSONObject();
        paramOut.put("result", 0);
        paramOut.put("result", "Success");
        JSONObject data = new JSONObject();
        data.put("opendoor", resultDto.getCode() == ResultDto.SUCCESS ? 1 : 0);
        paramOut.put("data", data);
        return paramOut.toJSONString();
    }

    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        JSONObject param = new JSONObject();
        param.put("confirmation_topic", "/hiot/people_send_reply");
        param.put("message_uuid", userFaceDto.getTaskId());
        param.put("message_id", "0");
        JSONArray datas = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("age", 0);
        data.put("person_uuid", userFaceDto.getUserId());
        data.put("name", userFaceDto.getName());
        data.put("product_key", userFaceDto.getIdNumber());
        data.put("image_url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        data.put("gender", 0);
        data.put("card", "");
        datas.add(data);
        param.put("data", datas);
        MqttFactory.publish("/hiot/{sn}/add_data".replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(userFaceDto.getTaskId(), machineDto.getMachineId(), CMD_ADD_FACE, param.toJSONString(), "", "", userFaceDto.getUserId(), userFaceDto.getName());

        return new ResultDto(ResultDto.SUCCESS, "推送成功");
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        JSONObject param = new JSONObject();
        param.put("confirmation_topic", "/hiot/people_send_reply");
        param.put("message_uuid", SeqUtil.getId());
        param.put("message_id", "0");
        JSONObject data = new JSONObject();
        data.put("clear_all", false);
        data.put("person_uuids", new String[]{userFaceDto.getUserId()});
        param.put("data", data);
        MqttFactory.publish("/hiot/{sn}/del_data".replace(SN, machineDto.getMachineCode()), param.toJSONString());


        param = new JSONObject();
        param.put("confirmation_topic", "/hiot/people_send_reply");
        param.put("message_uuid", userFaceDto.getTaskId());
        param.put("message_id", "0");
        JSONArray datas = new JSONArray();
        data = new JSONObject();
        data.put("age", 0);
        data.put("person_uuid", userFaceDto.getUserId());
        data.put("name", userFaceDto.getName());
        data.put("product_key", userFaceDto.getIdNumber());
        data.put("image_url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        data.put("gender", 0);
        data.put("card", "");
        datas.add(data);
        param.put("data", datas);
        MqttFactory.publish("/hiot/{sn}/add_data".replace(SN, machineDto.getMachineCode()), param.toJSONString());
        return new ResultDto(ResultDto.SUCCESS, "推送成功");

    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        JSONObject param = new JSONObject();
        param.put("confirmation_topic", "/hiot/people_send_reply");
        param.put("message_uuid", heartbeatTaskDto.getTaskid());
        param.put("message_id", "0");
        JSONObject data = new JSONObject();
        data.put("clear_all", false);
        data.put("person_uuids", new String[]{heartbeatTaskDto.getTaskinfo()});
        param.put("data", data);
        MqttFactory.publish("/hiot/{sn}/del_data".replace(SN, machineDto.getMachineCode()), param.toJSONString());

        saveLog(heartbeatTaskDto.getTaskid(), machineDto.getMachineId(), "/hiot/{sn}/del_data", param.toJSONString(), "", "", heartbeatTaskDto.getTaskinfo(), "未知");
        return new ResultDto(ResultDto.SUCCESS, "推送成功");

    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        JSONObject param = new JSONObject();
        param.put("confirmation_topic", "/hiot/people_send_reply");
        param.put("message_uuid", heartbeatTaskDto.getTaskid());
        param.put("message_id", "0");
        JSONObject data = new JSONObject();
        data.put("clear_all", true);
        param.put("data", data);
        MqttFactory.publish("/hiot/{sn}/del_data".replace(SN, machineDto.getMachineCode()), param.toJSONString());
        saveLog(heartbeatTaskDto.getTaskid(), machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "", "", heartbeatTaskDto.getTaskinfo(), "");
        return new ResultDto(ResultDto.SUCCESS, "推送成功");
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

        doCmdResultCloud(resultCmd);

    }

    private void doCmdResultCloud(JSONObject resultCmd) {
        try {
            String taskId = resultCmd.getString("message_uuid");
            int code = -1;
            if (!resultCmd.containsKey("code")) {
                code = -1;
            } else if (resultCmd.getIntValue("code") == 200) {
                code = 0;
            } else {
                code = resultCmd.getIntValue("code");
            }
            String msg = resultCmd.getString("message_info");
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
        param.put("confirmation_topic", "/hio/device_cmd_reply");
        param.put("message_uuid", SeqUtil.getId());
        param.put("message_id", "0");
        JSONObject data = new JSONObject();
        data.put("cmd_type", "reboot_device");
        param.put("data", data);
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

    public void openDoorResult(String data) {


        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject param = JSONObject.parseObject(data);
            if (param.containsKey("response_type") && !FACE_RESULT.equals(param.getString("response_type"))) {
                return;
            }
            JSONObject body = param.getJSONObject("body");
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(body.getString("sn"));
            List<MachineDto> machineDtos = notifyAccessControlService.queryMachines(machineDto);

            if (machineDtos.size() < 0) {
                return;//未找到设备
            }


            String userId = body.containsKey("per_id") ? body.getString("per_id") : "-1";
            String userName = body.containsKey("name") ? body.getString("name") : "未知人员";

            if (StringUtil.isEmpty(userId)) {
                userId = "-1";
            }


            OpenDoorDto openDoorDto = new OpenDoorDto();

            openDoorDto.setFace(body.getString("img_data"));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat(body.getString("hat"));
            openDoorDto.setMachineCode(body.getString("sn"));
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.containsKey("matched") ? body.getString("matched") : "0");
            openDoorDto.setIdNumber(body.getString("idCard"));
            openDoorDto.setTel("11111111111");

            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }

    }

    @Override
    public String httpFaceResult(MachineDto machineDto, String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        try {
            JSONObject param = JSONObject.parseObject(data);

            if (param.containsKey("type") && !FACE_RESULT.equals(param.getString("type"))) {
                return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG).toString();
            }

            JSONObject body = param.getJSONObject("data");
            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(body.getString("snap_image"));
            openDoorDto.setUserName(body.containsKey("name") ? body.getString("name") : "");
            openDoorDto.setHat("1");
            openDoorDto.setMachineCode(body.getString("mac_address"));
            openDoorDto.setUserId(body.containsKey("person_uuid") ? body.getString("person_uuid") : "");
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.containsKey("recog_result") && "1".equals(body.getString("recog_result")) ? "100" : "0");


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
     * 设备上线
     *
     * @param data {
     *             "cmd": "mqtt_online",
     *             "sn": "fffffff",
     *             "result": "mqtt is online"
     *             }
     */
    protected void machineOnline(String data) {
        JSONObject param = JSONObject.parseObject(data);

        String machineCode = param.getString("mac_address");
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        machineDto.setMachineMac(machineCode);
        ResultDto resultDto = null;
        MqttFactory.subscribe(TOPIC_FACE_SN_RESPONSE.replace(SN, machineDto.getMachineCode()));
        MqttFactory.subscribe("/hiot/record_message");

        if (!"1".equals(param.getString("online"))) {
            return;
        }

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

        } catch (Exception e) {
            logger.error("上报设备失败", e);
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


}
