package com.java110.things.adapt.car.compute;

import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.car.TempCarFeeConfigAttrDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.car.TempCarFeeResult;
import com.java110.things.factory.TempCarFeeFactory;
import com.java110.things.service.fee.ITempCarFeeConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseComputeTempCarFee implements IComputeTempCarFee {
    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;


    @Override
    public TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto = new TempCarFeeConfigAttrDto();
        tempCarFeeConfigAttrDto.setConfigId(tempCarFeeConfigDto.getConfigId());
        tempCarFeeConfigAttrDto.setCommunityId(tempCarFeeConfigDto.getCommunityId());

        List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigAttrs(tempCarFeeConfigAttrDto);
        TempCarFeeResult result = doCompute(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos);
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarMin(carInoutDto);
        result.setMin(min);
        return result;
    }

    /**
     * 计算 费用
     *
     * @param carInoutDto
     * @param tempCarFeeConfigDto
     * @param tempCarFeeConfigAttrDtos
     * @return
     */
    public abstract TempCarFeeResult doCompute(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto, List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos);


}
