package com.java110.things.service.mapping.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IMappingServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.mapping.MappingDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.mapping.IMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName MappingServiceImpl
 * @Description TODO 映射管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("mappingServiceImpl")
public class MappingServiceImpl implements IMappingService {

    @Autowired
    private IMappingServiceDao mappingServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加映射信息
     *
     * @param mappingDto 映射对象
     * @return
     */
    @Override
    public ResultDto saveMapping(MappingDto mappingDto) throws Exception {
        MappingDto tmpMappingDto = new MappingDto();
        tmpMappingDto.setDomain(mappingDto.getDomain());
        tmpMappingDto.setKey(mappingDto.getKey());
        long tmpExistsCount = mappingServiceDao.getMappingCount(tmpMappingDto);
        if (tmpExistsCount > 0) {
            throw new ServiceException(Result.SYS_ERROR, "已经存在该配置不能重复添加");
        }
        mappingDto.setId(UUID.randomUUID().toString());
        int count = mappingServiceDao.saveMapping(mappingDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 查询映射信息
     *
     * @param mappingDto 映射信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getMapping(MappingDto mappingDto) throws Exception {
        int page = mappingDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            mappingDto.setPage((page - 1) * mappingDto.getRow());
        }
        long count = mappingServiceDao.getMappingCount(mappingDto);
        int totalPage = (int) Math.ceil((double) count / (double) mappingDto.getRow());
        List<MappingDto> mappingDtoList = null;
        if (count > 0) {
            mappingDtoList = mappingServiceDao.getMappings(mappingDto);
        } else {
            mappingDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, mappingDtoList);
        return resultDto;
    }

    @Override
    public ResultDto deleteMapping(MappingDto mappingDto) throws Exception {
        mappingDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = mappingServiceDao.updateMapping(mappingDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 修改映射信息
     *
     * @param mappingDto 映射对象
     * @return
     */
    @Override
    public ResultDto updateMapping(MappingDto mappingDto) throws Exception {

        int count = mappingServiceDao.updateMapping(mappingDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto freshMapping(MappingDto mappingDto) throws Exception {
        MappingCacheFactory.flushCacheMappings();
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
    }


}
