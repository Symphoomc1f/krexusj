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
package com.java110.things.extApi.parkingArea;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.parkingArea.IParkingAreaService;
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
 * 停车场对外 控制类
 * <p>
 * 完成小区添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/parkingArea")
public class ParkingAreaExtController extends BaseController {

    @Autowired
    IParkingAreaService parkingAreaServiceImpl;

    @Autowired
    ICommunityService communityServiceImpl;

    /**
     * 添加停车场信息
     * <p>
     *
     * @param reqParam {
     *                 "parkingAreaCode":""
     *                 parkingArea_name
     *                 parkingArea_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_parkingArea_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addParkingArea", method = RequestMethod.POST)
    public ResponseEntity<String> addParkingArea(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "num", "未包含停车场编码");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingAreaDto parkingAreaDto = BeanConvertUtil.covertBean(reqJson, ParkingAreaDto.class);
        parkingAreaDto.setPaId(SeqUtil.getId());
        parkingAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());

        if (reqJson.containsKey("attrs")) {
            JSONArray attrs = reqJson.getJSONArray("attrs");
            List<ParkingAreaAttrDto> parkingAreaAttrDtos = new ArrayList<>();
            for (int attrIndex = 0; attrIndex < attrs.size(); attrIndex++) {
                ParkingAreaAttrDto parkingAreaAttrDto = BeanConvertUtil.covertBean(attrs.getJSONObject(attrIndex), ParkingAreaAttrDto.class);
                parkingAreaAttrDtos.add(parkingAreaAttrDto);
            }

            parkingAreaDto.setAttrs(parkingAreaAttrDtos);
        }


        ResultDto result = parkingAreaServiceImpl.saveParkingArea(parkingAreaDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改停车场信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC小区",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateParkingArea", method = RequestMethod.POST)
    public ResponseEntity<String> updateParkingArea(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");
        ParkingAreaDto tmpParkingAreaDto = new ParkingAreaDto();
        tmpParkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(tmpParkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        ParkingAreaDto parkingAreaDto = BeanConvertUtil.covertBean(reqJson, ParkingAreaDto.class);
        parkingAreaDto.setPaId(parkingAreaDtos.get(0).getPaId());


        ResultDto result = parkingAreaServiceImpl.updateParkingArea(parkingAreaDto);

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
    @RequestMapping(path = "/deleteParkingArea", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingArea(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");
        ParkingAreaDto tmpParkingAreaDto = new ParkingAreaDto();
        tmpParkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(tmpParkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        ParkingAreaDto parkingAreaDto = BeanConvertUtil.covertBean(reqJson, ParkingAreaDto.class);
        parkingAreaDto.setPaId(parkingAreaDtos.get(0).getPaId());
        ResultDto result = parkingAreaServiceImpl.deleteParkingArea(parkingAreaDto);

        return ResultDto.createResponseEntity(result);
    }

}
