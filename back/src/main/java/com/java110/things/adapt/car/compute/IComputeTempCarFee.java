package com.java110.things.adapt.car.compute;

import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.car.TempCarFeeResult;

/**
 * 计算 临时车 停车费
 */
public interface IComputeTempCarFee {


    /**
     * 临时车停车费计算
     *
     * @param carInoutDto
     * @param tempCarFeeConfigDto
     * @return
     */
    TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception;
}
