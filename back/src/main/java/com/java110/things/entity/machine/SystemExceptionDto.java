package com.java110.things.entity.machine;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName OperateLogDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/22 14:46
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
public class SystemExceptionDto extends PageDto implements Serializable {

    public static final String EXCEPTION_TYPE_ACCESS_CONTROL = "AC";
    public static final String EXCEPTION_TYPE_CAR = "C";

    private String exceptionId;
    private String exceptionType;
    private String objId;
    private String errMsg;

    private String machineId;

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
