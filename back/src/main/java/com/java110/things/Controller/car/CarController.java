package com.java110.things.Controller.car;

import com.java110.things.Controller.BaseController;
import com.java110.things.adapt.car.ICarInoutService;
import com.java110.things.adapt.car.ICarProcess;
import com.java110.things.adapt.car.ICarService;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.response.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
                                               @RequestParam(name = "inoutType", required = false) String inoutType) throws Exception {

        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setPage(page);
        carInoutDto.setRow(row);
        carInoutDto.setCarNum(carNum);
        carInoutDto.setInoutType(inoutType);
        ResultDto resultDto = carInoutService.getCarInout(carInoutDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param carNum 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getNeedPayOrder", method = RequestMethod.GET)
    public ResponseEntity<String> getNeedPayOrder(@RequestParam int page,
                                                  @RequestParam int row,
                                                  @RequestParam(name = "carNum", required = false) String carNum) throws Exception {

        CarDto carDto = new CarDto();
        carDto.setPage(page);
        carDto.setRow(row);
        carDto.setCarNum(carNum);

        String data = aiCarSocketProcessAdapt.getNeedPayOrder();
        ResultDto resultDto = new ResultDto(0, "成功", data);
        return super.createResponseEntity(resultDto);
    }


}
