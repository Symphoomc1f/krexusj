package com.java110.things.service.parkingArea.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IParkingAreaAttrServiceDao;
import com.java110.things.dao.IParkingAreaServiceDao;
import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    private IParkingAreaAttrServiceDao parkingAreaAttrServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param parkingAreaDto 小区对象
     * @return
     */
    @Override
    @Transactional
    public ResultDto saveParkingArea(ParkingAreaDto parkingAreaDto) throws Exception {
        int count = parkingAreaServiceDao.saveParkingArea(parkingAreaDto);
        for (ParkingAreaAttrDto parkingAreaAttrDto : parkingAreaDto.getAttrs()) {
            parkingAreaAttrDto.setAttrId(SeqUtil.getId());
            parkingAreaAttrDto.setPaId(parkingAreaDto.getPaId());
            parkingAreaAttrServiceDao.saveParkingAreaAttr(parkingAreaAttrDto);
        }
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
    public List<ParkingAreaDto> queryParkingAreas(ParkingAreaDto parkingAreaDto) {
        List<ParkingAreaDto> parkingAreaDtoList = parkingAreaServiceDao.getParkingAreas(parkingAreaDto);
        freshAttrs(parkingAreaDtoList);
        return parkingAreaDtoList;
    }

    /**
     * 刷新属性
     *
     * @param parkingAreaDtoList
     */
    private void freshAttrs(List<ParkingAreaDto> parkingAreaDtoList) {
        if (parkingAreaDtoList == null || parkingAreaDtoList.size() < 1) {
            return;
        }
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtoList) {
            paIds.add(parkingAreaDto.getPaId());
        }
        ParkingAreaAttrDto parkingAreaAttrDto = new ParkingAreaAttrDto();
        parkingAreaAttrDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        List<ParkingAreaAttrDto> parkingAreaAttrDtos = parkingAreaAttrServiceDao.getParkingAreaAttrs(parkingAreaAttrDto);

        List<ParkingAreaAttrDto> tmpParkingAreaAttrDtos = null;
        for(ParkingAreaDto parkingAreaDto : parkingAreaDtoList){
            tmpParkingAreaAttrDtos = new ArrayList<>();
            for(ParkingAreaAttrDto tmpParkingAreaAttrDto: parkingAreaAttrDtos){
                if(parkingAreaDto.getPaId().equals(tmpParkingAreaAttrDto.getPaId())){
                    tmpParkingAreaAttrDtos.add(tmpParkingAreaAttrDto);
                }
            }
            parkingAreaDto.setAttrs(tmpParkingAreaAttrDtos);
        }
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
