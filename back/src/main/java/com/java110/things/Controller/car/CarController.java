package com.java110.things.Controller.car;

import com.java110.things.Controller.BaseController;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarProcess;
import com.java110.things.service.car.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CommunityController
 * @Description TODO 小区信息控制类
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
    @RequestMapping(path = "/getNeedPayOrder", method = RequestMethod.GET)
    public ResponseEntity<String> getNeedPayOrder(@RequestParam int page,
                                          @RequestParam int row,
                                          @RequestParam(name = "carNum", required = false) String carNum) throws Exception {

        CarDto carDto = new CarDto();
        carDto.setPage(page);
        carDto.setRow(row);
        carDto.setCarNum(carNum);

         aiCarSocketProcessAdapt.getNeedPayOrder();
         ResultDto resultDto = new ResultDto(0,"成功");
        return super.createResponseEntity(resultDto);
    }




}
