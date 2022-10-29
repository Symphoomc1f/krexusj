package com.java110.things.service.manufacturer;

import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IMappingService
 * @Description TODO 映射服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IManufacturerService {


    /**
     * 获取厂商信息
     *
     * @param manufacturerDto 厂商信息
     * @return
     * @throws Exception
     */
    ResultDto getManufacturer(ManufacturerDto manufacturerDto) throws Exception;

    /**
     * 选择该协议为 默认协议
     * @param manufacturerDto 厂商信息
     * @return
     * @throws Exception
     */
    ResultDto startManufacturer(ManufacturerDto manufacturerDto) throws Exception;


}
