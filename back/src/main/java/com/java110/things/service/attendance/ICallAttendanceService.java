package com.java110.things.service.attendance;

import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;

import java.util.List;

/**
 * @ClassName ICallAttendanceService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/27 15:40
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public interface ICallAttendanceService {

    /**
     * 查询设备信息
     * @param machineDto 查询条件一般传 machineCode 设备编码
     * @return 返回设备信息，查询不到时返回null
     */
    MachineDto getMachine(MachineDto machineDto);


    /**
     * 门禁 上报,当门禁上线时 建议将门禁自动上报，系统管理页面和云端可以自动注册设备
     * @param machineDto 设备对象
     */
    void uploadMachine(MachineDto machineDto);

    /**
     * 查询设备指令
     * @param machineCmdDto 设备信息
     * @return 返回设备指令
     */
    List<MachineCmdDto> getMachineCmds(MachineCmdDto machineCmdDto) throws Exception;

    /**
     * 插入设备指令
     * @param machineCmdDto
     * @throws Exception
     */
    void insertMachineCmd(MachineCmdDto machineCmdDto) throws Exception;
}
