package com.java110.things.dao;

import com.java110.things.entity.parkingArea.ParkingBoxDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IParkingBoxServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IParkingBoxServiceDao {

    /**
     * 保存停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @return 返回影响记录数
     */
    int saveParkingBox(ParkingBoxDto parkingBoxDto);

    /**
     * 查询停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @return
     */
    List<ParkingBoxDto> getParkingBoxs(ParkingBoxDto parkingBoxDto);

    /**
     * 查询停车场总记录数
     *
     * @param parkingBoxDto 停车场信息
     * @return
     */
    long getParkingBoxCount(ParkingBoxDto parkingBoxDto);

    /**
     * 修改停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @return 返回影响记录数
     */
    int updateParkingBox(ParkingBoxDto parkingBoxDto);

    /**
     * 删除指令
     *
     * @param parkingBoxDto 指令id
     * @return 返回影响记录数
     */
    int delete(ParkingBoxDto parkingBoxDto);
}
