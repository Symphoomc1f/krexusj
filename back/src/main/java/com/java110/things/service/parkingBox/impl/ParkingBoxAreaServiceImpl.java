package com.java110.things.service.parkingBox.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IParkingBoxAreaServiceDao;
import com.java110.things.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingBox.IParkingBoxAreaService;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @ClassName ParkingBoxAreaServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("parkingBoxAreaServiceImpl")
public class ParkingBoxAreaServiceImpl implements IParkingBoxAreaService {

    @Autowired
    private IParkingBoxAreaServiceDao parkingBoxAreaServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param parkingBoxAreaDto 小区对象
     * @return
     */
    @Override
    @Transactional
    public ResultDto saveParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception {
        int count = parkingBoxAreaServiceDao.saveParkingBoxArea(parkingBoxAreaDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingBoxAreaDto.getPaId())) {
            data.put("paId", parkingBoxAreaDto.getPaId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception {
        int count = parkingBoxAreaServiceDao.updateParkingBoxArea(parkingBoxAreaDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingBoxAreaDto.getPaId())) {
            data.put("taskId", parkingBoxAreaDto.getPaId());
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
     * @param parkingBoxAreaDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception {


        List<ParkingBoxAreaDto> parkingBoxAreaDtoList = parkingBoxAreaServiceDao.getParkingBoxAreas(parkingBoxAreaDto);
        int count = 0;
        int totalPage = 1;
        if (parkingBoxAreaDtoList != null && parkingBoxAreaDtoList.size() > 0) {
            count = parkingBoxAreaDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, parkingBoxAreaDtoList);
        return resultDto;
    }

    /**
     * 查询小区
     *
     * @param parkingBoxAreaDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<ParkingBoxAreaDto> queryParkingBoxAreas(ParkingBoxAreaDto parkingBoxAreaDto) {
        List<ParkingBoxAreaDto> parkingBoxAreaDtoList = parkingBoxAreaServiceDao.getParkingBoxAreas(parkingBoxAreaDto);
        return parkingBoxAreaDtoList;
    }

   
    @Override
    public ResultDto deleteParkingBoxArea(ParkingBoxAreaDto parkingBoxAreaDto) throws Exception {
        parkingBoxAreaDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = parkingBoxAreaServiceDao.updateParkingBoxArea(parkingBoxAreaDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingBoxAreaDto.getPaId())) {
            data.put("taskId", parkingBoxAreaDto.getPaId());
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
