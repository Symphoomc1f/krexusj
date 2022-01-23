package com.java110.things.netty;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class Java110CarProtocol implements Serializable {


    private static final long serialVersionUID = 290429819350651974L;
    private long id;
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Java110CarProtocol(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Java110CarProtocol() {

    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
