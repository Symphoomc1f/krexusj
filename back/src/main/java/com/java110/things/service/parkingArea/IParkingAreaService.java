package com.java110.things.service.parkingArea;

import com.java110.things.entity.parkingArea.ParkingAreaDto;
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
public interface IParkingAreaService {


    /**
     * 保存停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto saveParkingArea(ParkingAreaDto parkingAreaDto) throws Exception;

    /**
     * 修改停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto updateParkingArea(ParkingAreaDto parkingAreaDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @throws Exception
     */
    ResultDto getParkingArea(ParkingAreaDto parkingAreaDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    List<ParkingAreaDto> queryParkingAreas(ParkingAreaDto parkingAreaDto);

    /**
     * 删除停车场
     *
     * @param parkingAreaDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto deleteParkingArea(ParkingAreaDto parkingAreaDto) throws Exception;

    /**
     * 岗亭对应停车场信息
     * @param parkingBoxDto
     * @return
     */
    List<ParkingAreaDto> queryParkingAreasByBox(ParkingBoxDto parkingBoxDto);
}
