package com.java110.things.service.hc;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.response.ResultDto;

/**
 * 调用HC小区管理系统
 * 这里设计的目的是为了解决异步调用问题
 *
 * @ClassName ICarCallHcService
 * @Description TODO
 * @Author wuxw
 * @Date 2021/1/18 20:50
 * @Version 1.0
 * add by wuxw 2021/1/18
 **/
public interface ICarCallHcService {

    /**
     * 车辆进出场上报
     *
     * @param carInoutDto
     * @return
     */
    void carInout(CarInoutDto carInoutDto) throws Exception;

    void notifyTempCarFeeOrder(CarInoutDto carInoutDto) throws Exception;
}
