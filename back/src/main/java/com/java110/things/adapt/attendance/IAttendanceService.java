package com.java110.things.adapt.attendance;

import com.java110.things.entity.attendance.*;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;

import java.util.List;

/**
 * @ClassName IAttendanceService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/29 18:34
 * @Version 1.0
 * add by wuxw 2020/5/29
 **/
public interface IAttendanceService {


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


    /**
     * 查询班次
     * @param attendanceClassesDto 班次信息
     * @return
     */
    ResultDto getClasses(AttendanceClassesDto attendanceClassesDto);


    List<AttendanceClassesDto> getAttendanceClasses(AttendanceClassesDto attendanceClassesDto);


    /**
     * 查询班次
     * @param staffDto 班次信息
     * @return
     */
    ResultDto getDepartments(StaffDto staffDto);

    /**
     * 查询员工
     * @param staffDto 班次信息
     * @return
     */
    ResultDto getStaffs(StaffDto staffDto);

    /**
     * 查询员工
     * @param staffDto 班次信息
     * @return
     */
    List<StaffDto> queryStaffs(StaffDto staffDto);



    /**
     * 保存班次员工
     * @param attendanceClassesStaffDto 班次信息
     * @return
     */
    ResultDto saveClassStaff(AttendanceClassesStaffDto attendanceClassesStaffDto) throws Exception;


    /**
     * 删除班次员工
     * @param attendanceClassesStaffDto 班次信息
     * @return
     */
    ResultDto deleteClassStaff(AttendanceClassesStaffDto attendanceClassesStaffDto);

    /**
     * 查询班次
     * @param attendanceClassesStaffDto 班次信息
     * @return
     */
    ResultDto getClassStaffs(AttendanceClassesStaffDto attendanceClassesStaffDto);

    /**
     * 查询班次
     * @param attendanceClassesStaffDto 班次信息
     * @return
     */
    List<AttendanceClassesStaffDto>  queryClassStaffs(AttendanceClassesStaffDto attendanceClassesStaffDto);


    /**
     * 查询考勤情况
     * @param attendanceClassesTaskDto 班次信息
     * @return
     */
    ResultDto getAttendanceTasks(AttendanceClassesTaskDto attendanceClassesTaskDto);


    /**
     * 查询考勤情况
     * @param attendanceClassesTaskDto 班次信息
     * @return
     */
    ResultDto getMonthAttendance(AttendanceClassesTaskDto attendanceClassesTaskDto);

    ResultDto insertAttendanceClassesDto(AttendanceClassesDto attendanceClassesDto, List<AttendanceClassesAttrDto> attrDtos);

    ResultDto saveAttendanceClassesAttrDto(AttendanceClassesAttrDto attrDto);

    ResultDto deleteAttendanceClassesDto(AttendanceClassesDto attendanceClassesDto);
    ResultDto deleteAttendanceClassesAttrDto(AttendanceClassesAttrDto attendanceClassesAttrDto);

    public ResultDto updateAttendanceClasses(AttendanceClassesDto attendanceClassesDto);

    /**
     * 查询考勤记录
     * @param staffAttendanceLogDto 班次信息
     * @return
     */
    ResultDto getStaffAttendanceLog(StaffAttendanceLogDto staffAttendanceLogDto);

    ResultDto getAttendanceClassesAttrs(AttendanceClassesAttrDto attrDto);
}
