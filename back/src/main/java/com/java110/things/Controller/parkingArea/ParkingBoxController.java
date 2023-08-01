package com.java110.things.Controller.parkingArea;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.parkingArea.ParkingBoxDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingBox.IParkingBoxService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ParkingBoxController
 * @Description TODO 停车场信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/parkingBox")
public class ParkingBoxController extends BaseController {

    @Autowired
    private IParkingBoxService parkingBoxServiceImpl;

    /**
     * 添加停车场接口类
     *
     * @param param 请求报文 包括停车场 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveParkingBox", method = RequestMethod.POST)
    public ResponseEntity<String> saveParkingBox(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "boxName", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "tempCarIn", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "fee", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "blueCarIn", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "yelowCarIn", "请求报文中未包含停车场编号");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");

        ParkingBoxDto parkingBoxDto = BeanConvertUtil.covertBean(paramObj, ParkingBoxDto.class);
        parkingBoxDto.setBoxId(SeqUtil.getId());

        ResultDto resultDto = parkingBoxServiceImpl.saveParkingBox(parkingBoxDto);

        return super.createResponseEntity(resultDto);
    }

    /**
     * 更细用户
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateParkingBox", method = RequestMethod.POST)
    public ResponseEntity<String> updateParkingBox(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "boxId", "请求报文中未包含停车场ID");
        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含小区信息");
        ResultDto resultDto = parkingBoxServiceImpl.updateParkingBox(BeanConvertUtil.covertBean(paramObj, ParkingBoxDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加停车场接口类
     *
     * @param boxId 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getParkingBoxs", method = RequestMethod.GET)
    public ResponseEntity<String> getParkingBoxs(@RequestParam(value = "boxId", required = false) String boxId,
                                                  @RequestParam(value = "communityId") String communityId,
                                                  HttpServletRequest request) throws Exception {

        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(boxId);
        parkingBoxDto.setCommunityId(communityId);

        ResultDto resultDto = parkingBoxServiceImpl.getParkingBox(parkingBoxDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除停车场 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteParkingBox", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingBox(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "boxId", "请求报文中未包含boxId");

        ResultDto resultDto = parkingBoxServiceImpl.deleteParkingBox(BeanConvertUtil.covertBean(paramObj, ParkingBoxDto.class));
        return super.createResponseEntity(resultDto);
    }
}
