package com.java110.things.service.car.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICarServiceDao;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @ClassName CarServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("carServiceImpl")
public class CarServiceImpl implements ICarService {

    @Autowired
    private ICarServiceDao carServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param carDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveCar(CarDto carDto) throws Exception {

        int count = carServiceDao.saveCar(carDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 查询小区信息
     *
     * @param carDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getCar(CarDto carDto) throws Exception {


        List<CarDto> carDtoList = carServiceDao.getCars(carDto);
        int count = 0;
        int totalPage = 1;
        if (carDtoList != null && carDtoList.size() > 0) {
            count = carDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, carDtoList);
        return resultDto;
    }

    @Override
    public ResultDto updateCar(CarDto carDto) throws Exception {
        int count = carServiceDao.updateCar(carDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto deleteCar(CarDto carDto) throws Exception {
        carDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = carServiceDao.updateCar(carDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


}
