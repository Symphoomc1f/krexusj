package com.java110.things.accessControl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.IAssessControlProcess;
import com.java110.things.service.manufacturer.IManufacturerService;
import com.java110.things.service.yld04.Yld04AssessControlProcessAdapt;
import com.java110.things.util.Assert;

import java.util.List;

public class BaseAccessControl {

    /**
     * 访问硬件接口
     */
    private IAssessControlProcess assessControlProcessImpl;

    private IManufacturerService manufacturerServiceImpl;

    /**
     * 获取硬件接口对象
     *
     * @return
     */
    protected IAssessControlProcess getAssessControlProcessImpl() throws Exception {

        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerDto tmpManufacturerDto = new ManufacturerDto();
        tmpManufacturerDto.setHmType("1001");
        tmpManufacturerDto.setDefaultProtocol("T");
        ResultDto resultDto = manufacturerServiceImpl.getManufacturer(tmpManufacturerDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, resultDto.getMsg());
        }

        List<ManufacturerDto> manufacturerDtos = (List<ManufacturerDto>) resultDto.getData();

        Assert.listOnlyOne(manufacturerDtos, "当前有多个默认协议或者一个都没有");
        assessControlProcessImpl = ApplicationContextFactory.getBean(manufacturerDtos.get(0).getProtocolImpl(), IAssessControlProcess.class);
        return assessControlProcessImpl;
    }
}
