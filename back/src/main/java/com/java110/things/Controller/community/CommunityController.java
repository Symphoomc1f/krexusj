package com.java110.things.Controller.community;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName CommunityController
 * @Description TODO 小区信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/community")
public class CommunityController extends BaseController {

    @Autowired
    private ICommunityService communityServiceImpl;

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> saveCommunity(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区编码");

        Assert.hasKeyAndValue(paramObj, "name", "请求报文中未包含小区名称");
        CommunityDto communityDto = BeanConvertUtil.covertBean(paramObj, CommunityDto.class);
        communityDto.setAppId(super.getAppId(request));
        ResultDto resultDto = communityServiceImpl.saveCommunity(communityDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @param communityId 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getCommunitys", method = RequestMethod.GET)
    public ResponseEntity<String> getCommunitys(@RequestParam String communityId) throws Exception {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(communityId);

        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCommunity(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含硬件ID");

        ResultDto resultDto = communityServiceImpl.deleteCommunity(BeanConvertUtil.covertBean(paramObj, CommunityDto.class));
        return super.createResponseEntity(resultDto);
    }
}
