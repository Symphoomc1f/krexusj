/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.extApi.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.car.ICarBlackWhiteService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 车辆对外 控制类
 * <p>
 * 完成车辆添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/car")
public class CarExtController extends BaseController {

    @Autowired
    ICarService carServiceImpl;

    @Autowired
    ICarBlackWhiteService carBlackWhiteServiceImpl;

    @Autowired
    ICommunityService communityServiceImpl;

    @Autowired
    IParkingAreaService parkingAreaServiceImpl;

    /**
     * 添加车辆信息
     * <p>
     *
     * @param reqParam {
     *                 "carCode":""
     *                 car_name
     *                 car_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_car_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addCar", method = RequestMethod.POST)
    public ResponseEntity<String> addCar(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "carNum", "未包含车辆编码");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含车辆起租时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含车辆截租时间");
        Assert.hasKeyAndValue(reqJson, "extCarId", "未包含外部车辆编码");
        Assert.hasKeyAndValue(reqJson, "personName", "未包含联系人");
        Assert.hasKeyAndValue(reqJson, "personTel", "未包含联系电话");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部外部停车场ID");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        parkingAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        CarDto carDto = BeanConvertUtil.covertBean(reqJson, CarDto.class);
        carDto.setCarId(SeqUtil.getId());
        carDto.setPaId(parkingAreaDtos.get(0).getPaId());
        carDto.setExtPaId(parkingAreaDtos.get(0).getExtPaId());

        carDto.setCommunityId(communityDtos.get(0).getCommunityId());
        ResultDto result = carServiceImpl.saveCar(carDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改车辆信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC车辆",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateCar", method = RequestMethod.POST)
    public ResponseEntity<String> updateCar(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "carNum", "未包含车辆编码");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含车辆起租时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含车辆截租时间");
        Assert.hasKeyAndValue(reqJson, "extCarId", "未包含外部车辆编码");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "personName", "未包含联系人");
        Assert.hasKeyAndValue(reqJson, "personTel", "未包含联系电话");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CarDto carDto = BeanConvertUtil.covertBean(reqJson, CarDto.class);

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);
        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        CarDto tmpCarDto = new CarDto();
        tmpCarDto.setExtCarId(reqJson.getString("extCarId"));
        tmpCarDto.setPaId(parkingAreaDtos.get(0).getPaId());
        List<CarDto> carDtos = carServiceImpl.queryCars(tmpCarDto);

        Assert.listOnlyOne(carDtos, "未找到车辆信息");
        carDto.setCarId(carDtos.get(0).getCarId());
        carDto.setCardId(carDto.getCardId());
        carDto.setExtPaId(carDtos.get(0).getExtPaId());
        ResultDto result = carServiceImpl.updateCar(carDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除车辆信息
     * <p>
     *
     * @param reqParam {
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteCar", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCar(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extCarId", "未包含外部车辆编码");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CarDto carDto = BeanConvertUtil.covertBean(reqJson, CarDto.class);

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);
        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        CarDto tmpCarDto = new CarDto();
        tmpCarDto.setExtCarId(reqJson.getString("extCarId"));
        tmpCarDto.setPaId(parkingAreaDtos.get(0).getPaId());
        List<CarDto> carDtos = carServiceImpl.queryCars(tmpCarDto);

        Assert.listOnlyOne(carDtos, "未找到车辆信息");
        carDto.setCarId(carDtos.get(0).getCarId());
        carDto.setExtPaId(parkingAreaDtos.get(0).getExtPaId());
        carDto.setCardId(carDtos.get(0).getCardId());

        ResultDto result = carServiceImpl.deleteCar(carDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 添加车辆名单信息
     * <p>
     *
     * @param reqParam {
     *                 "carCode":""
     *                 car_name
     *                 car_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_car_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addBlackWhite", method = RequestMethod.POST)
    public ResponseEntity<String> addBlackWhite(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "carNum", "未包含车辆编码");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含车辆起租时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含车辆截租时间");
        Assert.hasKeyAndValue(reqJson, "blackWhite", "未包含联系人");
        Assert.hasKeyAndValue(reqJson, "extBwId", "未包含联系电话");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部外部停车场ID");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        parkingAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        CarBlackWhiteDto carBlackWhiteDto = BeanConvertUtil.covertBean(reqJson, CarBlackWhiteDto.class);
        carBlackWhiteDto.setBwId(SeqUtil.getId());
        carBlackWhiteDto.setPaId(parkingAreaDtos.get(0).getPaId());
        carBlackWhiteDto.setExtPaId(parkingAreaDtos.get(0).getExtPaId());

        carBlackWhiteDto.setCommunityId(communityDtos.get(0).getCommunityId());
        ResultDto result = carBlackWhiteServiceImpl.saveCarBlackWhite(carBlackWhiteDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 添加车辆名单信息
     * <p>
     *
     * @param reqParam {
     *                 "carCode":""
     *                 car_name
     *                 car_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_car_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteBlackWhite", method = RequestMethod.POST)
    public ResponseEntity<String> deleteBlackWhite(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "carNum", "未包含车辆编码");
        Assert.hasKeyAndValue(reqJson, "extBwId", "未包含联系电话");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部外部停车场ID");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        parkingAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCarNum(reqJson.getString("carNum"));
        carBlackWhiteDto.setExtBwId(reqJson.getString("extBwId"));
        carBlackWhiteDto.setCommunityId(reqJson.getString("communityId"));
        List<CarBlackWhiteDto> carBlackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        Assert.listOnlyOne(carBlackWhiteDtos, "未找到黑白名单");


        ResultDto result = carBlackWhiteServiceImpl.deleteCarBlackWhite(carBlackWhiteDtos.get(0));

        return ResultDto.createResponseEntity(result);
    }

}
