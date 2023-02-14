package com.java110.things.dao;

import com.java110.things.entity.community.CommunityDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ICommunityServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ICommunityServiceDao {

    /**
     * 保存设备信息
     *
     * @param communityDto 设备信息
     * @return 返回影响记录数
     */
    int saveCommunity(CommunityDto communityDto);

    /**
     * 查询设备信息
     * @param communityDto 设备信息
     * @return
     */
    List<CommunityDto> getCommunitys(CommunityDto communityDto);


    /**
     * 修改设备信息
     *
     * @param communityDto 设备信息
     * @return 返回影响记录数
     */
    int updateCommunity(CommunityDto communityDto);

    long getCommunityCount(CommunityDto communityDto);
}
