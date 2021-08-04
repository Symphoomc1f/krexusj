package com.java110.things.service.yldMqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.IAssessControlProcess;
import com.java110.things.service.INotifyAccessControlService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.service.yld04.Function;
import com.java110.things.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    //平台名称
    public static final String MANUFACTURER = "YLD_";

    public static final String VERSION = "0.2";

    public static final String CMD_ADD_FACE = "create_face"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "gpio control"; // 开门

    public static final String CMD_REBOOT = "reboot_cam";// 重启设备

    public static final String CMD_UPDATE_FACE = "update_face"; //修改人脸

    public static final String CMD_DELETE_FACE = "delete_face"; //删除人脸

    public static final String CMD_UI_TITLE = "set_ui_title";// 设置名称

    //单设备处理
    public static final String TOPIC_FACE_SN_REQUEST = "face.{sn}.request";

    //识别结果上报
    public static final String TOPIC_FACE_RESPONSE = "face.response";

    //硬件上线上报
    public static final String TOPIC_ONLINE_RESPONSE = "online.response";

    public static final String SN = "{sn}";

    @Override
    public void initAssessControlProcess() {
        Function.Init();
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
    public void addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_ADD_FACE);
        param.put("per_id", userFaceDto.getUserId());
        param.put("face_id", userFaceDto.getUserId());
        param.put("per_name", userFaceDto.getName());
        param.put("idcardNum", userFaceDto.getIdNumber());
        param.put("img_url", "http://39.98.253.100/20200520021223248.jpg");
        param.put("idcardper", userFaceDto.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);

        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());

    }

    @Override
    public void updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        //CMD_UPDATE_FACE
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_UPDATE_FACE);
        param.put("per_id", userFaceDto.getUserId());
        param.put("face_id", userFaceDto.getUserId());
        param.put("per_name", userFaceDto.getName());
        param.put("idcardNum", userFaceDto.getIdNumber());
        param.put("img_url", "\"http://dqfile-1251895221.cos.ap-guangzhou.myqcloud.\n" +
                " com / img / DC681D46EA932574179933CA04F29998A99F2EF9.jpg ");
        param.put("idcardper", userFaceDto.getIdNumber());
        param.put("s_time", START_TIME);
        param.put("e_time", END_TIME);
        param.put("per_type", 0);
        param.put("usr_type", 0);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
    }

    @Override
    public void deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        param.put("type", 0);
        param.put("per_id", heartbeatTaskDto.getTaskinfo());
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
    }

    @Override
    public void clearFace(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_DELETE_FACE);
        param.put("type", 4);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
    }

    /**
     * 扫描设备
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() {
        return null;
    }

    @Override
    public void mqttMessageArrived(String topic, String data) {
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
    }

    @Override
    public void restartMachine(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        param.put("client_id", machineDto.getMachineCode());
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_REBOOT);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
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
            return;
        }
        String machineName = MANUFACTURER + SeqUtil.getMachineSeq();

        //设备上报
        INotifyAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getINotifyAccessControlService();
        machineDto = new MachineDto();
        machineDto.setMachineId(UUID.randomUUID().toString());
        machineDto.setMachineIp("");
        machineDto.setMachineMac(machineCode);
        machineDto.setMachineCode(machineCode);
        machineDto.setMachineName(machineName);
        machineDto.setMachineVersion("v1.0");
        machineDto.setOem("伊兰度");
        notifyAccessControlService.uploadMachine(machineDto);

        //将修改设备名称，方便对应
        param = new JSONObject();
        param.put("client_id", machineCode);
        param.put("cmd_id", SeqUtil.getId());
        param.put("version", VERSION);
        param.put("cmd", CMD_UI_TITLE);
        JSONObject body = new JSONObject();
        body.put("title", machineName);
        param.put("body", body);
        MqttFactory.publish(TOPIC_FACE_SN_REQUEST.replace(SN, machineDto.getMachineCode()), param.toJSONString());
    }

}
