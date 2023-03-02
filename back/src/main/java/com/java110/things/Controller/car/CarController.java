package com.java110.things.Controller.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.adapt.car.ICarProcess;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
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
 * @ClassName CommunityController
 * @Description TODO 车辆信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/car")
public class CarController extends BaseController {

    @Autowired
    private ICarService carServiceImpl;

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private ICarProcess aiCarSocketProcessAdapt;


    /**
     * 添加设备接口类
     *
     * @param carNum 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getCars", method = RequestMethod.GET)
    public ResponseEntity<String> getCars(@RequestParam int page,
                                          @RequestParam int row,
                                          @RequestParam(name = "carNum", required = false) String carNum) throws Exception {

        CarDto carDto = new CarDto();
        carDto.setPage(page);
        carDto.setRow(row);
        carDto.setCarNum(carNum);

        ResultDto resultDto = carServiceImpl.getCar(carDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param carNum 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getCarInouts", method = RequestMethod.GET)
    public ResponseEntity<String> getCarInouts(@RequestParam int page,
                                               @RequestParam int row,
                                               @RequestParam(name = "carNum", required = false) String carNum,
                                               @RequestParam(name = "inoutType", required = false) String inoutType,
                                               @RequestParam(name = "state", required = false) String state) throws Exception {

        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setPage(page);
        carInoutDto.setRow(row);
        carInoutDto.setCarNum(carNum);
        carInoutDto.setInoutType(inoutType);
        carInoutDto.setState(state);
        ResultDto resultDto = carInoutService.getCarInout(carInoutDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除车辆信息
     * <p>
     *
     * @param reqParam {
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteCar", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCar(@RequestBody String reqParam) throws Exception {
        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "carId", "未包含外部车辆编码");
        CarDto carDto = BeanConvertUtil.covertBean(reqJson, CarDto.class);
        ResultDto result = carServiceImpl.deleteCar(carDto);
        return ResultDto.createResponseEntity(result);
    }
}
