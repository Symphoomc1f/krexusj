package com.java110.things.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.service.IAssessControlProcess;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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



    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    void addUpdateFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {


        String url = java110Properties.getCloudApiUrl() + AccessControlConstant.MACHINE_QUERY_USER_INFO;

        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", machineDto.getMachineCode());
        headers.put("communityId", java110Properties.getCommunityId());

        JSONObject paramIn = new JSONObject();
        paramIn.put("faceid", heartbeatTaskDto.getTaskinfo());

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

        userFaceDto.setUserId(data.getString("userid"));


        //查询 当前用户是否在硬件中存在数据
        String faceId = getAssessControlProcessImpl().getFace(machineDto,userFaceDto);

        //调用新增人脸接口
        if(StringUtil.isEmpty(faceId)){
            getAssessControlProcessImpl().addFee(machineDto,userFaceDto);
        }else{ //调用更新人脸接口
            getAssessControlProcessImpl().updateFee(machineDto,userFaceDto);
        }




    }

}
