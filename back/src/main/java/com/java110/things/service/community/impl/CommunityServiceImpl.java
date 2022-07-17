package com.java110.things.service.community.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICommunityServiceDao;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.community.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
        int count = communityServiceDao.saveCommunity(communityDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateCommunity(CommunityDto communityDto) throws Exception {
        int count = communityServiceDao.updateCommunity(communityDto);
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

    /**
     * 查询小区
     *
     * @param communityDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<CommunityDto> queryCommunitys(CommunityDto communityDto) throws Exception {
        List<CommunityDto> communityDtoList = communityServiceDao.getCommunitys(communityDto);
        return communityDtoList;
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
