package com.java110.things.entity.attendance;

import java.io.Serializable;

/**
 * @ClassName ClockInDto 考勤对象类
 * @Description TODO 考勤结果 反馈dto 类
 * @Author wuxw
 * @Date 2020/6/9 0:00
 * @Version 1.0
 * add by wuxw 2020/6/9
 **/
public class ClockInResultDto implements Serializable {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -1;

    private int code;

    private String msg;

    public ClockInResultDto() {
    }

    public ClockInResultDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
