package com.java110.things.adapt.accessControl.sxoa;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.dao.ISxoaCommunityServiceDao;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.LocalCacheFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SxCommomFactory {

    static Logger logger = LoggerFactory.getLogger(SxCommomFactory.class);
    public static final String GET_TOKEN = "/v1.0/token";
    public static final String GET_SIGN = "/v1.0/sign";
    public static final String GET_AREA_CODE = "/v1.0/area/page";
    public static final String GET_COMMUNITY = "/v1.0/village/view";
    public static final String ADD_COMMUNITY = "/v1.0/village/create";
    public static final String FACEFEATURE = "/v1.0/facefeature/create";
    public static final String ADD_DEVICES_TYPE = "/v1.0/devicestype/create";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";
    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";


    public static SxAreaCodeDto getSxAreaCode(RestTemplate outRestTemplate) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("fullName", "陕西省安康市汉滨区");
        paramIn.put("pageNo", 1);
        paramIn.put("pageSize", 20);

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_AREA_CODE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        logger.debug("请求信息：" + httpEntity + ",返回参数：" + responseEntity);

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("请求sign失败" + paramOut);
        }

        JSONArray resultList = paramOut.getJSONObject("data").getJSONArray("resultList");

        if (resultList == null || resultList.size() < 1) {
            throw new IllegalArgumentException("未找到 区域信息");
        }

        SxAreaCodeDto sxAreaCodeDto = BeanConvertUtil.covertBean(resultList.getJSONObject(0), SxAreaCodeDto.class);

        return sxAreaCodeDto;
    }

    public static SxCommunityDto getSxCommunity(RestTemplate outRestTemplate, String communityId, ISxoaCommunityServiceDao sxoaCommunityServiceDaoImpl) {
        SxCommunityDto communityDto = new SxCommunityDto();
        communityDto.setCommunityId(communityId);
        List<SxCommunityDto> sxCommunityDtos = sxoaCommunityServiceDaoImpl.getCommunitys(communityDto);

        if (sxCommunityDtos == null || sxCommunityDtos.size() < 1) {
            return null;
        }

        return sxCommunityDtos.get(0);
    }

    public static void addSxCommunity(RestTemplate outRestTemplate, MachineDto machineDto, ISxoaCommunityServiceDao sxoaCommunityServiceDaoImpl) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("viAddress", "1");
        paramIn.put("viCode", machineDto.getCommunityId());
        paramIn.put("viName", "1");
        paramIn.put("viAreaCode", getSxAreaCode(outRestTemplate).getAcode());

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_COMMUNITY, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        logger.debug("请求信息：" + httpEntity + ",返回参数：" + responseEntity);

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加小区失败" + responseEntity);
        }

        String locationId = paramOut.getJSONObject("data").getJSONObject("group").getString("locationId");

        SxCommunityDto communityDto = BeanConvertUtil.covertBean(paramOut.getJSONObject("data").getJSONObject("village"), SxCommunityDto.class);
        communityDto.setLocationId(locationId);
        communityDto.setCommunityId(machineDto.getCommunityId());

        //建位置
        paramIn = new JSONObject();
        paramIn.put("dtParentId", 0);
        paramIn.put("dtCode", machineDto.getCommunityId().substring(0,6));
        paramIn.put("dtName", "门禁");
        paramIn.put("dtInfo", "门禁");

        httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_DEVICES_TYPE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        logger.debug("请求信息：" + httpEntity + ",返回参数：" + responseEntity);


        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加小区失败" + responseEntity);
        }

        String dtId = paramOut.getJSONObject("data").getString("dtId");
        communityDto.setDtId(dtId);
        try {
            sxoaCommunityServiceDaoImpl.saveCommunity(communityDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static FaceFeatureResultDto facefeature(RestTemplate outRestTemplate, MachineDto machineDto, ISxoaCommunityServiceDao sxoaCommunityServiceDaoImpl, UserFaceDto userFaceDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("accessToken", getToken(outRestTemplate));
        paramIn.put("ffResidentId", userFaceDto.getUserId());
        paramIn.put("ffOrgId", getSxCommunity(outRestTemplate, machineDto.getCommunityId(), sxoaCommunityServiceDaoImpl).getViOrgId());
        paramIn.put("fileUrl", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + FACEFEATURE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        logger.debug("请求信息：" + httpEntity + ",返回参数：" + responseEntity);

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加小区失败" + responseEntity);
        }

        FaceFeatureResultDto faceFeatureResultDto = BeanConvertUtil.covertBean(paramOut.getJSONObject("data"), FaceFeatureResultDto.class);

        return faceFeatureResultDto;

    }

    public static boolean hasSxCommunity(RestTemplate outRestTemplate, String communityId, ISxoaCommunityServiceDao sxoaCommunityServiceDaoImpl) {
        if (getSxCommunity(outRestTemplate, communityId, sxoaCommunityServiceDaoImpl) != null) {
            return true;
        }
        return false;
    }

    public static HttpHeaders getHeader(RestTemplate outRestTemplate) {
        return getHeader(outRestTemplate, 0);
    }

    public static HttpHeaders getHeader(RestTemplate outRestTemplate, int length) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN", getToken(outRestTemplate));
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        httpHeaders.add("X-EQUIP-TYPE", "dog");
        if (length > 0) {
            httpHeaders.add("Content-Length", length + "");
        }
        return httpHeaders;
    }

    /**
     * 获取accessToken
     *
     * @return
     */
    public static String getToken(RestTemplate outRestTemplate) {

        String token = LocalCacheFactory.getValue("sxoa_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String orgKey = MappingCacheFactory.getValue("orgKey");
        String orgSecret = MappingCacheFactory.getValue("orgSecret");

        long timestamp = DateUtil.getCurrentDate().getTime();
        JSONObject paramIn = new JSONObject();
        paramIn.put("orgKey", orgKey);
        paramIn.put("orgSecret", orgSecret);
        paramIn.put("timestamp", timestamp);
        // 查询sign
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_SIGN, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            logger.error("请求下游服务【" + MappingCacheFactory.getValue("SXOA_URL") + GET_SIGN + "】异常，参数为" + httpEntity + e.getResponseBodyAsString(), e);
            throw e;
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 2000) {
            throw new IllegalStateException("请求sign失败" + paramOut);
        }

        String sign = paramOut.getString("data");

        paramIn = new JSONObject();
        paramIn.put("orgKey", orgKey);
        paramIn.put("orgSecret", orgSecret);
        paramIn.put("timestamp", timestamp + "");
        paramIn.put("sign", sign);
        // 查询sign
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpEntity = new HttpEntity(paramIn.toJSONString(), httpHeaders);
        try {
            responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_TOKEN, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            logger.error("请求下游服务【" + MappingCacheFactory.getValue("SXOA_URL") + GET_TOKEN + "】异常，参数为" + httpEntity + e.getResponseBodyAsString(), e);
            throw e;
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 2000) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        token = paramOut.getJSONObject("data").getString("token");
        LocalCacheFactory.setValue("sxoa_token", token, 10 * 24 * 60);
        return token;
    }
}
