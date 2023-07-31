package com.java110.things.service.parkingBox;

import com.java110.things.entity.parkingArea.ParkingBoxDto;
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
public interface IParkingBoxService {


    /**
     * 保存停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto saveParkingBox(ParkingBoxDto parkingBoxDto) throws Exception;

    /**
     * 修改停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto updateParkingBox(ParkingBoxDto parkingBoxDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @throws Exception
     */
    ResultDto getParkingBox(ParkingBoxDto parkingBoxDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingBoxDto 停车场信息
     * @return
     * @throws Exception
     */
    List<ParkingBoxDto> queryParkingBoxs(ParkingBoxDto parkingBoxDto);

    /**
     * 删除停车场
     *
     * @param parkingBoxDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto deleteParkingBox(ParkingBoxDto parkingBoxDto) throws Exception;

}
