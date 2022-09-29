package com.java110.things.service.car;

import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName ICarBlackWhiteService
 * @Description TODO 小区服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface ICarBlackWhiteService {

    /**
     * 保存车辆信息
     *
     * @param carBlackWhiteDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto saveCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param carBlackWhiteDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto getCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param carBlackWhiteDto 车辆信息
     * @return
     * @throws Exception
     */
    List<CarBlackWhiteDto> queryCarBlackWhites(CarBlackWhiteDto carBlackWhiteDto) throws Exception;



    public ResultDto updateCarBlackWhiteByMachine(CarBlackWhiteDto carBlackWhiteDto) throws Exception;

    /**
     * 删除设备
     *
     * @param carBlackWhiteDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto deleteCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception;


}
