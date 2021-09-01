package com.java110.things.service.attendance;

import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineDto;

/**
 * @ClassName IDealQunyingGetService
 * @Description TODO 考勤机心跳接口类
 * @Author wuxw
 * @Date 2020/5/26 17:38
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
public interface IAttendanceProcess {

    /**
     * 给设备下发指令
     * @param machineDto
     * @return
     */
    ResultQunyingDto heartbeat(MachineDto machineDto);
}
