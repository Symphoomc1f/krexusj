package com.java110.things.entity.attendance;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName AttendanceClassesTaskDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 17:33
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
public class AttendanceClassesTaskDto extends PageDto implements Serializable {

    private String taskId;
    private String classId;
    private String staffId;
    private String taskYear;
    private String taskMonth;
    private String taskDay;
    private String state;
    private String[] states;
    private String createTime;
    private String statusCd;
    private String noClockIn;
    private String clockIn;
    private String late;
    private String early;
    private String free;


    private List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetails;

    private String staffName;
    private String departmentName;
    private String departmentId;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getTaskYear() {
        return taskYear;
    }

    public void setTaskYear(String taskYear) {
        this.taskYear = taskYear;
    }

    public String getTaskMonth() {
        return taskMonth;
    }

    public void setTaskMonth(String taskMonth) {
        this.taskMonth = taskMonth;
    }

    public String getTaskDay() {
        return taskDay;
    }

    public void setTaskDay(String taskDay) {
        this.taskDay = taskDay;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public List<AttendanceClassesTaskDetailDto> getAttendanceClassesTaskDetails() {
        return attendanceClassesTaskDetails;
    }

    public void setAttendanceClassesTaskDetails(List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetails) {
        this.attendanceClassesTaskDetails = attendanceClassesTaskDetails;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getNoClockIn() {
        return noClockIn;
    }

    public void setNoClockIn(String noClockIn) {
        this.noClockIn = noClockIn;
    }

    public String getClockIn() {
        return clockIn;
    }

    public void setClockIn(String clockIn) {
        this.clockIn = clockIn;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getEarly() {
        return early;
    }

    public void setEarly(String early) {
        this.early = early;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }
}
