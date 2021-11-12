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
public class AttendanceClassesDto extends PageDto implements Serializable {

    private String classesId;
    private String classesName;
    private String timeOffset;
    private String createTime;
    private String clockCount;
    private String clockType;
    private String clockTypeValue;
    private String statusCd;

    public String getClassesId() {
        return classesId;
    }

    public void setClassesId(String classesId) {
        this.classesId = classesId;
    }

    public String getClassesName() {
        return classesName;
    }

    public void setClassesName(String classesName) {
        this.classesName = classesName;
    }

    public String getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(String timeOffset) {
        this.timeOffset = timeOffset;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getClockCount() {
        return clockCount;
    }

    public void setClockCount(String clockCount) {
        this.clockCount = clockCount;
    }

    public String getClockType() {
        return clockType;
    }

    public void setClockType(String clockType) {
        this.clockType = clockType;
    }

    public String getClockTypeValue() {
        return clockTypeValue;
    }

    public void setClockTypeValue(String clockTypeValue) {
        this.clockTypeValue = clockTypeValue;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
