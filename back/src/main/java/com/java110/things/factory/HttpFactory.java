package com.java110.things.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.machine.TransactionLogDto;
import com.java110.things.service.machine.ITransactionLogService;
import com.java110.things.util.DateUtil;
import com.java110.things.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Date;
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
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, HttpMethod httpMethod) throws UnsupportedEncodingException {
        return exchange(restTemplate, url, param, null, httpMethod, MappingCacheFactory.getValue("SECURITY_CODE"));
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
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, HttpMethod httpMethod, String securityCode) throws UnsupportedEncodingException {
        return exchange(restTemplate, url, param, null, httpMethod, securityCode);
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
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, Map<String, String> headers, HttpMethod httpMethod) throws UnsupportedEncodingException {
        return exchange(restTemplate, url, param, null, httpMethod, MappingCacheFactory.getValue("SECURITY_CODE"));
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
    public static ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String param, Map<String, String> headers, HttpMethod httpMethod, String securityCode) throws UnsupportedEncodingException {
        HttpHeaders httpHeaders = getHeader();
        Date startTime = DateUtil.getCurrentDate();
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                if (SystemConstant.HTTP_APP_ID.toLowerCase().equals(key.toLowerCase())) {
                    httpHeaders.remove(key.toLowerCase());
                    httpHeaders.add(key.toLowerCase(), headers.get(key));
                } else {
                    httpHeaders.add(key, headers.get(key));
                }
            }
        }
        String tempGetParam = "";
        if (url.indexOf("?") > 0) {
            tempGetParam = url.substring(url.indexOf("?"));
        }
        String paramIn = HttpMethod.GET == httpMethod ? tempGetParam : param;

        // 生成sign
        String sign = AuthenticationFactory.generatorSign(httpHeaders.get(SystemConstant.HTTP_TRANSACTION_ID).get(0),
                httpHeaders.get(SystemConstant.HTTP_REQ_TIME).get(0),
                paramIn, securityCode);
        httpHeaders.remove(SystemConstant.HTTP_SIGN);
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
            Date endTime = DateUtil.getCurrentDate();
            saveTranLog(url, headers, param, responseEntity.getHeaders().toString(), responseEntity.getBody().toString(), startTime, endTime);
            return responseEntity;
        }
    }

    private static void saveTranLog(String url, Map<String, String> reqHeader, String reqParam, String resHeader, String resParam, Date startTime, Date endTime) {
        try {
            String tranLogSwitch = MappingCacheFactory.getValue("TRAN_LOG_SWITCH");
            if (!"ON".equals(tranLogSwitch)) {
                return;
            }
            String machineCode = reqHeader != null && reqHeader.containsKey("machinecode") ? reqHeader.get("machinecode").toString() : "";
            if (StringUtil.isEmpty(machineCode)) {
                machineCode = reqHeader != null && reqHeader.containsKey("machineCode") ? reqHeader.get("machineCode").toString() : "";
            }
            ITransactionLogService transactionLogServiceImpl = ApplicationContextFactory.getBean("transactionLogServiceImpl", ITransactionLogService.class);
            TransactionLogDto transactionLogDto = new TransactionLogDto();
            transactionLogDto.setTranId(UUID.randomUUID().toString());
            transactionLogDto.setMachineCode(machineCode);
            transactionLogDto.setReqHeader(JSONObject.toJSONString(reqHeader));
            transactionLogDto.setReqParam(reqParam);
            transactionLogDto.setResHeader(resHeader);
            transactionLogDto.setResParam(resParam);
            transactionLogDto.setUrl(url);
            transactionLogDto.setReqTime(DateUtil.getFormatTimeString(startTime, DateUtil.DATE_FORMATE_STRING_A));
            transactionLogDto.setResTime(DateUtil.getFormatTimeString(endTime, DateUtil.DATE_FORMATE_STRING_A));
            transactionLogServiceImpl.saveTransactionLog(transactionLogDto);
        } catch (Exception e) {
            logger.error("保存日志出错", e);
        }
    }
}
