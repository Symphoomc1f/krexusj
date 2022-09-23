package com.java110.things.netty;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class Java110CarProtocol implements Serializable {


    private static final long serialVersionUID = 290429819350651974L;
    private String id;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Java110CarProtocol(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Java110CarProtocol() {

    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
