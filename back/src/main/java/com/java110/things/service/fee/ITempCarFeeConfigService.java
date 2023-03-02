package com.java110.things.service.fee;

import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.TempCarFeeConfigAttrDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.fee.TempCarPayOrderDto;
import com.java110.things.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName ITempCarFeeConfigService
 * @Description TODO 小区服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface ITempCarFeeConfigService {

    /**
     * 保存临时车费用信息
     *
     * @param tempCarFeeConfigDto 临时车费用信息
     * @return
     * @throws Exception
     */
    ResultDto saveTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;


    /**
     * 获取临时车费用信息
     *
     * @param tempCarFeeConfigDto 临时车费用信息
     * @return
     * @throws Exception
     */
    ResultDto getTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;

    /**
     * 获取临时车费用信息
     *
     * @param tempCarFeeConfigDto 临时车费用信息
     * @return
     * @throws Exception
     */
    List<TempCarFeeConfigDto> queryTempCarFeeConfigs(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;


    public ResultDto updateTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;

    /**
     * 删除设备
     *
     * @param tempCarFeeConfigDto 临时车费用信息
     * @return
     * @throws Exception
     */
    ResultDto deleteTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;

    /**
     * 保存临时车费用信息
     *
     * @param tempCarFeeConfigDto 临时车费用信息
     * @return
     * @throws Exception
     */
    int saveTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigDto) throws Exception;

    /**
     * 保存临时车费用信息
     *
     * @param tempCarFeeConfigAttrDto 临时车费用属性信息
     * @return
     * @throws Exception
     */
    List<TempCarFeeConfigAttrDto> queryTempCarFeeConfigAttrs(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto) throws Exception;

    /**
     * 删除临时车费用信息
     *
     * @param tempCarFeeConfigAttrDto 临时车费用信息
     * @return
     * @throws Exception
     */
    int deleteTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto) throws Exception;

    /**
     * 查询临时车付费订单
     * @param carDto
     * @return
     */
    ResultDto getTempCarFeeOrder(CarDto carDto) throws Exception;

    ResultDto notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto)  throws Exception;
}
