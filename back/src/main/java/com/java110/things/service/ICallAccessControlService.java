package com.java110.things.service;

import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.OperateLogDto;

/**
 * @ClassName INotifyAccessControlServcie
 * @Description TODO 门禁硬件回调 接口类
 * @Author wuxw
 * @Date 2020/5/15 19:12
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
public interface ICallAccessControlService {

    /**
     * 门禁 上报,当门禁上线时 建议将门禁自动上报，系统管理页面和云端可以自动注册设备
     * @param machineDto 设备对象
     */
    void uploadMachine(MachineDto machineDto);

    /**
     * 记录操作日志
     * @param operateLogDto 日志对象，当logId 在数据库中不存在是做添加，存在时 做修改
     */
    void saveOrUpdateOperateLog(OperateLogDto operateLogDto);
}
