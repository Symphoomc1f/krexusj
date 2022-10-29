package com.java110.things.entity.attendance;

import java.io.Serializable;

/**
 * @ClassName ClockInDto 考勤对象类
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/9 0:00
 * @Version 1.0
 * add by wuxw 2020/6/9
 **/
public class ClockInDto implements Serializable {

    private String staffId;

    private String pic;

    private String clockInTime;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }
}
