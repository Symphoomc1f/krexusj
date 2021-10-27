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


    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    void addUpdateFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {


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

        //返回报文
        /**
         * {
         *     "code": 0,
         *     "data": {
         *         "reserved": "772019121580420009",
         *         "groupid": "702019120393220007",
         *         "name": "散散",
         *         "startTime": 1576387548000,
         *         "faceBase64": "UQohF+X49QMFqfYH/y8EMhMvFum2NfQbCiMD2y0kmRLczQzlw7G4ANQjBBMpp9Qq+ucyEQL88/Ll4+n6Bg4eDy4HGgMG7h0cEfnEBUITJjcY2hTT+eEFBdUSBzjR8iXzA/Ed1OP/Ibnh+h4B3dU+DOb9/uTj5PzxM/fXNNEgLjIRDBMb2u4JOvAvHQbtK+UcGAX5+rsE+q0k2AFBHDHp3gAt3hPl/ej38wgF1ObBmLsJ5cXE9DcqO9LzC+EE8v3Z4NMUAA==",
         *         "endTime": 32503651200000,
         *         "idNumber": "",
         *         "userid": "772019121580420009",
         *         "remarks": "HC小区管理系统",
         *         "group": "杭州月轮家园"
         *     },
         *     "message": "success"
         * }
         */

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
            AccessControlProcessFactory.getAssessControlProcessImpl().addFace(machineDto, userFaceDto);
        } else { //调用更新人脸接口
            updateFace(machineDto, userFaceDto);
            AccessControlProcessFactory.getAssessControlProcessImpl().updateFace(machineDto, userFaceDto);
        }
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
    private void updateFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");

        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        ImageFactory.GenerateImage(faceBase, machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");


    }


}
