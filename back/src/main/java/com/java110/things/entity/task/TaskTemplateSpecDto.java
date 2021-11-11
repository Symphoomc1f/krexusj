package com.java110.things.entity.task;

import java.io.Serializable;

/**
 * @ClassName TaskTemplateSpecDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 9:10
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
public class TaskTemplateSpecDto implements Serializable {

    private String specId;
    private String templateId;
    private String specCd;
    private String specName;
    private String specDesc;
    private String isShow;
    private String createTime;

    private String statusCd;

    public String getSpecId() {
        return specId;
    }

    public void setSpecId(String specId) {
        this.specId = specId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecDesc() {
        return specDesc;
    }

    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
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
