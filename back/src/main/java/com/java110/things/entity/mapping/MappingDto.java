package com.java110.things.entity.mapping;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName MappingDto 映射类
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/18 10:59
 * @Version 1.0
 * add by wuxw 2020/5/18
 **/
public class MappingDto extends PageDto implements Serializable {

    private String id;

    private String domain;

    private String name;

    private String key;

    private String value;

    private String remark;

    private String createTime;


    private String statusCd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
