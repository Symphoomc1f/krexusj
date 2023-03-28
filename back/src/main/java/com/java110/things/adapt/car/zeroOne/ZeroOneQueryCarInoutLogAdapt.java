package com.java110.things.adapt.car.zeroOne;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.parkingArea.ParkingAreaAttrDto;
import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.car.ICarService;
import com.java110.things.service.parkingArea.IParkingAreaService;
import com.java110.things.util.SeqUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ZeroOneQueryCarInoutLogAdapt {
    private static Logger logger = LoggerFactory.getLogger(ZeroOneCarSocketProcessAdapt.class);

    public static final String SPEC_EXT_PARKING_ID = "6185-17861";
    private static final String PULLCARENTRY_URL = "/yihao01-park-opendata/outapi/dataQuery/pullCarEntry";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICarInoutService carInoutService;

    @Autowired
    private IParkingAreaService parkingAreaService;

    @Autowired
    private ICarService carService;

    public void query(String pId){
        String url = MappingCacheFactory.getValue("ZERO_ONE_CAR_URL") + PULLCARENTRY_URL;
        String appKey = MappingCacheFactory.getValue("ZERO_ONE_APP_KEY");
        //拉去场内车辆

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(pId);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaService.queryParkingAreas(parkingAreaDto);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("parkNo", getParkingId(parkingAreaDtos.get(0)));
        postParameters.put("appKey", appKey);

        postParameters.put("pageNo", "1");
        postParameters.put("pageSize", "100");
        postParameters.put("sign", getSign(postParameters));
        HttpHeaders httpHeaders = getHeader();
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(postParameters), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

    }



    public String getParkingId(ParkingAreaDto parkingAreaDto) {
        List<ParkingAreaAttrDto> parkingAreaAttrDtos = parkingAreaDto.getAttrs();

        if (parkingAreaAttrDtos == null || parkingAreaAttrDtos.size() < 1) {
            return "";
        }

        for (ParkingAreaAttrDto parkingAreaAttrDto : parkingAreaAttrDtos) {
            if (SPEC_EXT_PARKING_ID.equals(parkingAreaAttrDto.getSpecCd())) {
                return parkingAreaAttrDto.getValue();
            }
        }

        return "";
    }


    /**
     * 获取accessToken
     *
     * @return
     */
    private String getSign(Map<String, String> reqParamMap) {

        String signKey = MappingCacheFactory.getValue("ZERO_ONE_SIGN_KEY");

        Map<String, String> paramsMap = reqParamMap instanceof TreeMap ? reqParamMap : new TreeMap<>(reqParamMap);
        StringBuilder buf = new StringBuilder(paramsMap.size() * 20);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            if ("sign".equals(key)) {
                continue; // 不在此处添加签名，sign参数必须放最后
            }
            try {
                key = URLEncoder.encode(key, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            buf.append(key).append('=').append(entry.getValue()).append('&');
        }
        buf.append("key=").append(signKey);
        String sign = DigestUtils.md5Hex(buf.toString());
        logger.debug("签名字符串={}", buf.toString());
        return sign;
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("appKey", "Bearer " + getToken());
//        httpHeaders.add("sign", "Bearer " + getSign());
        httpHeaders.add("Content-Type", "application/json");
        return httpHeaders;
    }
}
