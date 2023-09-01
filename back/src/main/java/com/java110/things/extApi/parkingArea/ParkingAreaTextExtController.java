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
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.service.parkingArea.IParkingAreaTextService;
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
@RequestMapping(path = "/extApi/parkingAreaText")
public class ParkingAreaTextExtController extends BaseController {

    @Autowired
    IParkingAreaTextService parkingAreaTextServiceImpl;

    @Autowired
    ICommunityService communityServiceImpl;

    @Autowired
    IParkingAreaService parkingAreaServiceImpl;

    /**
     * 添加停车场信息
     * <p>
     *
     * @param reqParam {
     *                 "parkingAreaTextCode":""
     *                 parkingAreaText_name
     *                 parkingAreaText_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_parkingAreaText_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addParkingAreaText", method = RequestMethod.POST)
    public ResponseEntity<String> addParkingAreaText(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "typeCd", "未包含类型");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        parkingAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");


        //查询 问候语是否存在
        ParkingAreaTextDto parkingAreaTextDto = new ParkingAreaTextDto();
        parkingAreaTextDto.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingAreaTextDto.setCommunityId(communityDtos.get(0).getCommunityId());
        parkingAreaTextDto.setTypeCd(reqJson.getString("typeCd"));
        List<ParkingAreaTextDto> parkingAreaTextDtos = parkingAreaTextServiceImpl.queryParkingAreaTexts(parkingAreaTextDto);
        parkingAreaTextDto = BeanConvertUtil.covertBean(reqJson, ParkingAreaTextDto.class);
        parkingAreaTextDto.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingAreaTextDto.setCommunityId(communityDtos.get(0).getCommunityId());
        ResultDto result = null;
        if(parkingAreaTextDtos == null || parkingAreaTextDtos.size() < 1){
            parkingAreaTextDto.setTextId(SeqUtil.getId());
            result = parkingAreaTextServiceImpl.saveParkingAreaText(parkingAreaTextDto);
        }else{
            parkingAreaTextDto.setTextId(parkingAreaTextDtos.get(0).getTextId());
            result = parkingAreaTextServiceImpl.updateParkingAreaText(parkingAreaTextDto);
        }

        return ResultDto.createResponseEntity(result);
    }

}
