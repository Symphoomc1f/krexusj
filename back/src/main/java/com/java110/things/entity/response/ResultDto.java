package com.java110.things.entity.response;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName ResultDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 22:41
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class ResultDto implements Serializable {

    private int code;

    private String msg;

    private Object data;

    public ResultDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultDto(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
