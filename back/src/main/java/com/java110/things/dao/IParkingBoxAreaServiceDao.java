package com.java110.things.dao;

import com.java110.things.entity.parkingArea.ParkingBoxAreaDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IParkingBoxAreaServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IParkingBoxAreaServiceDao {

    /**
     * 保存停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return 返回影响记录数
     */
    int saveParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto);

    /**
     * 查询停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return
     */
    List<ParkingBoxAreaDto> getParkingBoxAreas(ParkingBoxAreaDto parkingBoxAreaDto);

    /**
     * 查询停车场总记录数
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return
     */
    long getParkingBoxAreaCount(ParkingBoxAreaDto parkingBoxAreaDto);

    /**
     * 修改停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return 返回影响记录数
     */
    int updateParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto);

    /**
     * 删除指令
     *
     * @param parkingBoxAreaDto 指令id
     * @return 返回影响记录数
     */
    int delete(ParkingBoxAreaDto parkingBoxAreaDto);
}
