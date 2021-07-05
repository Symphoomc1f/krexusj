package com.java110.things.service;

import com.java110.things.entity.machine.MachineDto;

/**
 * @ClassName INotifyAccessControlServcie
 * @Description TODO 门禁硬件回调 接口类
 * @Author wuxw
 * @Date 2020/5/15 19:12
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
public interface INotifyAccessControlService {

    /**
     * 门禁 上报
     * @param machineDto 设备对象
     */
    void uploadMachine(MachineDto machineDto);
}
