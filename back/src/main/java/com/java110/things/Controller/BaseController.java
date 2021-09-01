package com.java110.things.Controller;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.util.Assert;
import com.java110.things.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName BaseController 基础父类
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:29
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class BaseController {

    /**
     * 获取json对象
     *
     * @param paramIn 入参
     * @return 返回json 对象
     */
    public JSONObject getParamJson(String paramIn) {
        Assert.isJsonObject(paramIn, "不是有效的json格式");
        return JSONObject.parseObject(paramIn);
    }

    public ResponseEntity<String> createResponseEntity(ResultDto resultDto) {
        return new ResponseEntity<String>(JSONObject.toJSONString(resultDto), HttpStatus.OK);
    }

    public ResponseEntity<String> createResponseEntity(ResultQunyingDto resultDto) {
        return new ResponseEntity<String>(JSONObject.toJSONString(resultDto), HttpStatus.OK);
    }

    /**
     * 从会话中获取用户信息
     * @param request 请求对象
     * @return
     */
    public String getUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute(SystemConstant.LOGIN_USER_ID);
        if (StringUtil.isNullOrNone(userIdObj)) {
            return null;
        }

        return userIdObj.toString();
    }
}
