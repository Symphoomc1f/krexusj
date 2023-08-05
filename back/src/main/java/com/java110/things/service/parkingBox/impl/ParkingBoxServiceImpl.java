package com.java110.things.service.parkingBox.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IParkingAreaServiceDao;
import com.java110.things.dao.IParkingBoxAreaServiceDao;
import com.java110.things.dao.IParkingBoxServiceDao;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.things.entity.parkingArea.ParkingBoxDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.parkingBox.IParkingBoxService;
import com.java110.things.util.Assert;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @ClassName ParkingBoxServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("parkingBoxServiceImpl")
public class ParkingBoxServiceImpl implements IParkingBoxService {

    @Autowired
    private IParkingBoxServiceDao parkingBoxServiceDao;

    @Autowired
    private IParkingBoxAreaServiceDao parkingBoxAreaServiceDao;

    @Autowired
    private IParkingAreaServiceDao parkingAreaServiceDaoImpl;


    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param parkingBoxDto 小区对象
     * @return
     */
    @Override
    @Transactional
    public ResultDto saveParkingBox(ParkingBoxDto parkingBoxDto) throws Exception {
        int count = parkingBoxServiceDao.saveParkingBox(parkingBoxDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
            return resultDto;
        }
        if (StringUtil.isEmpty(parkingBoxDto.getPaId())) {
            return resultDto;
        }

        //判断停车场是否存在
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(parkingBoxDto.getPaId());
        parkingAreaDto.setCommunityId(parkingBoxDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceDaoImpl.getParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");

        ParkingBoxAreaDto parkingBoxAreaPo = new ParkingBoxAreaDto();
        parkingBoxAreaPo.setBaId(SeqUtil.getId());
        parkingBoxAreaPo.setBoxId(parkingBoxDto.getBoxId());
        parkingBoxAreaPo.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingBoxAreaPo.setCommunityId(parkingAreaDtos.get(0).getCommunityId());
        parkingBoxAreaPo.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);

        int flag = parkingBoxAreaServiceDao.saveParkingBoxArea(parkingBoxAreaPo);
        if (flag < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
            return resultDto;
        }
        resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);

        return resultDto;
    }

    @Override
    public ResultDto updateParkingBox(ParkingBoxDto parkingBoxDto) throws Exception {
        int count = parkingBoxServiceDao.updateParkingBox(parkingBoxDto);
        JSONObject data = new JSONObject();
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
            return resultDto;
        }
        if (StringUtil.isEmpty(parkingBoxDto.getPaId())) {
            return resultDto;
        }

        //判断停车场是否存在
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(parkingBoxDto.getPaId());
        parkingAreaDto.setCommunityId(parkingBoxDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceDaoImpl.getParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");

        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setBoxId(parkingBoxDto.getBoxId());
        parkingBoxAreaDto.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingBoxAreaDto.setCommunityId(parkingAreaDtos.get(0).getCommunityId());
        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaServiceDao.getParkingBoxAreas(parkingBoxAreaDto);
        ParkingBoxAreaDto parkingBoxAreaPo = new ParkingBoxAreaDto();
        parkingBoxAreaPo.setBoxId(parkingBoxDto.getBoxId());
        parkingBoxAreaPo.setPaId(parkingAreaDtos.get(0).getPaId());
        parkingBoxAreaPo.setCommunityId(parkingAreaDtos.get(0).getCommunityId());
        int flag = 0;
        if (parkingBoxAreaDtos == null || parkingBoxAreaDtos.size() < 1) {
            parkingBoxAreaPo.setBaId(SeqUtil.getId());
            parkingBoxAreaPo.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
            flag = parkingBoxAreaServiceDao.saveParkingBoxArea(parkingBoxAreaPo);
        } else {
            parkingBoxAreaPo.setBaId(parkingBoxAreaDtos.get(0).getBaId());
            flag = parkingBoxAreaServiceDao.updateParkingBoxArea(parkingBoxAreaPo);
        }
        if (flag < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
            return resultDto;
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
    }

    /**
     * 查询小区信息
     *
     * @param parkingBoxDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getParkingBox(ParkingBoxDto parkingBoxDto) throws Exception {


        List<ParkingBoxDto> parkingBoxDtoList = parkingBoxServiceDao.getParkingBoxs(parkingBoxDto);
        int count = 0;
        int totalPage = 1;
        if (parkingBoxDtoList != null && parkingBoxDtoList.size() > 0) {
            count = parkingBoxDtoList.size();
        }
        totalPage = (int) Math.ceil((double) count / 10.0);


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, parkingBoxDtoList);
        return resultDto;
    }

    /**
     * 查询小区
     *
     * @param parkingBoxDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<ParkingBoxDto> queryParkingBoxs(ParkingBoxDto parkingBoxDto) {
        List<ParkingBoxDto> parkingBoxDtoList = parkingBoxServiceDao.getParkingBoxs(parkingBoxDto);
        return parkingBoxDtoList;
    }

    @Override
    public ResultDto deleteParkingBox(ParkingBoxDto parkingBoxDto) throws Exception {
        parkingBoxDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = parkingBoxServiceDao.delete(parkingBoxDto);
        JSONObject data = new JSONObject();
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto saveParkingBoxExt(ParkingBoxDto parkingBoxDto) {
        String extBoxId = parkingBoxDto.getExtBoxId();

        ParkingBoxDto tmpParkingBoxDto = new ParkingBoxDto();
        tmpParkingBoxDto.setExtBoxId(extBoxId);
        List<ParkingBoxDto> parkingBoxDtos = parkingBoxServiceDao.getParkingBoxs(tmpParkingBoxDto);

        ResultDto resultDto = null;
        if (parkingBoxDtos == null || parkingBoxDtos.size() < 1) {
            resultDto = doSaveParkingBox(parkingBoxDto);
        } else {
            resultDto = doUpdateParkingBox(parkingBoxDto, parkingBoxDtos.get(0));
        }

        return resultDto;
    }

    private ResultDto doUpdateParkingBox(ParkingBoxDto parkingBoxDto, ParkingBoxDto oldParkingBoxDto) {

        ParkingBoxAreaDto deleteParkingBoxAreaDto = new ParkingBoxAreaDto();
        deleteParkingBoxAreaDto.setBoxId(oldParkingBoxDto.getBoxId());
        parkingBoxAreaServiceDao.delete(deleteParkingBoxAreaDto);
        parkingBoxDto.setBoxId(oldParkingBoxDto.getBoxId());
        int flag = parkingBoxServiceDao.updateParkingBox(parkingBoxDto);

        if (flag < 1) {
            throw new IllegalArgumentException("参数错误");
        }

        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxDto.getParkingBoxAreas();
        if (parkingBoxAreaDtos == null || parkingBoxAreaDtos.size() < 1) {
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
        }

        for (ParkingBoxAreaDto parkingBoxAreaDto : parkingBoxAreaDtos) {
            parkingBoxAreaDto.setBoxId(parkingBoxDto.getBoxId());
            parkingBoxAreaServiceDao.saveParkingBoxArea(parkingBoxAreaDto);
        }
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    private ResultDto doSaveParkingBox(ParkingBoxDto parkingBoxDto) {

        parkingBoxDto.setBoxId(SeqUtil.getId());
        int flag = parkingBoxServiceDao.saveParkingBox(parkingBoxDto);

        if (flag < 1) {
            throw new IllegalArgumentException("参数错误");
        }

        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxDto.getParkingBoxAreas();
        if (parkingBoxAreaDtos == null || parkingBoxAreaDtos.size() < 1) {
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
        }

        for (ParkingBoxAreaDto parkingBoxAreaDto : parkingBoxAreaDtos) {
            parkingBoxAreaDto.setBoxId(parkingBoxDto.getBoxId());
            parkingBoxAreaServiceDao.saveParkingBoxArea(parkingBoxAreaDto);
        }
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }


}
