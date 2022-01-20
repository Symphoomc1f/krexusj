package com.java110.things.netty;

import java.io.Serializable;

public class CustomProtocol implements Serializable {


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

    public CustomProtocol(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public CustomProtocol() {

    }

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
