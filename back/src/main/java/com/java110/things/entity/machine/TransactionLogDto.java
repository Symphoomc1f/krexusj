package com.java110.things.entity.machine;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName TransctionLogDto
 * @Description TODO 传输日志对象
 * @Author wuxw
 * @Date 2020/5/27 10:21
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class TransactionLogDto extends PageDto implements Serializable {

    private String tranId;
    private String machineId;
    private String url;
    private String reqHeader;
    private String reqParam;
    private String resHeader;
    private String resParam;
    private String reqTime;
    private String resTime;
    private String machineCode;
    private String machineName;

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReqHeader() {
        return reqHeader;
    }

    public void setReqHeader(String reqHeader) {
        this.reqHeader = reqHeader;
    }

    public String getReqParam() {
        return reqParam;
    }

    public void setReqParam(String reqParam) {
        this.reqParam = reqParam;
    }

    public String getResHeader() {
        return resHeader;
    }

    public void setResHeader(String resHeader) {
        this.resHeader = resHeader;
    }

    public String getResParam() {
        return resParam;
    }

    public void setResParam(String resParam) {
        this.resParam = resParam;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public String getResTime() {
        return resTime;
    }

    public void setResTime(String resTime) {
        this.resTime = resTime;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
