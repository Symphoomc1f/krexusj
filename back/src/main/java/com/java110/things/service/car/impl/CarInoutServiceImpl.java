package com.java110.things.service.car.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICarInoutServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.community.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ICommunityService communityServiceImpl;

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
            return resultDto;
        }


        //同步HC云端
        //查询 小区信息
        CommunityDto communityDto = new CommunityDto();
        resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询小区信息失败");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "当前还没有设置小区，请先设置小区");
        }

        String url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machineTranslate.machineUploadCarLog";
        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", carInoutDto.getMachineCode());
        headers.put("communityId", communityDtos.get(0).getCommunityId());

        JSONObject data = new JSONObject();
        data.put("carNum", carInoutDto.getCarNum());
        data.put("communityId", communityDtos.get(0).getCommunityId());
        data.put("inTime", CarInoutDto.INOUT_TYPE_IN.equals(carInoutDto.getInoutType()) ? carInoutDto.getOpenTime() : "");
        data.put("outTime", CarInoutDto.INOUT_TYPE_OUT.equals(carInoutDto.getInoutType()) ? carInoutDto.getOpenTime() : "");
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data.toString(), headers, HttpMethod.POST);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "上传车辆失败" + tmpResponseEntity.getBody());
        }

        resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);

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


        if (carInoutDto.getPage() != PageDto.DEFAULT_PAGE) {
            carInoutDto.setPage((carInoutDto.getPage() - 1) * carInoutDto.getRow());
        }
        long count = carInoutServiceDao.getCarInoutCount(carInoutDto);
        int totalPage = (int) Math.ceil((double) count / (double) carInoutDto.getRow());
        List<CarInoutDto> carInoutDtoList = null;
        if (count > 0) {
            carInoutDtoList = carInoutServiceDao.getCarInouts(carInoutDto);
            //刷新人脸地
        } else {
            carInoutDtoList = new ArrayList<>();
        }

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
