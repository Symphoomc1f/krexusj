package com.java110.things.Controller.mapping;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.mapping.MappingDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.mapping.IMappingService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MappingController
 * @Description TODO 映射控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/mapping")
public class MappingController extends BaseController {

    @Autowired
    private IMappingService mappingServiceImpl;

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveMapping", method = RequestMethod.POST)
    public ResponseEntity<String> saveMapping(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "name", "请求报文中未包含小区名称");
        Assert.hasKeyAndValue(paramObj, "domain", "请求报文中未包含域");
        Assert.hasKeyAndValue(paramObj, "key", "请求报文中未包含键");
        Assert.hasKeyAndValue(paramObj, "value", "请求报文中未包含值");


        ResultDto resultDto = mappingServiceImpl.saveMapping(BeanConvertUtil.covertBean(paramObj, MappingDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @param id 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMappings", method = RequestMethod.GET)
    public ResponseEntity<String> getMappings(@RequestParam int page,
                                              @RequestParam int row,
                                              @RequestParam(name = "id", required = false) String id,
                                              @RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "domain", required = false) String domain,
                                              @RequestParam(name = "key", required = false) String key
    ) throws Exception {


        MappingDto mappingDto = new MappingDto();
        mappingDto.setPage(page);
        mappingDto.setRow(row);
        mappingDto.setId(id);
        mappingDto.setName(name);
        mappingDto.setDomain(domain);
        mappingDto.setKey(key);

        ResultDto resultDto = mappingServiceImpl.getMapping(mappingDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMapping", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMapping(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "id", "请求报文中未包含映射");

        ResultDto resultDto = mappingServiceImpl.deleteMapping(BeanConvertUtil.covertBean(paramObj, MappingDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateMapping", method = RequestMethod.POST)
    public ResponseEntity<String> updateMapping(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "id", "请求报文中未包含小区名称");
        Assert.hasKeyAndValue(paramObj, "name", "请求报文中未包含小区名称");
        Assert.hasKeyAndValue(paramObj, "domain", "请求报文中未包含域");
        Assert.hasKeyAndValue(paramObj, "key", "请求报文中未包含键");
        Assert.hasKeyAndValue(paramObj, "value", "请求报文中未包含值");


        ResultDto resultDto = mappingServiceImpl.updateMapping(BeanConvertUtil.covertBean(paramObj, MappingDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/freshMapping", method = RequestMethod.POST)
    public ResponseEntity<String> freshMapping(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);
        MappingDto mappingDto = new MappingDto();
        ResultDto resultDto = mappingServiceImpl.freshMapping(mappingDto);
        return super.createResponseEntity(resultDto);
    }
}
