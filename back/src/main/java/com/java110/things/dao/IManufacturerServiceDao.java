package com.java110.things.dao;

import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.mapping.MappingDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IMappingServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IManufacturerServiceDao {


    long getManufacturerCount(ManufacturerDto manufacturerDto);

    /**
     * 查询厂商信息
     * @param manufacturerDto 厂商信息
     * @return
     */
    List<ManufacturerDto> getManufacturers(ManufacturerDto manufacturerDto);



}
