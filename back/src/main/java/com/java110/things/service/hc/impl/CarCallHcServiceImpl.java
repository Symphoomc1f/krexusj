package com.java110.things.service.hc.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.app.AppAttrDto;
import com.java110.things.entity.app.AppDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.service.app.IAppService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.hc.ICarCallHcService;
import com.java110.things.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用HC小区管理系统实现类
 * <p>
 * 演示地址：demo.homecommunity.cn
 * 代码：https://gitee.com/wuxw7/MicroCommunity
 *
 * @ClassName CarCallHcServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2021/1/18 20:54
 * @Version 1.0
 * add by wuxw 2021/1/18
 **/
@Service
public class CarCallHcServiceImpl implements ICarCallHcService {

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IAppService appServiceImpl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    //@Async
    public void carInout(CarInoutDto carInoutDto) throws Exception {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(carInoutDto.getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未包含小区信息");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_CAR_INOUT);

        if (appAttrDto == null) {
            return;
        }

        String value = appAttrDto.getValue();
        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }

        String url = value;
        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", carInoutDto.getMachineCode());
        headers.put("communityId", communityDtos.get(0).getExtCommunityId());

        JSONObject data = new JSONObject();
        data.put("carNum", carInoutDto.getCarNum());
        data.put("machineCode", carInoutDto.getMachineCode());
        data.put("communityId", communityDtos.get(0).getExtCommunityId());
        if (CarInoutDto.INOUT_TYPE_IN.equals(carInoutDto.getInoutType())) {
            data.put("inTime", CarInoutDto.INOUT_TYPE_IN.equals(carInoutDto.getInoutType()) ? carInoutDto.getOpenTime() : "");
        } else {
            data.put("outTime", CarInoutDto.INOUT_TYPE_OUT.equals(carInoutDto.getInoutType()) ? carInoutDto.getOpenTime() : "");
            data.put("payCharge", carInoutDto.getPayCharge());
            data.put("realCharge", carInoutDto.getRealCharge());
            data.put("payType", carInoutDto.getPayType());
        }
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data.toString(), headers, HttpMethod.POST, securityCode);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "上传车辆失败" + tmpResponseEntity.getBody());
        }

    }
}
