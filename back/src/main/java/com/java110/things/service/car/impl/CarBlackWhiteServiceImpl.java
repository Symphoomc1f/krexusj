package com.java110.things.service.car.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICarBlackWhiteServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.service.car.ICarBlackWhiteService;
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
 * @ClassName CarBlackWhiteServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("carBlackWhiteServiceImpl")
public class CarBlackWhiteServiceImpl implements ICarBlackWhiteService {

    @Autowired
    private ICarBlackWhiteServiceDao carServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMachineService machineService;

    /**
     * 添加小区信息
     *
     * @param carBlackWhiteDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception {
        ResultDto resultDto = null;
        //第三方平台
        resultDto = addTransactorOtherCarBlackWhite(carBlackWhiteDto);
        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }


        int count = carServiceDao.saveCarBlackWhite(carBlackWhiteDto);

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    private ResultDto addTransactorOtherCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception {
        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carBlackWhiteDto.getExtPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "设备不存在");
        }


        if(MachineDto.MACHINE_TYPE_CAR.equals(machineDtos.get(0).getMachineTypeCd())){
            return new ResultDto(ResultDto.SUCCESS,"成功");
        }

        ResultDto resultDto = null;

        for (MachineDto tmpMachineDto : machineDtos) {
            resultDto = CarProcessFactory.getCarImpl(tmpMachineDto.getHmId()).addCarBlackWhite(tmpMachineDto, carBlackWhiteDto);
        }
        return resultDto;
    }

    /**
     * 查询小区信息
     *
     * @param carBlackWhiteDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception {

        if (carBlackWhiteDto.getPage() != PageDto.DEFAULT_PAGE) {
            carBlackWhiteDto.setPage((carBlackWhiteDto.getPage() - 1) * carBlackWhiteDto.getRow());
        }
        long count = carServiceDao.getCarBlackWhiteCount(carBlackWhiteDto);
        int totalPage = (int) Math.ceil((double) count / (double) carBlackWhiteDto.getRow());
        List<CarBlackWhiteDto> carBlackWhiteDtoList = null;
        if (count > 0) {
            carBlackWhiteDtoList = carServiceDao.getCarBlackWhites(carBlackWhiteDto);
            //刷新人脸地
        } else {
            carBlackWhiteDtoList = new ArrayList<>();
        }

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, carBlackWhiteDtoList);
        return resultDto;
    }

    @Override
    public List<CarBlackWhiteDto> queryCarBlackWhites(CarBlackWhiteDto carBlackWhiteDto) throws Exception {
        if (carBlackWhiteDto.getPage() != PageDto.DEFAULT_PAGE) {
            carBlackWhiteDto.setPage((carBlackWhiteDto.getPage() - 1) * carBlackWhiteDto.getRow());
        }
        List<CarBlackWhiteDto> carBlackWhiteDtoList = null;

        carBlackWhiteDtoList = carServiceDao.getCarBlackWhites(carBlackWhiteDto);
        //刷新人脸地
        return carBlackWhiteDtoList;

    }

    @Override
    public ResultDto updateCarBlackWhiteByMachine(CarBlackWhiteDto carBlackWhiteDto) throws Exception {

        int count = carServiceDao.updateCarBlackWhite(carBlackWhiteDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    private ResultDto deleteTransactorOtherCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception {
        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carBlackWhiteDto.getExtPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "设备不存在");
        }

        if(MachineDto.MACHINE_TYPE_CAR.equals(machineDtos.get(0).getMachineTypeCd())){
            return new ResultDto(ResultDto.SUCCESS,"成功");
        }

        ResultDto resultDto = null;
        for (MachineDto tmpMachineDto : machineDtos) {
            resultDto = CarProcessFactory.getCarImpl(tmpMachineDto.getHmId()).deleteCarBlackWhite(tmpMachineDto, carBlackWhiteDto);
        }
        return resultDto;
    }


    @Override
    public ResultDto deleteCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto) throws Exception {
        ResultDto resultDto = null;
        //第三方平台
        resultDto = deleteTransactorOtherCarBlackWhite(carBlackWhiteDto);
        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }
        carBlackWhiteDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = carServiceDao.updateCarBlackWhite(carBlackWhiteDto);
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

}
