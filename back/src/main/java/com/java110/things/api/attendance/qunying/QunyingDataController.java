package com.java110.things.api.attendance.qunying;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.response.ResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName GetDataController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/26 16:25
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
@RestController
@RequestMapping(path = "/api/data")
public class QunyingDataController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(QunyingDataController.class);

    /**
     * 添加设备接口类
     * <p>
     *
     * @param sn 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public ResponseEntity<String> getData(
            @RequestParam String sn,
            @RequestParam(name = "requesttime", required = false) String requesttime,
            @RequestParam(name = "sign", required = false) String sign
    ) throws Exception {
        logger.debug("考勤机请求接口 get" + sn);

        ResultQunyingDto resultQunyingDto = new ResultQunyingDto();

        resultQunyingDto.setStatus(1);
        resultQunyingDto.setInfo("ok");
        JSONArray data = new JSONArray();
        JSONObject dataInfo = new JSONObject();
        dataInfo.put("id","1006");
        dataInfo.put("do","upload");
        dataInfo.put("data","info");
        data.add(dataInfo);
        resultQunyingDto.setData(data);

        return super.createResponseEntity(resultQunyingDto);
    }


    /**
     * 考勤机信息上报接口
     * <p>
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/post", method = RequestMethod.POST)
    public ResponseEntity<String> postData(
            @RequestBody String param
    ) throws Exception {

        logger.debug("考勤机请求接口 post " + param);

        return super.createResponseEntity(new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG));
    }
}
