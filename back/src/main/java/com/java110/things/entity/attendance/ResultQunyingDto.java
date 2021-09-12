package com.java110.things.entity.attendance;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName ResultQunyingDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/26 16:33
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
public class ResultQunyingDto implements Serializable {

    private int status;

    private String info;

    private Object data;

    public ResultQunyingDto(int status, String info, Object data) {
        this.status = status;
        this.info = info;
        this.data = data;
    }

    public ResultQunyingDto(int status, String info) {
        this.status = status;
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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
