package com.java110.things.service.attendance;

import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IDealQunyingGetService
 * @Description TODO 考勤机心跳接口类
 * @Author wuxw
 * @Date 2020/5/26 17:38
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
public interface IAttendanceProcess {



    ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto);
    /**
     * 查询设备是否存在
     *
     * @param machineDto 设备信息
     * @return
     */
     void initMachine(MachineDto machineDto);



    /**
     * 重启设备
     * @param machineDto 设备信息
     */
    void restartAttendanceMachine(MachineDto machineDto);

    /**
     * 返回默认结果值，在没有指令的情况下返回设备的 结果值
     * @return
     */
    String getDefaultResult();
}
