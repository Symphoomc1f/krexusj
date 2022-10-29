package com.java110.things.dao;

import com.java110.things.entity.user.CommunityPersonDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ICommunityPersonServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ICommunityPersonServiceDao {

    /**
     * 保存人员信息
     *
     * @param communityPersonDto 人员信息
     * @return 返回影响记录数
     */
    int saveCommunityPerson(CommunityPersonDto communityPersonDto);

    /**
     * 查询人员信息
     * @param communityPersonDto 人员信息
     * @return
     */
    List<CommunityPersonDto> getCommunityPersons(CommunityPersonDto communityPersonDto);

    /**
     * 查询人员总记录数
     * @param communityPersonDto 人员信息
     * @return
     */
    long getCommunityPersonCount(CommunityPersonDto communityPersonDto);

    /**
     * 修改人员信息
     *
     * @param communityPersonDto 人员信息
     * @return 返回影响记录数
     */
    int updateCommunityPerson(CommunityPersonDto communityPersonDto);

}
