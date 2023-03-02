package com.java110.things.service.fee.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ITempCarFeeConfigServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.car.TempCarFeeConfigAttrDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.fee.TempCarPayOrderDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.service.fee.ITempCarFeeConfigService;
import com.java110.things.service.machine.IMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TempCarFeeConfigServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("tempCarFeeConfigServiceImpl")
public class TempCarFeeConfigServiceImpl implements ITempCarFeeConfigService {

    @Autowired
    private ITempCarFeeConfigServiceDao carServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMachineService machineService;

    /**
     * 添加小区信息
     *
     * @param tempCarFeeConfigDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        ResultDto resultDto = null;

        int count = carServiceDao.saveTempCarFeeConfig(tempCarFeeConfigDto);

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
     * @param tempCarFeeConfigDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {

        if (tempCarFeeConfigDto.getPage() != PageDto.DEFAULT_PAGE) {
            tempCarFeeConfigDto.setPage((tempCarFeeConfigDto.getPage() - 1) * tempCarFeeConfigDto.getRow());
        }
        long count = carServiceDao.getTempCarFeeConfigCount(tempCarFeeConfigDto);
        int totalPage = (int) Math.ceil((double) count / (double) tempCarFeeConfigDto.getRow());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtoList = null;
        if (count > 0) {
            tempCarFeeConfigDtoList = carServiceDao.getTempCarFeeConfigs(tempCarFeeConfigDto);
            //刷新人脸地
        } else {
            tempCarFeeConfigDtoList = new ArrayList<>();
        }

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, tempCarFeeConfigDtoList);
        return resultDto;
    }

    @Override
    public List<TempCarFeeConfigDto> queryTempCarFeeConfigs(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        if (tempCarFeeConfigDto.getPage() != PageDto.DEFAULT_PAGE) {
            tempCarFeeConfigDto.setPage((tempCarFeeConfigDto.getPage() - 1) * tempCarFeeConfigDto.getRow());
        }
        List<TempCarFeeConfigDto> tempCarFeeConfigDtoList = null;

        tempCarFeeConfigDtoList = carServiceDao.getTempCarFeeConfigs(tempCarFeeConfigDto);
        //刷新人脸地
        return tempCarFeeConfigDtoList;

    }

    @Override
    public ResultDto updateTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {

        int count = carServiceDao.updateTempCarFeeConfig(tempCarFeeConfigDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    @Override
    public ResultDto deleteTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        ResultDto resultDto = null;

        tempCarFeeConfigDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = carServiceDao.updateTempCarFeeConfig(tempCarFeeConfigDto);
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public int saveTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigDto) throws Exception {
        int count = carServiceDao.saveTempCarFeeConfigAttr(tempCarFeeConfigDto);
        return count;
    }

    @Override
    public List<TempCarFeeConfigAttrDto> queryTempCarFeeConfigAttrs(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto) throws Exception {
        return carServiceDao.getTempCarFeeConfigAttrs(tempCarFeeConfigAttrDto);
    }

    @Override
    public int deleteTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto) throws Exception {
        return carServiceDao.deleteTempCarFeeConfigAttr(tempCarFeeConfigAttrDto);
    }

    @Override
    public ResultDto getTempCarFeeOrder(CarDto carDto) throws Exception {

        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carDto.getExtPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "设备不存在");
        }
        //这里预留 调用自己的 算费系统算费 暂不实现
        if (MachineDto.MACHINE_TYPE_CAR.equals(machineDtos.get(0).getMachineTypeCd())) {
            return new ResultDto(ResultDto.SUCCESS, "成功");
        }

        TempCarPayOrderDto tempCarPayOrderDto = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).getNeedPayOrder(machineDtos.get(0), carDto);

        if (tempCarPayOrderDto == null) {
            return new ResultDto(ResultDto.ERROR, "查询失败");
        }

        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG, tempCarPayOrderDto);

    }

    @Override
    public ResultDto notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) throws Exception {
        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(tempCarPayOrderDto.getExtPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "设备不存在");
        }
        //这里预留 调用自己的 算费系统算费 暂不实现
        if (MachineDto.MACHINE_TYPE_CAR.equals(machineDtos.get(0).getMachineTypeCd())) {
            return new ResultDto(ResultDto.SUCCESS, "成功");
        }

        ResultDto resultDto = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).notifyTempCarFeeOrder(machineDtos.get(0), tempCarPayOrderDto);

        return resultDto;
    }

}
