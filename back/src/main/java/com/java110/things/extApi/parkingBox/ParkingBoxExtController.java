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
package com.java110.things.extApi.parkingBox;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.things.entity.parkingArea.ParkingBoxDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.parkingBox.IParkingBoxService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 岗亭对外 控制类
 * <p>
 * 完成小区添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/parkingBox")
public class ParkingBoxExtController extends BaseController {

    @Autowired
    IParkingBoxService parkingBoxServiceImpl;

    @Autowired
    ICommunityService communityServiceImpl;

    /**
     * 添加停车场信息
     * <p>
     *
     * @param reqParam {
     *                 "parkingBoxCode":""
     *                 parkingBox_name
     *                 parkingBox_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_parkingBox_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addParkingBox", method = RequestMethod.POST)
    public ResponseEntity<String> addParkingBox(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "boxName", "未包含停车场编码");
        Assert.hasKeyAndValue(reqJson, "extBoxId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingBoxDto parkingBoxDto = BeanConvertUtil.covertBean(reqJson, ParkingBoxDto.class);
        parkingBoxDto.setBoxId(SeqUtil.getId());
        parkingBoxDto.setCommunityId(communityDtos.get(0).getCommunityId());

        if (reqJson.containsKey("areas")) {
            JSONArray areas = reqJson.getJSONArray("areas");
            List<ParkingBoxAreaDto> parkingBoxAttrDtos = new ArrayList<>();
            for (int attrIndex = 0; attrIndex < areas.size(); attrIndex++) {
                ParkingBoxAreaDto parkingBoxAttrDto = BeanConvertUtil.covertBean(areas.getJSONObject(attrIndex), ParkingBoxAreaDto.class);
                parkingBoxAttrDtos.add(parkingBoxAttrDto);
            }

            parkingBoxDto.setParkingBoxAreas(parkingBoxAttrDtos);
        }


        ResultDto result = parkingBoxServiceImpl.saveParkingBoxExt(parkingBoxDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 删除停车场信息
     * <p>
     *
     * @param reqParam {
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteParkingBox", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingBox(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extBoxId", "未包含外部岗亭编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");
        ParkingBoxDto tmpParkingBoxDto = new ParkingBoxDto();
        tmpParkingBoxDto.setExtBoxId(reqJson.getString("extBoxId"));
        List<ParkingBoxDto> parkingBoxDtos = parkingBoxServiceImpl.queryParkingBoxs(tmpParkingBoxDto);

        Assert.listOnlyOne(parkingBoxDtos, "未找到停车场信息");

        ParkingBoxDto parkingBoxDto = BeanConvertUtil.covertBean(reqJson, ParkingBoxDto.class);
        parkingBoxDto.setExtBoxId(parkingBoxDtos.get(0).getExtBoxId());
        parkingBoxDto.setBoxId(parkingBoxDtos.get(0).getBoxId());
        ResultDto result = parkingBoxServiceImpl.deleteParkingBox(parkingBoxDto);

        return ResultDto.createResponseEntity(result);
    }

}
