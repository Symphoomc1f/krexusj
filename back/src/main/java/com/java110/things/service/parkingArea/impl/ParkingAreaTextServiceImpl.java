package com.java110.things.service.parkingArea.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IParkingAreaTextServiceDao;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingArea.IParkingAreaTextService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ParkingAreaTextServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("parkingAreaTextServiceImpl")
public class ParkingAreaTextServiceImpl implements IParkingAreaTextService {

    @Autowired
    private IParkingAreaTextServiceDao parkingAreaTextServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param parkingAreaTextDto 小区对象
     * @return
     */
    @Override
    @Transactional
    public ResultDto saveParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception {
        int count = parkingAreaTextServiceDao.saveParkingAreaText(parkingAreaTextDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingAreaTextDto.getPaId())) {
            data.put("paId", parkingAreaTextDto.getPaId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception {
        int count = parkingAreaTextServiceDao.updateParkingAreaText(parkingAreaTextDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingAreaTextDto.getPaId())) {
            data.put("taskId", parkingAreaTextDto.getPaId());
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
     * @param parkingAreaTextDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception {


        List<ParkingAreaTextDto> parkingAreaTextDtoList = parkingAreaTextServiceDao.getParkingAreaTexts(parkingAreaTextDto);
        int count = 0;
        int totalPage = 1;
        if (parkingAreaTextDtoList != null && parkingAreaTextDtoList.size() > 0) {
            count = parkingAreaTextDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, parkingAreaTextDtoList);
        return resultDto;
    }

    /**
     * 查询小区
     *
     * @param parkingAreaTextDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<ParkingAreaTextDto> queryParkingAreaTexts(ParkingAreaTextDto parkingAreaTextDto) {
        List<ParkingAreaTextDto> parkingAreaTextDtoList = parkingAreaTextServiceDao.getParkingAreaTexts(parkingAreaTextDto);
        return parkingAreaTextDtoList;
    }

   
    @Override
    public ResultDto deleteParkingAreaText(ParkingAreaTextDto parkingAreaTextDto) throws Exception {
        parkingAreaTextDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = parkingAreaTextServiceDao.updateParkingAreaText(parkingAreaTextDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingAreaTextDto.getPaId())) {
            data.put("taskId", parkingAreaTextDto.getPaId());
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
