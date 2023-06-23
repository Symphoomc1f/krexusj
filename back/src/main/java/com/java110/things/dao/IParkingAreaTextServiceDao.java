package com.java110.things.dao;

import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IParkingAreaTextServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IParkingAreaTextServiceDao {

    /**
     * 保存停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @return 返回影响记录数
     */
    int saveParkingAreaText(ParkingAreaTextDto parkingAreaTextDto);

    /**
     * 查询停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @return
     */
    List<ParkingAreaTextDto> getParkingAreaTexts(ParkingAreaTextDto parkingAreaTextDto);

    /**
     * 查询停车场总记录数
     *
     * @param parkingAreaTextDto 停车场信息
     * @return
     */
    long getParkingAreaTextCount(ParkingAreaTextDto parkingAreaTextDto);

    /**
     * 修改停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @return 返回影响记录数
     */
    int updateParkingAreaText(ParkingAreaTextDto parkingAreaTextDto);

    /**
     * 删除指令
     *
     * @param parkingAreaTextDto 指令id
     * @return 返回影响记录数
     */
    int delete(ParkingAreaTextDto parkingAreaTextDto);
}
