package com.java110.things.kafka;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.app.AppDto;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.factory.KafkaFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.app.IAppService;
import com.java110.things.util.Assert;
import com.java110.things.util.DateUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * kafka侦听
 * Created by wuxw on 2018/4/15.
 */
public class IotServiceKafka {

    private final static Logger logger = LoggerFactory.getLogger(IotServiceKafka.class);

    public static final String HC_TOPIC = "iot-topic";

    private IAppService appServiceImpl;

    /**
     * 协议
     * {
     * appId:"",
     * action:“”,
     * sign:"",
     * data:{
     * <p>
     * },
     * <p>
     * }
     *
     * @param record
     */
    @KafkaListener(topics = {"${kafka.topic}"})
    public void listen(ConsumerRecord<?, ?> record) throws Exception {
        logger.info("kafka的key: " + record.key());
        logger.info("kafka的value: " + record.value().toString());
        String data = record.value().toString();

        JSONObject param = JSONObject.parseObject(data);

        String appId = param.getString("appId");


        AppDto appDto = new AppDto();
        appDto.setAppId(appId);
        appServiceImpl = ApplicationContextFactory.getBean("appServiceImpl", IAppService.class);
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "应用不存在");

        String accessToken = appDtos.get(0).getAccessToken();

        try {
            AuthenticationFactory.verifyToken(accessToken);
        } catch (Exception e) {
            Map<String, String> info = new HashMap();
            info.put("appId", appId);
            info.put(AuthenticationFactory.LOGIN_USER_ID, appId);
            accessToken = AuthenticationFactory.createAndSaveToken(info);
            appDto.setAccessToken(accessToken);
            appDto.setUpdateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
            appServiceImpl.updateApp(appDto);
        }

        //
        RestTemplate restTemplate = ApplicationContextFactory.getBean("restTemplate", RestTemplate.class);

        String url = MappingCacheFactory.getValue("IOT_URL") + "/" + param.getString("action");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access_token", accessToken);
        HttpEntity httpEntity = new HttpEntity(JSONObject.toJSONString(param.get("data")), httpHeaders);
        ResponseEntity<String> paramOut = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        JSONObject paramObj = JSONObject.parseObject(paramOut.getBody());


        param.put("data", paramObj);

        KafkaFactory.sendKafkaMessage(HC_TOPIC, "", param.toJSONString());
    }


}
