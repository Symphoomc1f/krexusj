package com.java110.things.entity.user;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName Staff
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/7 22:50
 * @Version 1.0
 * add by wuxw 2020/6/7
 **/
public class StaffDto extends PageDto implements Serializable {

    private String staffId;
    private String staffName;
    private String departmentId;
    private String departmentName;
    private String face1;
    private String face2;
    private String face3;
    private String extStaffId;

    private String statusCd;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
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

    public String getFace1() {
        return face1;
    }

    public void setFace1(String face1) {
        this.face1 = face1;
    }

    public String getFace2() {
        return face2;
    }

    public void setFace2(String face2) {
        this.face2 = face2;
    }

    public String getFace3() {
        return face3;
    }

    public void setFace3(String face3) {
        this.face3 = face3;
    }

    public String getExtStaffId() {
        return extStaffId;
    }

    public void setExtStaffId(String extStaffId) {
        this.extStaffId = extStaffId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
