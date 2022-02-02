package com.java110.things.service.car.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICarInoutServiceDao;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarInoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @ClassName CarInoutServiceImpl
 * @Description TODO 车辆进出场服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("carInoutServiceImpl")
public class CarInoutServiceImpl implements ICarInoutService {

    @Autowired
    private ICarInoutServiceDao carInoutServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param carInoutDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveCarInout(CarInoutDto carInoutDto) throws Exception {

        int count = carInoutServiceDao.saveCarInout(carInoutDto);
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
     * @param carInoutDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getCarInout(CarInoutDto carInoutDto) throws Exception {


        List<CarInoutDto> carInoutDtoList = carInoutServiceDao.getCarInouts(carInoutDto);
        int count = 0;
        int totalPage = 1;
        if (carInoutDtoList != null && carInoutDtoList.size() > 0) {
            count = carInoutDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, carInoutDtoList);
        return resultDto;
    }

    @Override
    public ResultDto updateCarInout(CarInoutDto carInoutDto) throws Exception {
        int count = carInoutServiceDao.updateCarInout(carInoutDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto deleteCarInout(CarInoutDto carInoutDto) throws Exception {
        carInoutDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = carInoutServiceDao.updateCarInout(carInoutDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


}
