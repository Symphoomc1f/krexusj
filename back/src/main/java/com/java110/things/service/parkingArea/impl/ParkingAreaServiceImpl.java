package com.java110.things.service.parkingArea.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IParkingAreaServiceDao;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @ClassName ParkingAreaServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("parkingAreaServiceImpl")
public class ParkingAreaServiceImpl implements IParkingAreaService {

    @Autowired
    private IParkingAreaServiceDao parkingAreaServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param parkingAreaDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveParkingArea(ParkingAreaDto parkingAreaDto) throws Exception {
        int count = parkingAreaServiceDao.saveParkingArea(parkingAreaDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingAreaDto.getPaId())) {
            data.put("paId", parkingAreaDto.getPaId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateParkingArea(ParkingAreaDto parkingAreaDto) throws Exception {
        int count = parkingAreaServiceDao.updateParkingArea(parkingAreaDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingAreaDto.getPaId())) {
            data.put("taskId", parkingAreaDto.getPaId());
        }
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    /**
     * 查询小区信息
     *
     * @param parkingAreaDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getParkingArea(ParkingAreaDto parkingAreaDto) throws Exception {


        List<ParkingAreaDto> parkingAreaDtoList = parkingAreaServiceDao.getParkingAreas(parkingAreaDto);
        int count = 0;
        int totalPage = 1;
        if (parkingAreaDtoList != null && parkingAreaDtoList.size() > 0) {
            count = parkingAreaDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, parkingAreaDtoList);
        return resultDto;
    }

    /**
     * 查询小区
     *
     * @param parkingAreaDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<ParkingAreaDto> queryParkingAreas(ParkingAreaDto parkingAreaDto) throws Exception {
        List<ParkingAreaDto> parkingAreaDtoList = parkingAreaServiceDao.getParkingAreas(parkingAreaDto);
        return parkingAreaDtoList;
    }

    @Override
    public ResultDto deleteParkingArea(ParkingAreaDto parkingAreaDto) throws Exception {
        parkingAreaDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = parkingAreaServiceDao.updateParkingArea(parkingAreaDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingAreaDto.getPaId())) {
            data.put("taskId", parkingAreaDto.getPaId());
        }
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }


}
