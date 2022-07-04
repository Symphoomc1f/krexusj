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
package com.java110.things.extApi.community;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 小区对外 控制类
 * <p>
 * 完成小区添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/community")
public class CommunityExtController extends BaseController {

    @Autowired
    ICommunityService communityServiceImpl;

    /**
     * 添加小区信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC小区",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extCommunityId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> addCommunity(@RequestBody String reqParam, HttpServletRequest request) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "name", "未包含小区名称");
        Assert.hasKeyAndValue(reqJson, "address", "未包含小区地址");
        Assert.hasKeyAndValue(reqJson, "cityCode", "未包含城市编码");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");

        CommunityDto communityDto = BeanConvertUtil.covertBean(reqJson, CommunityDto.class);
        communityDto.setCommunityId(SeqUtil.getId());
        communityDto.setAppId(super.getAppId(request));
        communityDto.setCommunityId(SeqUtil.getId());
        ResultDto result = communityServiceImpl.saveCommunity(communityDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改小区信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC小区",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extCommunityId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> updateCommunity(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "name", "未包含小区名称");
        Assert.hasKeyAndValue(reqJson, "address", "未包含小区地址");
        Assert.hasKeyAndValue(reqJson, "cityCode", "未包含城市编码");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");

        CommunityDto communityDto = BeanConvertUtil.covertBean(reqJson, CommunityDto.class);
        ResultDto result = communityServiceImpl.updateCommunity(communityDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除小区信息
     * <p>
     *
     * @param reqParam {
     *                 "extCommunityId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCommunity(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");

        CommunityDto communityDto = BeanConvertUtil.covertBean(reqJson, CommunityDto.class);
        ResultDto result = communityServiceImpl.deleteCommunity(communityDto);

        return ResultDto.createResponseEntity(result);
    }
}
