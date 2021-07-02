package com.java110.things.Controller;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.util.Assert;

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
     * @param paramIn 入参
     * @return 返回json 对象
     */
    public JSONObject getParamJson(String paramIn) {
        Assert.isJsonObject(paramIn, "不是有效的json格式");
        return JSONObject.parseObject(paramIn);
    }
}
