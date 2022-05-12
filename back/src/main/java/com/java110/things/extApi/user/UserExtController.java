/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.extApi.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.service.user.IUserFaceService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户对外 控制类
 * <p>
 * 完成小区添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/user")
public class UserExtController extends BaseController {

    @Autowired
    IMachineService machineServiceImpl;


    @Autowired
    private IUserFaceService userFaceServiceImpl;

    /**
     * 添加用户信息
     * <p>
     *
     * @param reqParam {
     *                 "userId": "702020042194860037",
     *                 "faceBase64": "base64",
     *                 "startTime": "2020-12-01 00:00:00",
     *                 "endTime": "2020-12-31 00:00:00",
     *                 "name": "张三",
     *                 "idNumber": "63216111111111111",
     *                 "machineCode": "101010"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addUser", method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "userId", "未包含用户ID");
        Assert.hasKeyAndValue(reqJson, "faceBase64", "未包含人脸信息");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含开始时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含结束时间");
        Assert.hasKeyAndValue(reqJson, "name", "未包含用户名称");
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(reqJson.getString("machineCode"));
        UserFaceDto userFaceDto = BeanConvertUtil.covertBean(reqJson, UserFaceDto.class);
        ResultDto result = userFaceServiceImpl.saveUserFace(machineDto, userFaceDto);
        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改用户信息
     * <p>
     *
     * @param reqParam {
     *                 "userId": "702020042194860037",
     *                 "faceBase64": "base64",
     *                 "startTime": "2020-12-01 00:00:00",
     *                 "endTime": "2020-12-31 00:00:00",
     *                 "name": "张三",
     *                 "idNumber": "63216111111111111",
     *                 "machineCode": "101010"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateUser", method = RequestMethod.POST)
    public ResponseEntity<String> updateUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "userId", "未包含用户ID");
        Assert.hasKeyAndValue(reqJson, "faceBase64", "未包含人脸信息");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含开始时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含结束时间");
        Assert.hasKeyAndValue(reqJson, "name", "未包含用户名称");
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(reqJson.getString("machineCode"));
        UserFaceDto userFaceDto = BeanConvertUtil.covertBean(reqJson, UserFaceDto.class);
        ResultDto result = userFaceServiceImpl.updateUserFace(machineDto, userFaceDto);
        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除用户信息
     * <p>
     *
     * @param reqParam {
     *                 "userId": "702020042194860037",
     *                 "machineCode": "101010"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteUser", method = RequestMethod.POST)
    public ResponseEntity<String> deleteUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "userId", "未包含用户ID");
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(reqJson.getString("machineCode"));

        HeartbeatTaskDto heartbeatTaskDto = new HeartbeatTaskDto();
        heartbeatTaskDto.setTaskid(reqJson.getString("userId"));
        ResultDto result = userFaceServiceImpl.deleteUserFace(machineDto, heartbeatTaskDto);
        return ResultDto.createResponseEntity(result);
    }

    /**
     * 清空用户信息
     * <p>
     *
     * @param reqParam {
     *                 "machineCode": "101010"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/clearUser", method = RequestMethod.POST)
    public ResponseEntity<String> clearUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "userId", "未包含用户ID");
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(reqJson.getString("machineCode"));

        HeartbeatTaskDto heartbeatTaskDto = new HeartbeatTaskDto();
        heartbeatTaskDto.setTaskid(reqJson.getString("userId"));
        ResultDto result = userFaceServiceImpl.deleteUserFace(machineDto, heartbeatTaskDto);
        return ResultDto.createResponseEntity(result);
    }
}
