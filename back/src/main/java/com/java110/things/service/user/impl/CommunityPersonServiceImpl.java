package com.java110.things.service.user.impl;


import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICommunityPersonServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.CommunityPersonDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.user.ICommunityPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CommunityPersonServiceImpl
 * @Description TODO 小区人员管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("communityPersonServiceImpl")
public class CommunityPersonServiceImpl implements ICommunityPersonService {

    @Autowired
    private ICommunityPersonServiceDao communityPersonServiceDao;

    @Autowired
    private ICommunityService communityServiceImpl;

    /**
     * 查询小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int page = communityPersonDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            communityPersonDto.setPage((page - 1) * communityPersonDto.getRow());
        }
        long count = communityPersonServiceDao.getCommunityPersonCount(communityPersonDto);
        int totalPage = (int) Math.ceil((double) count / (double) communityPersonDto.getRow());
        List<CommunityPersonDto> communityPersonDtoList = null;
        if (count > 0) {
            communityPersonDtoList = communityPersonServiceDao.getCommunityPersons(communityPersonDto);
            freshUserFace(communityPersonDtoList);
        } else {
            communityPersonDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, communityPersonDtoList);
        return resultDto;
    }

    /**
     * 刷新人脸地址
     *
     * @param communityPersonDtos
     */
    private void freshUserFace(List<CommunityPersonDto> communityPersonDtos) {
        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");

        for (CommunityPersonDto communityPersonDto : communityPersonDtos) {
            communityPersonDto.setFacePath(faceUrl + communityPersonDto.getFacePath());
        }
    }

    /**
     * 查询小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    @Override
    public List<CommunityPersonDto> queryCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int page = communityPersonDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            communityPersonDto.setPage((page - 1) * communityPersonDto.getRow());
        }
        List<CommunityPersonDto> communityPersonDtoList = null;
        communityPersonDtoList = communityPersonServiceDao.getCommunityPersons(communityPersonDto);
        return communityPersonDtoList;
    }

    @Override
    public int saveCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int count = communityPersonServiceDao.saveCommunityPerson(communityPersonDto);
        return count;
    }

    /**
     * 修改小区人员信息
     *
     * @param communityPersonDto 小区人员对象
     * @return
     */
    @Override
    public int updateCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int count = communityPersonServiceDao.updateCommunityPerson(communityPersonDto);
        return count;
    }

    @Override
    public int deleteCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        communityPersonDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = communityPersonServiceDao.updateCommunityPerson(communityPersonDto);
        return count;
    }


}
