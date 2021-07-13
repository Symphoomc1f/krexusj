package com.java110.things.service.community.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICommunityServiceDao;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.HttpFactory;
import com.java110.things.service.community.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CommunityServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("communityServiceImpl")
public class CommunityServiceImpl implements ICommunityService {

    @Autowired
    private ICommunityServiceDao communityServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param communityDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveCommunity(CommunityDto communityDto) throws Exception {

        String url = "/api/community.listCommunitys?communityId=" + communityDto.getCommunityId() + "&name=" + communityDto.getName();

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, "", HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResponseConstant.ERROR, responseEntity.getBody());
        }

        JSONObject resultObj = JSONObject.parseObject(responseEntity.getBody().toString());
        if (!resultObj.containsKey("communitys")) {
            return new ResultDto(ResponseConstant.ERROR, "云端校验小区失败，云端返回报文格式错误" + resultObj.toJSONString());
        }

        JSONArray communitys = resultObj.getJSONArray("communitys");
        if (communitys == null || communitys.size() < 1) {
            return new ResultDto(ResponseConstant.ERROR, "输入小区信息错误，云端未找到该小区信息");
        }

        JSONObject communityInfo = communitys.getJSONObject(0);
        communityDto.setAddress(communityInfo.getString("address"));
        communityDto.setCityCode(communityInfo.getString("cityCode"));

        int count = communityServiceDao.saveCommunity(communityDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 查询小区信息
     *
     * @param communityDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getCommunity(CommunityDto communityDto) throws Exception {


        List<CommunityDto> communityDtoList = communityServiceDao.getCommunitys(communityDto);
        int count = 0;
        int totalPage = 1;
        if (communityDtoList != null && communityDtoList.size() > 0) {
            count = communityDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, communityDtoList);
        return resultDto;
    }

    @Override
    public ResultDto deleteCommunity(CommunityDto communityDto) throws Exception {
        communityDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = communityServiceDao.updateCommunity(communityDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


}
