package com.java110.things.factory;

import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.service.manufacturer.IManufacturerService;
import com.java110.things.util.Assert;

import java.util.List;

/**
 * @ClassName AssessControlProcessFactory
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 21:00
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class AccessControlProcessFactory {


    /**
     * 获取硬件接口对象
     *
     * @return
     */
    public static IAssessControlProcess getAssessControlProcessImpl(String hmId) throws Exception {

        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerDto tmpManufacturerDto = new ManufacturerDto();
        tmpManufacturerDto.setHmType("1001");
        tmpManufacturerDto.setHmId(hmId);
        ResultDto resultDto = manufacturerServiceImpl.getManufacturer(tmpManufacturerDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, resultDto.getMsg());
        }

        List<ManufacturerDto> manufacturerDtos = (List<ManufacturerDto>) resultDto.getData();

        Assert.listOnlyOne(manufacturerDtos, "当前有多个默认协议或者一个都没有");
        IAssessControlProcess assessControlProcessImpl = ApplicationContextFactory.getBean(manufacturerDtos.get(0).getProtocolImpl(), IAssessControlProcess.class);
        return assessControlProcessImpl;
    }

}
