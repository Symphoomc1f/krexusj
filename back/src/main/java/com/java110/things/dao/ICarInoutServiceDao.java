package com.java110.things.dao;

import com.java110.things.entity.car.CarInoutDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ICarInoutServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ICarInoutServiceDao {

    /**
     * 保存设备信息
     *
     * @param carInoutDto 设备信息
     * @return 返回影响记录数
     */
    int saveCarInout(CarInoutDto carInoutDto);

    /**
     * 查询设备信息
     *
     * @param carInoutDto 设备信息
     * @return
     */
    List<CarInoutDto> getCarInouts(CarInoutDto carInoutDto);

    /**
     * 查询设备总记录数
     *
     * @param carInoutDto 设备信息
     * @return
     */
    long getCarInoutCount(CarInoutDto carInoutDto);

    /**
     * 修改设备信息
     *
     * @param carInoutDto 设备信息
     * @return 返回影响记录数
     */
    int updateCarInout(CarInoutDto carInoutDto);

    /**
     * 删除指令
     *
     * @param carInoutDto 指令id
     * @return 返回影响记录数
     */
    int delete(CarInoutDto carInoutDto);
}
