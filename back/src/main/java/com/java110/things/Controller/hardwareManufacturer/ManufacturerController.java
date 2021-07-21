package com.java110.things.Controller.hardwareManufacturer;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.manufacturer.ManufacturerDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.manufacturer.IManufacturerService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ManufacturerController
 * @Description TODO 硬件厂商 控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/manufacturer")
public class ManufacturerController extends BaseController {

    @Autowired
    private IManufacturerService manufacturerServiceImpl;


    /**
     * 添加设备接口类
     *
     * @param hmId 商户ID
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getManufacturers", method = RequestMethod.GET)
    public ResponseEntity<String> getManufacturers(@RequestParam(name = "hmId", required = false) String hmId,
                                                   @RequestParam(name = "hmName", required = false) String hmName,
                                                   @RequestParam(name = "hmType", required = true) String hmType
    ) throws Exception {


        ManufacturerDto manufacturerDto = new ManufacturerDto();

        manufacturerDto.setHmId(hmId);
        manufacturerDto.setHmName(hmName);
        manufacturerDto.setHmType(hmType);

        ResultDto resultDto = manufacturerServiceImpl.getManufacturer(manufacturerDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param param 厂商信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/startManufacturer", method = RequestMethod.POST)
    public ResponseEntity<String> startManufacturers(@RequestBody String param
    ) throws Exception {


        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "hmId", "请求报文中未包含厂商编码");
        Assert.hasKeyAndValue(paramObj, "hmType", "请求报文中未包含厂商类型");

        ResultDto resultDto = manufacturerServiceImpl.startManufacturer(BeanConvertUtil.covertBean(paramObj, ManufacturerDto.class));
        return super.createResponseEntity(resultDto);
    }

}
