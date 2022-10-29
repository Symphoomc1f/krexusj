package com.java110.things.dao;

import com.java110.things.entity.cityArea.CityAreaDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ICityAreaServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ICityAreaServiceDao {

    /**
     * 查询城市编码信息
     *
     * @param cityAreaDto 城市编码信息
     * @return
     */
    List<CityAreaDto> getCityAreas(CityAreaDto cityAreaDto);


}
