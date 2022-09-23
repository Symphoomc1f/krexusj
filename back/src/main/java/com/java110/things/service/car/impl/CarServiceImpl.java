package com.java110.things.service.car.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICarServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @Autowired
    private IMachineService machineService;

    /**
     * 添加小区信息
     *
     * @param carDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveCar(CarDto carDto) throws Exception {

        //第三方平台
        addTransactorOtherCar(carDto);

        int count = carServiceDao.saveCar(carDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    private void addTransactorOtherCar(CarDto carDto) throws Exception {
        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carDto.getPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        for (MachineDto tmpMachineDto : machineDtos) {
            CarProcessFactory.getCarImpl(tmpMachineDto.getHmId()).addCar(tmpMachineDto, carDto);
        }
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

        if (carDto.getPage() != PageDto.DEFAULT_PAGE) {
            carDto.setPage((carDto.getPage() - 1) * carDto.getRow());
        }
        long count = carServiceDao.getCarCount(carDto);
        int totalPage = (int) Math.ceil((double) count / (double) carDto.getRow());
        List<CarDto> carDtoList = null;
        if (count > 0) {
            carDtoList = carServiceDao.getCars(carDto);
            //刷新人脸地
        } else {
            carDtoList = new ArrayList<>();
        }

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, carDtoList);
        return resultDto;
    }

    @Override
    public List<CarDto> queryCars(CarDto carDto) throws Exception {
        if (carDto.getPage() != PageDto.DEFAULT_PAGE) {
            carDto.setPage((carDto.getPage() - 1) * carDto.getRow());
        }
        List<CarDto> carDtoList = null;

        carDtoList = carServiceDao.getCars(carDto);
        //刷新人脸地
        return carDtoList;

    }

    @Override
    public ResultDto updateCarByMachine(CarDto carDto) throws Exception {

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
    public ResultDto updateCar(CarDto carDto) throws Exception {
        //修改传送第三方平台
        updateTransactorOtherCar(carDto);
        int count = carServiceDao.updateCar(carDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    private void updateTransactorOtherCar(CarDto carDto) throws Exception {
        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carDto.getPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        CarDto tmpCarDto = new CarDto();
        tmpCarDto.setCarId(carDto.getCarId());
        List<CarDto> carDtos = queryCars(tmpCarDto);
        Assert.listOnlyOne(carDtos, "未找到车辆信息");

        Date preTime = carDtos.get(0).getEndTime();

        double month = dayCompare(preTime, carDto.getEndTime());

        carDto.setCycles(month);

        for (MachineDto tmpMachineDto : machineDtos) {
            CarProcessFactory.getCarImpl(tmpMachineDto.getHmId()).updateCar(tmpMachineDto, carDto);
        }
    }


    private void deleteTransactorOtherCar(CarDto carDto) throws Exception {
        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carDto.getPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }
        for (MachineDto tmpMachineDto : machineDtos) {
            CarProcessFactory.getCarImpl(tmpMachineDto.getHmId()).deleteCar(tmpMachineDto, carDto);
        }
    }

    /**
     * 计算2个日期之间相差的  以年、月、日为单位，各自计算结果是多少
     * 比如：2011-02-02 到  2017-03-02
     * 以年为单位相差为：6年
     * 以月为单位相差为：73个月
     * 以日为单位相差为：2220天
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static double dayCompare(Date fromDate, Date toDate) {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        int result = to.get(Calendar.MONTH) - from.get(Calendar.MONTH);
        int month = (to.get(Calendar.YEAR) - from.get(Calendar.YEAR)) * 12;

        result = result + month;
        Calendar newFrom = Calendar.getInstance();
        newFrom.setTime(fromDate);
        newFrom.add(Calendar.MONTH, result);

        long t1 = newFrom.getTimeInMillis();
        long t2 = to.getTimeInMillis();
        long days = (t2 - t1) / (24 * 60 * 60 * 1000);

        BigDecimal tmpDays = new BigDecimal(days);
        BigDecimal monthDay = new BigDecimal(30);

        return tmpDays.divide(monthDay, 2, RoundingMode.HALF_UP).doubleValue() + result;
    }

    @Override
    public ResultDto deleteCar(CarDto carDto) throws Exception {
        deleteTransactorOtherCar(carDto);
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
