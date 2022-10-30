package com.java110.things.Controller.parkingArea;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ParkingAreaController
 * @Description TODO 停车场信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/parkingArea")
public class ParkingAreaController extends BaseController {

    @Autowired
    private IParkingAreaService parkingAreaServiceImpl;

    /**
     * 添加停车场接口类
     *
     * @param param 请求报文 包括停车场 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveParkingArea", method = RequestMethod.POST)
    public ResponseEntity<String> saveParkingArea(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "num", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");

        ParkingAreaDto parkingAreaDto = BeanConvertUtil.covertBean(paramObj, ParkingAreaDto.class);

        parkingAreaDto.setPaId(SeqUtil.getId());

        ResultDto resultDto = parkingAreaServiceImpl.saveParkingArea(parkingAreaDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 更细用户
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateParkingArea", method = RequestMethod.POST)
    public ResponseEntity<String> updateParkingArea(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "paId", "请求报文中未包含停车场ID");
        Assert.hasKeyAndValue(paramObj, "num", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");
        ResultDto resultDto = parkingAreaServiceImpl.updateParkingArea(BeanConvertUtil.covertBean(paramObj, ParkingAreaDto.class));
        return super.createResponseEntity(resultDto);
    }



    /**
     * 添加停车场接口类
     *
     * @param paId 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getParkingAreas", method = RequestMethod.GET)
    public ResponseEntity<String> getParkingAreas(@RequestParam(value = "paId",required = false) String paId, @RequestParam(value = "communityId") String communityId,
                                                  HttpServletRequest request) throws Exception {

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(paId);
        parkingAreaDto.setCommunityId(communityId);

        ResultDto resultDto = parkingAreaServiceImpl.getParkingArea(parkingAreaDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除停车场 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteParkingArea", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingArea(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "paId", "请求报文中未包含硬件ID");

        ResultDto resultDto = parkingAreaServiceImpl.deleteParkingArea(BeanConvertUtil.covertBean(paramObj, ParkingAreaDto.class));
        return super.createResponseEntity(resultDto);
    }
}
