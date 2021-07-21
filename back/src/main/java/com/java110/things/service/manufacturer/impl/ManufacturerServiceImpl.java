package com.java110.things.service.manufacturer.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IManufacturerServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.manufacturer.IManufacturerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MappingServiceImpl
 * @Description TODO 映射管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("manufacturerServiceImpl")
public class ManufacturerServiceImpl implements IManufacturerService {
    private Logger logger = LoggerFactory.getLogger(ManufacturerServiceImpl.class);

    @Autowired
    private IManufacturerServiceDao manufacturerServiceDao;


    /**
     * 查询映射信息
     *
     * @param manufacturerDto 厂商信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getManufacturer(ManufacturerDto manufacturerDto) throws Exception {
        int page = manufacturerDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            manufacturerDto.setPage((page - 1) * manufacturerDto.getRow());
        }
        long count = manufacturerServiceDao.getManufacturerCount(manufacturerDto);
        int totalPage = (int) Math.ceil((double) count / (double) manufacturerDto.getRow());
        List<ManufacturerDto> manufacturerDtoList = null;
        if (count > 0) {
            manufacturerDtoList = manufacturerServiceDao.getManufacturers(manufacturerDto);
        } else {
            manufacturerDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, manufacturerDtoList);
        return resultDto;
    }


    @Transactional
    @Override
    public ResultDto startManufacturer(ManufacturerDto manufacturerDto) throws Exception {
        //1.0 将所有门禁协议刷为 不是默认协议
        ManufacturerDto tmpManufacturerDto = new ManufacturerDto();
        tmpManufacturerDto.setHmType(manufacturerDto.getHmType());
        tmpManufacturerDto.setDefaultProtocol("F");
        long cnt = manufacturerServiceDao.updateManufacturer(tmpManufacturerDto);
        if (cnt < 1) {
            logger.error("启用协议失败" + JSONObject.toJSONString(tmpManufacturerDto));
            throw new ServiceException(Result.SYS_ERROR, "启用失败");
        }
        //2.0 将当前协议修改为默认协议
        ManufacturerDto tempManufacturerDto = new ManufacturerDto();
        tempManufacturerDto.setHmType(manufacturerDto.getHmType());
        tempManufacturerDto.setDefaultProtocol("T");
        tempManufacturerDto.setHmId(manufacturerDto.getHmId());
        long tempCnt = manufacturerServiceDao.updateManufacturer(tempManufacturerDto);
        if (tempCnt < 1) {
            logger.error("启用协议失败" + JSONObject.toJSONString(tmpManufacturerDto));
            throw new ServiceException(Result.SYS_ERROR, "启用失败");
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
    }


}
