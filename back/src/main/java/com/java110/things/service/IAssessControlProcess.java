package com.java110.things.service;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.machine.MachineDto;

/**
 * 门禁处理接口类，各大门禁厂商 需要实现这个类，实现相应的方法
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface IAssessControlProcess {


    /**
     * 查询设备中 人脸数量
     *
     * @param machineDto 设备信息，其中包括设备编码，如 mac 设备信号，或者设备IP
     * @return 返回人脸数
     */
    int getFaceNum(MachineDto machineDto);




}
