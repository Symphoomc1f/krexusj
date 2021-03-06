package com.java110.things.adapt.accessControl.yldComet;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.constant.DEL_PERSON_MODE;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.factory.CometFactory;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.DateUtil;
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
 * 伊兰度 门禁设备 Comet 方式
 */
@Service("yldCometAssessControlProcessAdapt")
public class YldCometAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(YldCometAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    @Autowired
    private IMachineService machineServiceImpl;


    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //comet 通道名称
    public static final String CHANNELNAME = "/cometd";

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
        JSONObject param = new JSONObject();
        param.put("version", VERSION);
        param.put("cmd", CMD_ADD_FACE);
        param.put("per_id", userFaceDto.getUserId());
        param.put("face_id", userFaceDto.getUserId());
        param.put("per_name", userFaceDto.getName());
        param.put("idcardNum", userFaceDto.getIdNumber());
        param.put("img_data", userFaceDto.getFaceBase64());
        param.put("idcardper", userFaceDto.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);

        CometFactory.publish(CHANNELNAME, param.toJSONString());

        String cmdId = SeqUtil.getId();
        saveLog(cmdId, machineDto.getMachineId(), CMD_ADD_FACE, param.toJSONString(), "", "", userFaceDto.getUserId(), userFaceDto.getName());

        return null;
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        //CMD_UPDATE_FACE
        JSONObject param = new JSONObject();
        param.put("version", VERSION);
        param.put("cmd", CMD_UPDATE_FACE);
        param.put("per_id", userFaceDto.getUserId());
        param.put("face_id", userFaceDto.getUserId());
        param.put("per_name", userFaceDto.getName());
        param.put("idcardNum", userFaceDto.getIdNumber());
        param.put("img_data", userFaceDto.getFaceBase64());
        param.put("idcardper", userFaceDto.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);

        CometFactory.publish(CHANNELNAME, param.toJSONString());

        String cmdId = SeqUtil.getId();
        saveLog(cmdId, machineDto.getMachineId(), CMD_UPDATE_FACE, param.toJSONString(), "", "", userFaceDto.getUserId(), userFaceDto.getName());
        return null;
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String cmdId = SeqUtil.getId();

        JSONObject param = new JSONObject();
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        //TODO  type 不确定应该设置为数字 0，1，2，3 还是 字符串
        param.put("type", DEL_PERSON_MODE.DEL_PERSON_ID);
        param.put("per_id", heartbeatTaskDto.getTaskinfo());
        CometFactory.publish(CHANNELNAME, param.toJSONString());

        saveLog(cmdId, machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "", "", heartbeatTaskDto.getTaskinfo(), "");
        return null;
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String cmdId = SeqUtil.getId();

        JSONObject param = new JSONObject();
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        //TODO  type 不确定应该设置为数字 0，1，2，3 还是 字符串
        param.put("type", DEL_PERSON_MODE.DEL_PERSON_ALL);
        CometFactory.publish(CHANNELNAME, param.toJSONString());
        saveLog(cmdId, machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), "");

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

    @Override
    public void restartMachine(MachineDto machineDto) {
        String cmdId = SeqUtil.getId();

        JSONObject param = new JSONObject();
        param.put("version", VERSION);
        param.put("cmd", CMD_REBOOT);
        CometFactory.publish(CHANNELNAME, param.toJSONString());
        saveLog(cmdId, machineDto.getMachineId(), CMD_REBOOT, param.toJSONString(), "");

    }

    @Override
    public void openDoor(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("version", VERSION);
        param.put("cmd", CMD_OPEN_DOOR);
        param.put("ctrl_type", "on");
        CometFactory.publish(CHANNELNAME, param.toJSONString());
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, param.toJSONString(), "");
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
        String heartBeatTime = null;
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG).toString();
    }
}
