package com.java110.things.service.manufacturer.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IManufacturerServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.manufacturer.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
