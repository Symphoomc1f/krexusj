package com.java110.things.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.machine.IMachineFaceService;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
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
public class AddUpdateCar extends BaseCar {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Java110Properties java110Properties;

    @Autowired
    private IMachineFaceService machineFaceService;

    @Autowired
    private ICarService carService;


    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    public void addUpdateCar(HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {


        String url = MappingCacheFactory.getValue("CLOUD_API") + AccessControlConstant.MACHINE_QUERY_USER_INFO;

        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", communityDto.getCommunityId());
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
            String msg = paramOut.containsKey("msg") ? paramOut.getString("msg") : ResponseConstant.NO_RESULT;
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, msg);
        }

        JSONObject data = paramOut.getJSONObject("data");

        UserFaceDto userFaceDto = BeanConvertUtil.covertBean(data, UserFaceDto.class);

        userFaceDto.setTaskId(heartbeatTaskDto.getTaskid());

        Date startTime = new Date(data.getLong("startTime"));

        userFaceDto.setStartTime(DateUtil.getFormatTimeString(startTime, DateUtil.DATE_FORMATE_STRING_A));

        Date endTime = new Date(data.getLong("endTime"));

        userFaceDto.setEndTime(DateUtil.getFormatTimeString(endTime, DateUtil.DATE_FORMATE_STRING_A));
        userFaceDto.setUserId(data.getString("userid"));

        CarProcessFactory.getCarImpl().addAndUpdateCar(userFaceDto);

        //保存数据

        CarDto carDto = new CarDto();
        carDto.setCarId(data.getString("userid"));
        carDto.setCarNum(data.getString("name"));
        carDto.setStartTime(startTime);
        carDto.setEndTime(endTime);
        carDto.setCreateTime(new Date());
        carDto.setCommunityId(communityDto.getCommunityId());

        CarDto tmpCarDto = new CarDto();
        tmpCarDto.setCarId(data.getString("userid"));

        ResultDto resultDto = carService.getCar(tmpCarDto);

        if (resultDto.getTotal() > 0) {
            carService.updateCar(carDto);
        } else {
            carService.saveCar(carDto);
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
