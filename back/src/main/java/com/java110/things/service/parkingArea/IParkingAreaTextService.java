package com.java110.things.service.parkingArea;

import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
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
public interface IParkingAreaTextService {


    /**
     * 保存停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto saveParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception;

    /**
     * 修改停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto updateParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @throws Exception
     */
    ResultDto getParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception;

    /**
     * 获取停车场信息
     *
     * @param parkingAreaTextDto 停车场信息
     * @return
     * @throws Exception
     */
    List<ParkingAreaTextDto> queryParkingAreaTexts(ParkingAreaTextDto parkingAreaTextDto);

    /**
     * 删除停车场
     *
     * @param parkingAreaTextDto 停车场信息
     * @return
     * @throws Exception
     */
    ResultDto deleteParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception;

}
