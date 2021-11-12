package com.java110.things.entity.attendance;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName AttendanceClassesDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 17:33
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
public class AttendanceClassesAttrDto extends PageDto implements Serializable {

    private String attrId;
    private String classesId;
    private String specCd;
    private String value;
    private String createTime;
    private String statusCd;

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getClassesId() {
        return classesId;
    }

    public void setClassesId(String classesId) {
        this.classesId = classesId;
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
