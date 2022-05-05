package com.java110.things.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.machine.IMachineFaceService;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 添加更新人脸
 */
@Component
public class AddUpdateFace extends BaseAccessControl {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Java110Properties java110Properties;

    @Autowired
    private IMachineFaceService machineFaceService;


    public static final String MACHINE_HAS_NOT_FACE = "-1"; // 设备没有人脸


    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    public void addUpdateFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {


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
            String msg = paramOut.containsKey("msg") ? paramOut.getString("msg") : ResponseConstant.NO_RESULT;
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
        String faceId = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).getFace(machineDto, userFaceDto);

        if (faceId == null) {
            // 从本地磁盘中检查是否有人脸存在
            boolean exists = ImageFactory.existsImage(machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
            faceId = exists ? userFaceDto.getUserId() : null;
        }

        ResultDto resultDto = null;
        //调用新增人脸接口
        if (StringUtil.isEmpty(faceId) || MACHINE_HAS_NOT_FACE.equals(faceId)) {
            //存储人脸
            saveFace(machineDto, userFaceDto);
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).addFace(machineDto, userFaceDto);
        } else { //调用更新人脸接口
            updateFace(machineDto, userFaceDto);
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).updateFace(machineDto, userFaceDto);
        }

        if (resultDto == null) {
            return;
        }

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(userFaceDto.getUserId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState(resultDto.getCode() == ResultDto.SUCCESS ? "S" : "F");
        machineFaceDto.setMessage(resultDto.getMsg());

        machineFaceService.updateMachineFace(machineFaceDto);
    }

    /**
     * 本地存储人脸
     *
     * @param userFaceDto
     */
    private void saveFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        String img = ImageFactory.GenerateImage(faceBase, machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
        userFaceDto.setFaceBase64(img);
        MachineFaceDto machineFaceDto = BeanConvertUtil.covertBean(userFaceDto, MachineFaceDto.class);
        machineFaceDto.setId(SeqUtil.getId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setFacePath("/" + machineDto.getMachineCode() + "/" + userFaceDto.getUserId() + ".jpg");
        machineFaceDto.setState("W");
        machineFaceDto.setState("新增人脸待同步设备");


        //machineFaceDto.set
        machineFaceService.saveMachineFace(machineFaceDto);
    }


    /**
     * 本地修改人脸
     *
     * @param userFaceDto
     */
    private void updateFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");

        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        String img = ImageFactory.GenerateImage(faceBase, machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
        userFaceDto.setFaceBase64(img);

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(userFaceDto.getUserId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState("W");
        machineFaceDto.setState("更新人脸待同步设备");

        machineFaceService.updateMachineFace(machineFaceDto);

    }


}
