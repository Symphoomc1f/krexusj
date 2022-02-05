package com.java110.things.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.CarConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.CarResultDto;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
    public void addUpdateCar(MachineDto machineDto,HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto, int action) throws Exception {


        String url = MappingCacheFactory.getValue("CLOUD_API") + CarConstant.MACHINE_QUERY_CAR_INFO;

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
            String msg = paramOut.containsKey("msg") ? paramOut.getString("msg") : ResponseConstant.NO_RESULT;
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, msg);
        }

        JSONObject data = paramOut.getJSONObject("data");

        CarResultDto carResultDto = BeanConvertUtil.covertBean(data, CarResultDto.class);


        Date startTime = new Date(data.getLong("startTime"));

        carResultDto.setStartTime(DateUtil.getFormatTimeString(startTime, DateUtil.DATE_FORMATE_STRING_A));

        Date endTime = new Date(data.getLong("endTime"));

        carResultDto.setEndTime(DateUtil.getFormatTimeString(endTime, DateUtil.DATE_FORMATE_STRING_A));
        CarDto tmpCarDto = new CarDto();
        tmpCarDto.setCarId(carResultDto.getCarId());
        ResultDto resultDto = carService.getCar(tmpCarDto);

        if (action == CarConstant.CMD_ADD_CAR) {
            CarProcessFactory.getCarImpl().addCar(carResultDto);
        } else {
            if (resultDto.getTotal() < 1) {
                throw new IllegalArgumentException("未找到 车辆记录");
            }
            List<CarDto> carDtoList = (List<CarDto>) resultDto.getData();

            Date preTime = carDtoList.get(0).getEndTime();

            double month = dayCompare(preTime, endTime);

            carResultDto.setCycles(month);

            CarProcessFactory.getCarImpl().updateCar(carResultDto);
        }

        //保存数据

        CarDto carDto = new CarDto();
        carDto.setCarId(carResultDto.getCarId());
        carDto.setCarNum(carResultDto.getCarNum());
        carDto.setStartTime(startTime);
        carDto.setEndTime(endTime);
        carDto.setCreateTime(new Date());
        carDto.setCommunityId(communityDto.getCommunityId());


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


    /**
     * 计算2个日期之间相差的  以年、月、日为单位，各自计算结果是多少
     * 比如：2011-02-02 到  2017-03-02
     * 以年为单位相差为：6年
     * 以月为单位相差为：73个月
     * 以日为单位相差为：2220天
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static double dayCompare(Date fromDate, Date toDate) {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        int result = to.get(Calendar.MONTH) - from.get(Calendar.MONTH);
        int month = (to.get(Calendar.YEAR) - from.get(Calendar.YEAR)) * 12;

        result = result + month;
        Calendar newFrom = Calendar.getInstance();
        newFrom.setTime(fromDate);
        newFrom.add(Calendar.MONTH, result);

        long t1 = newFrom.getTimeInMillis();
        long t2 = to.getTimeInMillis();
        long days = (t2 - t1) / (24 * 60 * 60 * 1000);

        BigDecimal tmpDays = new BigDecimal(days);
        BigDecimal monthDay = new BigDecimal(30);

        return tmpDays.divide(monthDay, 2, RoundingMode.HALF_UP).doubleValue() + result;
    }


}
