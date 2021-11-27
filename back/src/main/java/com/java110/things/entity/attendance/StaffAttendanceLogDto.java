package com.java110.things.entity.attendance;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.Date;

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
    private String staffName;
    private String departmentId;
    private String departmentName;
    private String clockTime;
    private String reqParam;
    private String createTime;
    private Date startDate;
    private Date endDate;

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

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
