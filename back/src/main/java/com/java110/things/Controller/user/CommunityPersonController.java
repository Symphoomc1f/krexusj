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
package com.java110.things.Controller.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.CommunityPersonDto;
import com.java110.things.factory.ImageFactory;
import com.java110.things.service.user.ICommunityPersonService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.UUID;

/**
 * @ClassName CommunityPersonController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/communityPerson")
public class CommunityPersonController extends BaseController {

    @Autowired
    private ICommunityPersonService communityPersonServiceImpl;

    /**
     * 添加小区人员接口类
     *
     * @param param 请求报文 包括小区人员 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveCommunityPerson", method = RequestMethod.POST)
    public ResponseEntity<String> saveCommunityPerson(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "name", "请求报文中未包含人员名称");

        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");

        Assert.hasKeyAndValue(paramObj, "personType", "请求报文中未包含人员类型");

        paramObj.put("personId", UUID.randomUUID().toString());
        CommunityPersonDto communityPersonDto = BeanConvertUtil.covertBean(paramObj, CommunityPersonDto.class);
        if (paramObj.containsKey("photo")) {
            String faceBase = paramObj.getString("photo");
            if (faceBase.contains("base64,")) {
                faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
            }
            ImageFactory.GenerateImage(faceBase, paramObj.getString("communityId") + File.separatorChar + paramObj.getString("personId") + ".jpg");
            communityPersonDto.setFacePath("/" + paramObj.getString("communityId") + "/" + paramObj.getString("personId") + ".jpg");
        }

        int count = communityPersonServiceImpl.saveCommunityPerson(communityPersonDto);

        if (count < 1) {
            return ResultDto.error("保存失败");
        }
        return ResultDto.success();
    }

    /**
     * 修改小区人员接口类
     *
     * @param param 请求报文 包括小区人员 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateCommunityPerson", method = RequestMethod.POST)
    public ResponseEntity<String> updateCommunityPerson(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "personId", "请求报文中未包含硬件人员信息");

        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");

        int count = communityPersonServiceImpl.updateCommunityPerson(BeanConvertUtil.covertBean(paramObj, CommunityPersonDto.class));

        if (count < 1) {
            return ResultDto.error("保存失败");
        }
        return ResultDto.success();
    }

    /**
     * 添加小区人员接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getCommunityPersons", method = RequestMethod.GET)
    public ResponseEntity<String> getCommunityPersons(@RequestParam int page,
                                                      @RequestParam int row,
                                                      @RequestParam(value = "personType", required = false) String personType,
                                                      @RequestParam String communityId) throws Exception {

        //Assert.hasText(personType, "请求报文中未包含小区人员类型");
        CommunityPersonDto communityPersonDto = new CommunityPersonDto();
        communityPersonDto.setPage(page);
        communityPersonDto.setRow(row);
        communityPersonDto.setPersonType(personType);
        communityPersonDto.setCommunityId(communityId);

        ResultDto resultDto = communityPersonServiceImpl.getCommunityPerson(communityPersonDto);
        return ResultDto.createResponseEntity(resultDto);
    }


    /**
     * 删除小区人员 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteCommunityPerson", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCommunityPerson(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "personId", "请求报文中未包含人员ID");
        int count = communityPersonServiceImpl.deleteCommunityPerson(BeanConvertUtil.covertBean(paramObj, CommunityPersonDto.class));
        if (count < 1) {
            return ResultDto.error("保存失败");
        }
        return ResultDto.success();
    }

}
