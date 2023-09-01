package com.java110.things.dao;

import com.java110.things.entity.attendance.AttendanceClassesAttrDto;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesStaffDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskAttrDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDetailDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDto;
import com.java110.things.entity.attendance.StaffAttendanceLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IAttendanceClassesServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 17:32
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Mapper
public interface IAttendanceClassesServiceDao {

    /**
     * 保存设备人脸信息
     *
     * @param attendanceClassesDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveAttendanceClasses(AttendanceClassesDto attendanceClassesDto);

    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesDto> getAttendanceClassess(AttendanceClassesDto attendanceClassesDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param attendanceClassesDto 设备人脸信息
     * @return
     */
    long getAttendanceClassesCount(AttendanceClassesDto attendanceClassesDto);

    /**
     * 修改设备人脸信息
     *
     * @param attendanceClassesDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateAttendanceClasses(AttendanceClassesDto attendanceClassesDto);


    /**
     * 保存设备人脸信息
     *
     * @param attendanceClassesAttrDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveAttendanceClassesAttr(AttendanceClassesAttrDto attendanceClassesAttrDto);

    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesAttrDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesAttrDto> getAttendanceClassesAttrs(AttendanceClassesAttrDto attendanceClassesAttrDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param attendanceClassesAttrDto 设备人脸信息
     * @return
     */
    long getAttendanceClassesAttrCount(AttendanceClassesAttrDto attendanceClassesAttrDto);

    /**
     * 修改设备人脸信息
     *
     * @param attendanceClassesAttrDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateAttendanceClassesAttrDto(AttendanceClassesAttrDto attendanceClassesAttrDto);




    /**
     * 保存设备人脸信息
     *
     * @param attendanceClassesStaffDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveAttendanceClassesStaff(AttendanceClassesStaffDto attendanceClassesStaffDto);

    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesStaffDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesStaffDto> getAttendanceClassesStaffs(AttendanceClassesStaffDto attendanceClassesStaffDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param attendanceClassesStaffDto 设备人脸信息
     * @return
     */
    long getAttendanceClassesStaffCount(AttendanceClassesStaffDto attendanceClassesStaffDto);

    /**
     * 修改设备人脸信息
     *
     * @param attendanceClassesStaffDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateAttendanceClassesStaffDto(AttendanceClassesStaffDto attendanceClassesStaffDto);




    /**
     * 保存设备人脸信息
     *
     * @param attendanceClassesTaskDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveAttendanceClassesTask(AttendanceClassesTaskDto attendanceClassesTaskDto);

    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesTaskDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesTaskDto> getAttendanceClassesTasks(AttendanceClassesTaskDto attendanceClassesTaskDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param attendanceClassesTaskDto 设备人脸信息
     * @return
     */
    long getAttendanceClassesTaskCount(AttendanceClassesTaskDto attendanceClassesTaskDto);

    /**
     * 修改设备人脸信息
     *
     * @param attendanceClassesTaskDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateAttendanceClassesTaskDto(AttendanceClassesTaskDto attendanceClassesTaskDto);


    /**
     * 保存设备人脸信息
     *
     * @param attendanceClassesTaskAttrDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveAttendanceClassesTaskAttr(AttendanceClassesTaskAttrDto attendanceClassesTaskAttrDto);

    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesTaskAttrDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesTaskAttrDto> getAttendanceClassesTaskAttrs(AttendanceClassesTaskAttrDto attendanceClassesTaskAttrDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param attendanceClassesTaskAttrDto 设备人脸信息
     * @return
     */
    long getAttendanceClassesTaskAttrCount(AttendanceClassesTaskAttrDto attendanceClassesTaskAttrDto);

    /**
     * 修改设备人脸信息
     *
     * @param attendanceClassesTaskAttrDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateAttendanceClassesTaskAttrDto(AttendanceClassesTaskAttrDto attendanceClassesTaskAttrDto);


    /**
     * 保存设备人脸信息
     *
     * @param attendanceClassesTaskDetailDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveAttendanceClassesTaskDetail(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto);

    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesTaskDetailDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesTaskDetailDto> getAttendanceClassesTaskDetails(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param attendanceClassesTaskDetailDto 设备人脸信息
     * @return
     */
    long getAttendanceClassesTaskDetailCount(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto);

    /**
     * 修改设备人脸信息
     *
     * @param attendanceClassesTaskDetailDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateAttendanceClassesTaskDetailDto(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto);


    /**
     * 保存员工考勤日志
     *
     * @param staffAttendanceLogDto 员工考勤日志对象
     * @return 返回影响记录数
     */
    int saveStaffAttendanceLog(StaffAttendanceLogDto staffAttendanceLogDto);

    /**
     * 查询员工考勤日志
     *
     * @param staffAttendanceLogDto 员工考勤日志对象
     * @return
     */
    List<StaffAttendanceLogDto> getStaffAttendanceLogs(StaffAttendanceLogDto staffAttendanceLogDto);

    /**
     * 查询员工考勤日志
     *
     * @param staffAttendanceLogDto 设备人脸信息
     * @return
     */
    long getStaffAttendanceLogCount(StaffAttendanceLogDto staffAttendanceLogDto);


    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesTaskDto 设备人脸信息
     * @return
     */
    List<AttendanceClassesTaskDto> getMonthAttendance(AttendanceClassesTaskDto attendanceClassesTaskDto);


    /**
     * 查询设备人脸信息
     *
     * @param attendanceClassesTaskDto 设备人脸信息
     * @return
     */
    long getMonthAttendanceCount(AttendanceClassesTaskDto attendanceClassesTaskDto);

    int deleteAttendanceClasses(String value);


    /**
     *删除班次属性
     * @param attendanceClassesAttrDto
     * @return
     */
    int deleteAttendanceClassesAttr(AttendanceClassesAttrDto attendanceClassesAttrDto);
}
