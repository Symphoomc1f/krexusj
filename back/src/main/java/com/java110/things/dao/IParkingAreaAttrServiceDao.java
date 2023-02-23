package com.java110.things.dao;

import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IParkingAreaAttrServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IParkingAreaAttrServiceDao {

    /**
     * 保存停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return 返回影响记录数
     */
    int saveParkingAreaAttr(ParkingAreaAttrDto parkingAreaDto);

    /**
     * 查询停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return
     */
    List<ParkingAreaAttrDto> getParkingAreaAttrs(ParkingAreaAttrDto parkingAreaDto);


    /**
     * 修改停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return 返回影响记录数
     */
    int updateParkingAreaAttr(ParkingAreaAttrDto parkingAreaDto);

    /**
     * 删除指令
     *
     * @param parkingAreaDto 指令id
     * @return 返回影响记录数
     */
    int delete(ParkingAreaAttrDto parkingAreaDto);
}
