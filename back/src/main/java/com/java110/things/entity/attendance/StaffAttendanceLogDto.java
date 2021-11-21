package com.java110.things.entity.attendance;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName StaffAttendanceLogDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/9 10:13
 * @Version 1.0
 * add by wuxw 2020/6/9
 **/
public class StaffAttendanceLogDto extends PageDto implements Serializable {

    private String logId;
    private String staffId;
    private String clockTime;
    private String reqParam;
    private String createTime;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }

    public String getReqParam() {
        return reqParam;
    }

    public void setReqParam(String reqParam) {
        this.reqParam = reqParam;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
