package com.java110.things.dao;

import com.java110.things.entity.parkingArea.ParkingAreaDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IParkingAreaServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IParkingAreaServiceDao {

    /**
     * 保存停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return 返回影响记录数
     */
    int saveParkingArea(ParkingAreaDto parkingAreaDto);

    /**
     * 查询停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return
     */
    List<ParkingAreaDto> getParkingAreas(ParkingAreaDto parkingAreaDto);

    /**
     * 查询停车场总记录数
     *
     * @param parkingAreaDto 停车场信息
     * @return
     */
    long getParkingAreaCount(ParkingAreaDto parkingAreaDto);

    /**
     * 修改停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return 返回影响记录数
     */
    int updateParkingArea(ParkingAreaDto parkingAreaDto);

    /**
     * 删除指令
     *
     * @param parkingAreaDto 指令id
     * @return 返回影响记录数
     */
    int delete(ParkingAreaDto parkingAreaDto);
}
