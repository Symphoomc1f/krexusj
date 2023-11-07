package com.java110.things.dao;

import com.java110.things.adapt.accessControl.sxoa.SxCommunityDto;
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
public interface ISxoaCommunityServiceDao {

    /**
     * 保存设备信息
     *
     * @param communityDto 设备信息
     * @return 返回影响记录数
     */
    int saveCommunity(SxCommunityDto communityDto);

    /**
     * 查询设备信息
     * @param communityDto 设备信息
     * @return
     */
    List<SxCommunityDto> getCommunitys(SxCommunityDto communityDto);

}
