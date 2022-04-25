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
package com.java110.things.api.token;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.app.AppDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.service.app.IAppService;
import com.java110.things.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CommunityController
 * @Description TODO 获取token 接口类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/extApi/auth")
public class TokenController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private IAppService appServiceImpl;

    /**
     * 登录
     * <p>
     *
     * @param appId 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getAccessToken", method = RequestMethod.GET)
    public ResponseEntity<String> getAccessToken(@RequestParam(value = "appId") String appId,
                                                 @RequestParam(value = "appSecure") String appSecure) throws Exception {

        AppDto appDto = new AppDto();
        appDto.setAppId(appId);
        appDto.setAppSecret(appSecure);
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        if (appDtos == null || appDtos.size() < 1) {
            return ResultDto.error("获取accessToken 失败");
        }
        Map<String, String> info = new HashMap();
        info.put("appId", appId);
        info.put(AuthenticationFactory.LOGIN_USER_ID, appId);
        String accessToken = AuthenticationFactory.createAndSaveToken(info);
        appDto.setAccessToken(accessToken);
        appDto.setUpdateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        appServiceImpl.updateApp(appDto);

        JSONObject paramOut = new JSONObject();
        paramOut.put("access_token", accessToken);
        paramOut.put("expires_in", 7200);

        return ResultDto.createResponseEntity(paramOut);
    }

}
