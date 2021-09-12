package com.java110.things.entity.attendance;

import java.io.Serializable;

/**
 * @ClassName AttendanceUplaodDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/27 16:33
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class AttendanceUploadDto implements Serializable {

    //上报设备编码
    private String machineCode;

    //设备上报数据，每个设备协议不太一样这里定义为string 请在自己的实现类中去格式化成自己的格式
    private String data;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
