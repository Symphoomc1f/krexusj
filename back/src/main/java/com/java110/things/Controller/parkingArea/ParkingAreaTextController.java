package com.java110.things.Controller.parkingArea;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingArea.IParkingAreaTextService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ParkingAreaTextController
 * @Description TODO 停车场信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/parkingAreaText")
public class ParkingAreaTextController extends BaseController {

    @Autowired
    private IParkingAreaTextService parkingAreaTextServiceImpl;

    /**
     * 添加停车场接口类
     *
     * @param param 请求报文 包括停车场 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveParkingAreaText", method = RequestMethod.POST)
    public ResponseEntity<String> saveParkingAreaText(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "num", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");
        ParkingAreaTextDto parkingAreaTextDto = BeanConvertUtil.covertBean(param, ParkingAreaTextDto.class);

        ResultDto resultDto = parkingAreaTextServiceImpl.saveParkingAreaText(parkingAreaTextDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 更细用户
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateParkingAreaText", method = RequestMethod.POST)
    public ResponseEntity<String> updateParkingAreaText(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "paId", "请求报文中未包含停车场ID");
        Assert.hasKeyAndValue(paramObj, "num", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");
        ResultDto resultDto = parkingAreaTextServiceImpl.updateParkingAreaText(BeanConvertUtil.covertBean(paramObj, ParkingAreaTextDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加停车场接口类
     *
     * @param paId 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getParkingAreaTexts", method = RequestMethod.GET)
    public ResponseEntity<String> getParkingAreaTexts(@RequestParam(value = "paId", required = false) String paId,
                                                      @RequestParam(value = "communityId") String communityId,
                                                      HttpServletRequest request) throws Exception {

        ParkingAreaTextDto parkingAreaTextDto = new ParkingAreaTextDto();
        parkingAreaTextDto.setPaId(paId);
        parkingAreaTextDto.setCommunityId(communityId);

        ResultDto resultDto = parkingAreaTextServiceImpl.getParkingAreaText(parkingAreaTextDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除停车场 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteParkingAreaText", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingAreaText(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "paId", "请求报文中未包含硬件ID");

        ResultDto resultDto = parkingAreaTextServiceImpl.deleteParkingAreaText(BeanConvertUtil.covertBean(paramObj, ParkingAreaTextDto.class));
        return super.createResponseEntity(resultDto);
    }
}
