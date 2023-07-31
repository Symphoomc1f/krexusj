package com.java110.things.Controller.parkingArea;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingBox.IParkingBoxAreaService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ParkingBoxAreaController
 * @Description TODO 停车场信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/parkingBoxArea")
public class ParkingBoxAreaController extends BaseController {

    @Autowired
    private IParkingBoxAreaService parkingBoxAreaServiceImpl;

    /**
     * 添加停车场接口类
     *
     * @param param 请求报文 包括停车场 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveParkingBoxArea", method = RequestMethod.POST)
    public ResponseEntity<String> saveParkingBoxArea(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "boxId", "请求报文中未包含岗亭");
        Assert.hasKeyAndValue(paramObj, "paId", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");

        ParkingBoxAreaDto parkingBoxAreaDto = BeanConvertUtil.covertBean(paramObj, ParkingBoxAreaDto.class);
        parkingBoxAreaDto.setBoxId(SeqUtil.getId());

        ResultDto resultDto = parkingBoxAreaServiceImpl.saveParkingBoxArea(parkingBoxAreaDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 更细用户
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateParkingBoxArea", method = RequestMethod.POST)
    public ResponseEntity<String> updateParkingBoxArea(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "baId", "请求报文中未包含停车场ID");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");
        ResultDto resultDto = parkingBoxAreaServiceImpl.updateParkingBoxArea(BeanConvertUtil.covertBean(paramObj, ParkingBoxAreaDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加停车场接口类
     *
     * @param boxId 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getParkingBoxAreas", method = RequestMethod.GET)
    public ResponseEntity<String> getParkingBoxAreas(@RequestParam(value = "boxId", required = false) String boxId,
                                                  @RequestParam(value = "communityId") String communityId,
                                                  HttpServletRequest request) throws Exception {

        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setBoxId(boxId);
        parkingBoxAreaDto.setCommunityId(communityId);

        ResultDto resultDto = parkingBoxAreaServiceImpl.getParkingBoxArea(parkingBoxAreaDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除停车场 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteParkingBoxArea", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingBoxArea(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "baId", "请求报文中未包含baId");

        ResultDto resultDto = parkingBoxAreaServiceImpl.deleteParkingBoxArea(BeanConvertUtil.covertBean(paramObj, ParkingBoxAreaDto.class));
        return super.createResponseEntity(resultDto);
    }
}
