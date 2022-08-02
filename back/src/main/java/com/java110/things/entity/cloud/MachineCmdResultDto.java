package com.java110.things.entity.cloud;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName MachineUploadFaceDto
 * @Description TODO 设备命令执行结果上报
 * @Author wuxw
 * @Date 2020/5/27 8:31
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class MachineCmdResultDto implements Serializable {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = 0;

    private int code;

    private String msg;

    private String taskid;

    private String machineCode;
    private String resJson;


    public MachineCmdResultDto(int code, String msg, String taskid, String machineCode, String resJson) {
        this.code = code;
        this.msg = msg;
        this.taskid = taskid;
        this.machineCode = machineCode;
        this.resJson = resJson;
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

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public String getResJson() {
        return resJson;
    }

    public void setResJson(String resJson) {
        this.resJson = resJson;
    }
}
