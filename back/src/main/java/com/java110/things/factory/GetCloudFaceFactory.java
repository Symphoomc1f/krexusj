package com.java110.things.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.SyncGetTaskResultDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.service.machine.IMachineFaceService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GetCloudFaceFactory
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/6 17:13
 * @Version 1.0
 * add by wuxw 2020/6/6
 **/
public class GetCloudFaceFactory {
    static Logger logger = LoggerFactory.getLogger(GetCloudFaceFactory.class);

    /**
     * 心跳云端 接受指令
     *
     * @param machineDto
     */
    public static List<SyncGetTaskResultDto> doHeartbeatCloud(MachineDto machineDto, CommunityDto communityDto) throws Exception {

        RestTemplate restTemplate = ApplicationContextFactory.getBean("restTemplate", RestTemplate.class);
        String url = MappingCacheFactory.getValue("CLOUD_API") + AccessControlConstant.MACHINE_HEARTBEART;
        Map<String, String> headers = new HashMap<>();
        headers.put("command", "gettask");
        headers.put("machinecode", machineDto.getMachineCode());
        headers.put("communityId", communityDto.getCommunityId());
        JSONObject paramIn = new JSONObject();
        paramIn.put("machineCode", machineDto.getMachineCode());
        paramIn.put("devGroup", "default");
        paramIn.put("communityId", communityDto.getCommunityId());
        paramIn.put("name", machineDto.getMachineName());
        paramIn.put("authCode", machineDto.getAuthCode());
        paramIn.put("ip", machineDto.getMachineIp());
        paramIn.put("mac", machineDto.getMachineMac());
        paramIn.put("remarks", "");
        paramIn.put("faceNum", AccessControlProcessFactory.getAssessControlProcessImpl().getFaceNum(machineDto));
        paramIn.put("lastOnTime", DateUtil.getTime());
        paramIn.put("statCode", "");
        paramIn.put("deviceType", machineDto.getMachineTypeCd());
        paramIn.put("versionCode", machineDto.getMachineVersion());

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), headers, HttpMethod.POST);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, responseEntity.getBody());
        }
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("code") || ResponseConstant.SUCCESS != paramOut.getInteger("code")) {
            String msg = paramOut.containsKey("message") ? paramOut.getString("message") : ResponseConstant.NO_RESULT;
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, msg);
        }

        JSONArray data = paramOut.getJSONArray("data");

        List<SyncGetTaskResultDto> syncGetTaskResultDtos = new ArrayList<>();
        SyncGetTaskResultDto syncGetTaskResultDto = null;
        for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
            syncGetTaskResultDto = doHeartbeatCloudResult(machineDto, data.getJSONObject(dataIndex), communityDto, restTemplate);
            syncGetTaskResultDtos.add(syncGetTaskResultDto);
        }

        return syncGetTaskResultDtos;
    }

    /**
     * 心跳结果处理
     *
     * @param commandInfo 指令内容 {
     *                    "taskinfo": "772019121580420009", 任务对象ID
     *                    "taskcmd": 101, 指令编码
     *                    "taskId": "ed06d2329c774474a05475ac6f3d623d"  任务ID
     *                    }
     */
    private static SyncGetTaskResultDto doHeartbeatCloudResult(MachineDto machineDto, JSONObject commandInfo, CommunityDto communityDto, RestTemplate restTemplate) throws Exception {

        Assert.hasKeyAndValue(commandInfo, "taskcmd", "云端返回报文格式错误 未 包含指令编码 taskcmd" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskinfo", "云端返回报文格式错误 未 包含任务内容 taskinfo" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskId", "云端返回报文格式错误 未 包含任务ID taskId" + commandInfo.toJSONString());

        HeartbeatTaskDto heartbeatTaskDto = BeanConvertUtil.covertBean(commandInfo, HeartbeatTaskDto.class);

        UserFaceDto userFaceDto = null;

        SyncGetTaskResultDto syncGetTaskResultDto = new SyncGetTaskResultDto();
        syncGetTaskResultDto.setCmd(commandInfo.getString("taskcmd"));
        syncGetTaskResultDto.setTaskId(commandInfo.getString("taskinfo"));
        syncGetTaskResultDto.setTaskId(commandInfo.getString("taskId"));
        syncGetTaskResultDto.setCommunityDto(communityDto);
        syncGetTaskResultDto.setMachineDto(machineDto);

        switch (commandInfo.getInteger("taskcmd")) {
            case AccessControlConstant.CMD_ADD_UPDATE_FACE:
                userFaceDto = addUpdateFace(machineDto, heartbeatTaskDto, communityDto, restTemplate);
                syncGetTaskResultDto.setUserFaceDto(userFaceDto);
                break;
            case AccessControlConstant.CMD_DELETE_FACE:
                deleteFace(machineDto, heartbeatTaskDto, communityDto);
                break;
            case AccessControlConstant.CMD_CLEAR_ALL_FACE:
                clearFace(machineDto, heartbeatTaskDto, communityDto);
                break;
            default:
                logger.error("不支持的指令", commandInfo.getInteger("taskcmd"));
        }

        return syncGetTaskResultDto;

    }

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    private static UserFaceDto addUpdateFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto, RestTemplate restTemplate) throws Exception {


        String url = MappingCacheFactory.getValue("CLOUD_API") + AccessControlConstant.MACHINE_QUERY_USER_INFO;

        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", machineDto.getMachineCode());
        headers.put("communityId", communityDto.getCommunityId());

        JSONObject paramIn = new JSONObject();
        paramIn.put("faceid", heartbeatTaskDto.getTaskinfo());
        paramIn.put("communityId", communityDto.getCommunityId());

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), headers, HttpMethod.POST);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, responseEntity.getBody());
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("code") || ResponseConstant.SUCCESS != paramOut.getInteger("code")) {
            String msg = paramOut.containsKey("message") ? paramOut.getString("message") : ResponseConstant.NO_RESULT;
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, msg);
        }

        JSONObject data = paramOut.getJSONObject("data");

        UserFaceDto userFaceDto = BeanConvertUtil.covertBean(data, UserFaceDto.class);

        userFaceDto.setTaskId(heartbeatTaskDto.getTaskid());

        Date startTime = new Date(data.getLong("startTime"));

        userFaceDto.setStartTime(DateUtil.getFormatTimeString(startTime, DateUtil.DATE_FORMATE_STRING_A));

        Date endTime = new Date(data.getLong("endTime"));

        if (endTime.getTime() > DateUtil.getDateFromString("2038-01-01", DateUtil.DATE_FORMATE_STRING_B).getTime()) {
            endTime = DateUtil.getDateFromString("2037-12-01", DateUtil.DATE_FORMATE_STRING_B);
        }

        userFaceDto.setEndTime(DateUtil.getFormatTimeString(endTime, DateUtil.DATE_FORMATE_STRING_A));

        userFaceDto.setUserId(data.getString("userid"));


        //查询 当前用户是否在硬件中存在数据
        String faceId = AccessControlProcessFactory.getAssessControlProcessImpl().getFace(machineDto, userFaceDto);

        if (faceId == null) {
            // 从本地磁盘中检查是否有人脸存在
            boolean exists = ImageFactory.existsImage(machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
            faceId = exists ? userFaceDto.getUserId() : null;
        }

        //调用新增人脸接口
        if (StringUtil.isEmpty(faceId)) {
            //存储人脸
            saveFace(machineDto, userFaceDto);
        } else { //调用更新人脸接口
            updateFace(machineDto, userFaceDto);
        }

        return userFaceDto;
    }

    /**
     * 本地存储人脸
     *
     * @param userFaceDto
     */
    private static void saveFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {
        IMachineFaceService machineFaceService = ApplicationContextFactory.getBean("machineFaceServiceImpl", IMachineFaceService.class);
        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        ImageFactory.GenerateImage(faceBase, machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");

        MachineFaceDto machineFaceDto = BeanConvertUtil.covertBean(userFaceDto, MachineFaceDto.class);
        machineFaceDto.setId(SeqUtil.getId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setFacePath("/" + machineDto.getMachineCode() + "/" + userFaceDto.getUserId() + ".jpg");

        //machineFaceDto.set
        machineFaceService.saveMachineFace(machineFaceDto);
    }

    /**
     * 本地修改人脸
     *
     * @param userFaceDto
     */
    private static void updateFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");

        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        ImageFactory.GenerateImage(faceBase, machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");


    }


    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    static void deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {
        IMachineFaceService machineFaceService = ApplicationContextFactory.getBean("machineFaceServiceImpl", IMachineFaceService.class);

        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + heartbeatTaskDto.getTaskinfo() + ".jpg");
        AccessControlProcessFactory.getAssessControlProcessImpl().deleteFace(machineDto, heartbeatTaskDto);

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setTaskId(heartbeatTaskDto.getTaskid());
        //machineFaceDto.set
        machineFaceService.deleteMachineFace(machineFaceDto);
    }

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    static void clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {
        IMachineFaceService machineFaceService = ApplicationContextFactory.getBean("machineFaceServiceImpl", IMachineFaceService.class);

        //清空硬件下的人脸
        ImageFactory.clearImage(machineDto.getMachineCode());

        AccessControlProcessFactory.getAssessControlProcessImpl().clearFace(machineDto, heartbeatTaskDto);

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        //machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setTaskId(heartbeatTaskDto.getTaskid());
        //machineFaceDto.set
        machineFaceService.deleteMachineFace(machineFaceDto);
    }
}
