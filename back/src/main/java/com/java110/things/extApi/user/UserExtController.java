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
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    ICommunityService communityServiceImpl;

    /**
     * 添加用户信息
     * <p>
     *
     * @param reqParam {
     *                 "machineCode":""
     *                 machine_name
     *                 machine_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_machine_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addUser", method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");
        Assert.hasKeyAndValue(reqJson, "machineName", "未包含设备名称");
        Assert.hasKeyAndValue(reqJson, "machineTypeCd", "未包含设备类型");
        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        machineDto.setMachineId(SeqUtil.getId());
        machineDto.setCommunityId(communityDtos.get(0).getCommunityId());
        if (!reqJson.containsKey("machineVersion")) {
            machineDto.setMachineVersion("v1.0");
        }
        if (!reqJson.containsKey("machineIp")) {
            machineDto.setMachineVersion("192.168.1.1");
        }
        ResultDto result = machineServiceImpl.saveMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改用户信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC小区",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateUser", method = RequestMethod.POST)
    public ResponseEntity<String> updateUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);


        ResultDto result = machineServiceImpl.updateMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除用户信息
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteUser", method = RequestMethod.POST)
    public ResponseEntity<String> deleteUser(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.deleteMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }
}
