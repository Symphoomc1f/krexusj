package com.java110.things.adapt.accessControl.smt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
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
import com.java110.things.quartz.accessControl.AddUpdateFace;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import com.tendcent.face.sdk.client.TdxSdkClient;
import com.tendcent.face.sdk.client.dto.FaceDto;
import com.tendcent.face.sdk.client.dto.HostInfoDto;
import com.tendcent.face.sdk.client.dto.PersonDto;
import com.tendcent.face.sdk.client.resp.TdxSdkResponse;
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
 * 视美通sdk
 */
@Service("smtHttpAssessControlProcessAdapt")
public class SmtHttpAssessControlProcessAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(SmtHttpAssessControlProcessAdapt.class);

    private static final String DEFAULT_PORT = "8090"; //端口

    @Autowired
    private IMachineService machineServiceImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    public static final String CMD_ADD_FACE = "/face/create"; // 创建人脸


    public static final String CMD_OPEN_DOOR = "/device/openDoorControl"; // 开门


    public static final String CMD_ADD_USER = "/person/create"; // 添加人员


    public static final String CMD_DELETE_FACE = "/person/delete"; //删除人脸

    public static final String SN = "{sn}";

    @Override
    public void initAssessControlProcess() {
        logger.debug("初始化是配置器");
    }

    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        //设置回调地址
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//设备号
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        String url = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SET_IDENTIFY_CALLBACK"); //设备识别回调地址
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.setIdentifyCallback(hostInfo,deviceKey, secret,url);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse.toString());

        //设置心跳地址
        url = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SET_HEARTBEAT_CALLBACK");
        tdxSdkResponse=TdxSdkClient.setHeartbeatUrl(hostInfo,deviceKey, secret,url);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse.toString());

        if ("000".equals(tdxSdkResponse.getCode())) {

            return new ResultDto(ResultDto.SUCCESS , tdxSdkResponse.getMsg());
        }else{
            return new ResultDto(ResultDto.ERROR, "初始化设备失败");
        }
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//设备号
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        String personId=userFaceDto.getUserId();

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.findPerson(hostInfo,deviceKey, secret,personId);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse.toString());

        if(!tdxSdkResponse.toString().equals("000")){
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }
        return personId;
    }

   /*
     * @param machineDto  硬件信息
     * @param userFaceDto 用户人脸信息
     * @return
     */
    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//设备号
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        PersonDto personDto=new PersonDto();
        personDto.setExpireTime(0l);
        personDto.setId(userFaceDto.getUserId());
        personDto.setIdcardNum(userFaceDto.getIdNumber());
        personDto.setName(userFaceDto.getName());

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.addPerson(hostInfo,deviceKey, secret,personDto);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse.toString());

        FaceDto faceDto=new FaceDto();
        faceDto.setFaceId(userFaceDto.getUserId());
        faceDto.setPersonId(userFaceDto.getUserId());
        faceDto.setImgBase64(userFaceDto.getFaceBase64());
        tdxSdkResponse=TdxSdkClient.addFace(hostInfo,deviceKey, secret,faceDto);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE, deviceKey, tdxSdkResponse.toString());

        return new ResultDto(tdxSdkResponse.getCode()=="200" ? ResultDto.SUCCESS : ResultDto.ERROR, tdxSdkResponse.getMsg());


    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

       return null;
//        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("msg"));
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//设备号
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        String personId=heartbeatTaskDto.getTaskid();

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.delFace(hostInfo,deviceKey, secret,personId);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse.toString());

        if(tdxSdkResponse.toString().equals("000")){
            return new ResultDto( ResultDto.SUCCESS , tdxSdkResponse.getMsg());
        }
        return new ResultDto( ResultDto.ERROR , tdxSdkResponse.getMsg());

    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {


        return null;
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


    }


    @Override
    public void restartMachine(MachineDto machineDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//设备号
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        TdxSdkResponse tdxSdkResponse=TdxSdkClient.rebootDevice(hostInfo,deviceKey,secret);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, deviceKey, tdxSdkResponse.toString());
    }

    @Override
    public void openDoor(MachineDto machineDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//设备号
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        JSONObject param = new JSONObject();
        param.put("secret", secret);
        param.put("deviceKey", deviceKey);
        param.put("hostInfo", hostInfo);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        TdxSdkResponse tdxSdkResponse=TdxSdkClient.openDoor(hostInfo,deviceKey,secret);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, deviceKey, tdxSdkResponse.toString());
    }

    /**
     *{
     *     "standard":"37.3",
     *     "searchScore":"81.65185",
     *     "ip":"192.168.204.167",
     *     "deviceKey":"8DA5A59CF625F562",
     *     "type":"face_0",
     *     "token":"D9E2CCDAC4A5D68B4BCE830150DFA4F8",
     *     "personName":"贾琼公司号",
     *     "path":"http://192.168.204.167:8090/snapshot/20210227/1614393774845.jpg",
     *     "imgBase64":"",
     *     "temperature":"-1.0",
     *     "personId":"1883349468",
     *     "time":"1614393774982",
     *     "temperatureState":"1",
     *     "livenessScore":"78.943634",
     *     "mask":"-1"
     * }
     * @param machineDto
     * @param data 这个为设备人脸推送协议，请参考设备协议文档
     * @return
     */

    @Override
    public String httpFaceResult(MachineDto machineDto,String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);
            String type=body.getString("type");
            String userId = body.containsKey("personId") ? body.getString("personId") : "";
            String userName =body.getString("personName");
            if("face_0".equals(type)){
                OpenDoorDto openDoorDto = new OpenDoorDto();
                openDoorDto.setFace(body.getString("imgBase64").replace("data:image/jpeg;base64,", ""));
                openDoorDto.setUserName(userName);
                openDoorDto.setHat("3");
                openDoorDto.setMachineCode(body.getString("deviceKey"));//machineDto.getMachineCode());
                openDoorDto.setMachineCode(body.getString("deviceKey"));//machineDto.getMachineCode());
                openDoorDto.setUserId(userId);
                openDoorDto.setOpenId(SeqUtil.getId());
                openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
//                openDoorDto.setSimilarity(info.getString("Similarity1"));
                freshOwnerFee(openDoorDto);
                notifyAccessControlService.saveFaceResult(openDoorDto);
            }
//            if (!StringUtils.isEmpty(userId)) {
//                MachineFaceDto machineFaceDto = new MachineFaceDto();
//                machineFaceDto.setUserId(userId);
//                machineFaceDto.setMachineId(machineDto.getMachineId());
//                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
//                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
//                    userName = machineFaceDtos.get(0).getName();
//                }
//
//            }
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
    public String heartbeat(String data, String machineCode) throws Exception {
        JSONObject info = JSONObject.parseObject(data);

        //设备ID  8DA5A59CF625F562   192.168.204.167   氧仕多视美通测试1
        //String machineCode = info.getString("deviceKey");
        String heartBeatTime = null;
        heartBeatTime = info.getString("time");
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        JSONObject resultParam = new JSONObject();
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
