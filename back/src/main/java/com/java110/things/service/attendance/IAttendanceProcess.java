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

    /**
     * 给设备下发指令
     *
     * @param machineDto
     * @return
     */
    ResultDto heartbeat(MachineDto machineDto);

    /**
     * 设备上报数据
     * @param attendanceUploadDto 设备上报数据封装
     * @return
     */
    ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto);
}
