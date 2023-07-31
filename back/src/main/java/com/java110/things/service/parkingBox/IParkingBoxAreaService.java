package com.java110.things.service.parkingBox;

import com.java110.things.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.things.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName IMappingService
 * @Description TODO 停车场相关接口服务类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IParkingBoxAreaService {


    /**
     * 保存停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto saveParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception;

    /**
     * 修改停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto updateParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @throws Exception
     */
    ResultDto getParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    List<ParkingBoxAreaDto> queryParkingBoxAreas(ParkingBoxAreaDto parkingBoxAreaDto);

    /**
     * 删除停车场
     *
     * @param parkingBoxAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto deleteParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception;

}
