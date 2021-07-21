package com.java110.things.factory;

import com.java110.things.accessControl.HeartbeatCloudApiThread;
import com.java110.things.constant.SystemConstant;
import com.java110.things.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * @ClassName HttpFactory
 * @Description Http 请求工厂类
 * @Author wuxw
 * @Date 2020/5/11 0:06
 * @Version 1.0
 * add by wuxw 2020/5/11
 **/
public class HttpFactory {
    private static Logger logger = LoggerFactory.getLogger(HttpFactory.class);

    public static HttpHeaders getHeader() {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json");
        header.add(SystemConstant.HTTP_APP_ID.toLowerCase(), MappingCacheFactory.getValue("APP_ID"));
        header.add(SystemConstant.HTTP_USER_ID.toLowerCase(), SystemConstant.ORDER_DEFAULT_USER_ID);
        header.add(SystemConstant.HTTP_TRANSACTION_ID.toLowerCase(), UUID.randomUUID().toString());
        header.add(SystemConstant.HTTP_REQ_TIME.toLowerCase(), DateUtil.getyyyyMMddhhmmssDateString());
        header.add(SystemConstant.HTTP_SIGN.toLowerCase(), "");
        return header;
    }

    /**
     * map 参数转 url get 参数 非空值转为get参数 空值忽略
     *
     * @param info map数据
     * @return url get 参数 带？
     */
    public static String mapToUrlParam(Map info) {
        String urlParam = "";
        if (info == null || info.isEmpty()) {
            return urlParam;
        }

        urlParam += "?";

        for (Object key : info.keySet()) {
            if (StringUtils.isEmpty(info.get(key) + "")) {
                continue;
            }

            urlParam += (key + "=" + info.get(key) + "&");
        }

        urlParam = urlParam.endsWith("&") ? urlParam.substring(0, urlParam.length() - 1) : urlParam;

        return urlParam;
    }

    /**
     * http 调用接口
     *
     * @param restTemplate 请求模板
     * @param url          请求地址
     * @param param        请求参数
     * @param httpMethod   请求方法
     * @return 返回 ResponseEntity
     */
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, HttpMethod httpMethod) {
        return exchange(restTemplate, url, param, null, httpMethod);
    }

    /**
     * http 调用接口
     *
     * @param restTemplate 请求模板
     * @param url          请求地址
     * @param param        请求参数
     * @param headers      请求头
     * @param httpMethod   请求方法
     * @return 返回 ResponseEntity
     */
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, Map<String, String> headers, HttpMethod httpMethod) {
        HttpHeaders httpHeaders = getHeader();
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                if (SystemConstant.HTTP_SIGN.equals(key)) {
                    continue;
                }
                httpHeaders.add(key, headers.get(key));
            }
        }

        String paramIn = HttpMethod.GET == httpMethod ? url : param;

        // 生成sign
        String sign = AuthenticationFactory.generatorSign(headers.get(SystemConstant.HTTP_TRANSACTION_ID),
                headers.get(SystemConstant.HTTP_REQ_TIME),
                paramIn);
        httpHeaders.add(SystemConstant.HTTP_SIGN, sign);
        HttpEntity<String> httpEntity = new HttpEntity<String>(param, httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            responseEntity = new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            responseEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", url, httpEntity, responseEntity);
            return responseEntity;
        }
    }
}
