package com.java110.things.entity.task;

import java.io.Serializable;

/**
 * @ClassName TaskDtoAttr
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 9:06
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
public class TaskAttrDto implements Serializable {

    private String taskId;
    private String attrId;
    private String specCd;
    private String value;
    private String createTime;
    private String statusCd;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
