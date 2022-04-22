package com.java110.things.service.app;

import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IAppService {

    /**
     * 保存设备指令信息
     *
     * @param machineCmdDto 设备指令信息
     * @return
     * @throws Exception
     */
    ResultDto saveApp(MachineCmdDto machineCmdDto) throws Exception;

    /**
     * 获取设备指令信息
     *
     * @param machineCmdDto 设备指令信息
     * @return
     * @throws Exception
     */
    ResultDto getMachineCmd(MachineCmdDto machineCmdDto) throws Exception;

    /**
     * 修改设备指令
     *
     * @param machineCmdDto 设备指令信息
     * @return
     * @throws Exception
     */
    ResultDto updateMachineCmd(MachineCmdDto machineCmdDto) throws Exception;

    /**
     * 删除指令
     *
     * @param machineCmdDto 设备指令信息
     * @return
     * @throws Exception
     */
    ResultDto deleteMachineCmd(MachineCmdDto machineCmdDto)  throws Exception;
}
