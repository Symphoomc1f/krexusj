package com.java110.things.service.mapping;

import com.java110.things.entity.mapping.MappingDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IMappingService
 * @Description TODO 映射服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IMappingService {

    /**
     * 保存设备信息
     *
     * @param mappingDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveMapping(MappingDto mappingDto) throws Exception;

    /**
     * 获取设备信息
     *
     * @param mappingDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto getMapping(MappingDto mappingDto) throws Exception;

    /**
     * 删除设备
     *
     * @param mappingDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto deleteMapping(MappingDto mappingDto) throws Exception;


    /**
     * 修改映射信息
     *
     * @param mappingDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto updateMapping(MappingDto mappingDto) throws Exception;
}
