package com.java110.things.service.cityArea;

import com.java110.things.entity.cityArea.CityAreaDto;

import java.util.List;


/**
 * 城市地区编码 服务类
 */
public interface ICityAreaService {

    /**
     * 查询城市编码
     *
     * @param cityAreaDto 城市编码对象
     * @return 城市编码列表
     */
    List<CityAreaDto> getCityAreas(CityAreaDto cityAreaDto);
}
