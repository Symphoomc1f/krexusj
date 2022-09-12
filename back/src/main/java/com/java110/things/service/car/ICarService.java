package com.java110.things.service.car;

import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName ICarService
 * @Description TODO 小区服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface ICarService {

    /**
     * 保存车辆信息
     *
     * @param carDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto saveCar(CarDto carDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param carDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto getCar(CarDto carDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param carDto 车辆信息
     * @return
     * @throws Exception
     */
    List<CarDto> queryCars(CarDto carDto) throws Exception;

    /**
     * 修改车辆信息
     *
     * @param carDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto updateCar(CarDto carDto) throws Exception;

    /**
     * 删除设备
     *
     * @param carDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto deleteCar(CarDto carDto) throws Exception;
}
