package com.java110.things.service.fee.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ITempCarFeeConfigServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import com.java110.things.entity.response.ResultDto;
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

        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }


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
        //第三方平台
        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }
        tempCarFeeConfigDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = carServiceDao.updateTempCarFeeConfig(tempCarFeeConfigDto);
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

}
