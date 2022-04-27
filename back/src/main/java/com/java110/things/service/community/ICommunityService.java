package com.java110.things.service.community;

import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName ICommunityService
 * @Description TODO 小区服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface ICommunityService {

    /**
     * 保存小区信息
     *
     * @param communityDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveCommunity(CommunityDto communityDto) throws Exception;
    /**
     * 修改小区信息
     *
     * @param communityDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto updateCommunity(CommunityDto communityDto) throws Exception;

    /**
     * 获取设备信息
     *
     * @param communityDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto getCommunity(CommunityDto communityDto) throws Exception;

    /**
     * 删除设备
     *
     * @param communityDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto deleteCommunity(CommunityDto communityDto) throws Exception;
}
