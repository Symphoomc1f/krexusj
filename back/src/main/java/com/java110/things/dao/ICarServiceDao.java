package com.java110.things.dao;

import com.java110.things.entity.car.CarDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ICarServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ICarServiceDao {

    /**
     * 保存设备信息
     *
     * @param carDto 设备信息
     * @return 返回影响记录数
     */
    int saveCar(CarDto carDto);

    /**
     * 查询设备信息
     *
     * @param carDto 设备信息
     * @return
     */
    List<CarDto> getCars(CarDto carDto);

    /**
     * 查询设备总记录数
     *
     * @param carDto 设备信息
     * @return
     */
    long getCarCount(CarDto carDto);

    /**
     * 修改设备信息
     *
     * @param carDto 设备信息
     * @return 返回影响记录数
     */
    int updateCar(CarDto carDto);

    /**
     * 删除指令
     *
     * @param carDto 指令id
     * @return 返回影响记录数
     */
    int delete(CarDto carDto);
}
