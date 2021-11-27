package com.java110.things.Controller.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesStaffDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDto;
import com.java110.things.entity.attendance.StaffAttendanceLogDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.service.attendance.IAttendanceService;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName AttendanceController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/9 12:40
 * @Version 1.0
 * add by wuxw 2020/6/9
 **/
@RestController
@RequestMapping(path = "/api/attendance")
public class AttendanceController extends BaseController {

    @Autowired
    private IAttendanceService attendanceServiceImpl;

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getClasses", method = RequestMethod.GET)
    public ResponseEntity<String> getClasses(@RequestParam int page,
                                             @RequestParam int row) throws Exception {


        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setPage(page);
        attendanceClassesDto.setRow(row);

        ResultDto resultDto = attendanceServiceImpl.getClasses(attendanceClassesDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getDepartments", method = RequestMethod.GET)
    public ResponseEntity<String> getDepartments(@RequestParam(name = "departmentId", required = false) String departmentId
    ) throws Exception {


        StaffDto staffDto = new StaffDto();
        staffDto.setDepartmentId(departmentId);
        ResultDto resultDto = attendanceServiceImpl.getDepartments(staffDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getStaffs", method = RequestMethod.GET)
    public ResponseEntity<String> getStaffs(@RequestParam(name = "departmentId", required = false) String departmentId
    ) throws Exception {


        StaffDto staffDto = new StaffDto();
        staffDto.setDepartmentId(departmentId);
        ResultDto resultDto = attendanceServiceImpl.getStaffs(staffDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveClassesStaffs", method = RequestMethod.POST)
    public ResponseEntity<String> saveClassesStaffs(@RequestBody String paramIn) throws Exception {

        JSONObject paramObj = JSONObject.parseObject(paramIn);
        AttendanceClassesStaffDto attendanceClassesStaffDto
                = BeanConvertUtil.covertBean(paramObj, AttendanceClassesStaffDto.class);
        attendanceClassesStaffDto.setCsId(SeqUtil.getId());
        ResultDto resultDto = attendanceServiceImpl.saveClassStaff(attendanceClassesStaffDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getClassesStaffs", method = RequestMethod.GET)
    public ResponseEntity<String> getClassesStaffs(
            @RequestParam int page,
            @RequestParam int row,
            @RequestParam(name = "classesId", required = false) String classesId,
            @RequestParam(name = "staffName", required = false) String staffName
    ) throws Exception {


        AttendanceClassesStaffDto attendanceClassesStaffDto = new AttendanceClassesStaffDto();
        attendanceClassesStaffDto.setPage(page);
        attendanceClassesStaffDto.setRow(row);
        attendanceClassesStaffDto.setClassesId(classesId);
        attendanceClassesStaffDto.setStaffName(staffName);
        ResultDto resultDto = attendanceServiceImpl.getClassStaffs(attendanceClassesStaffDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteClassesStaff", method = RequestMethod.POST)
    public ResponseEntity<String> deleteClassesStaff(@RequestBody String paramIn) throws Exception {

        JSONObject paramObj = JSONObject.parseObject(paramIn);
        AttendanceClassesStaffDto attendanceClassesStaffDto
                = BeanConvertUtil.covertBean(paramObj, AttendanceClassesStaffDto.class);
        ResultDto resultDto = attendanceServiceImpl.deleteClassStaff(attendanceClassesStaffDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getAttendanceTasks", method = RequestMethod.GET)
    public ResponseEntity<String> getAttendanceTasks(
            @RequestParam int page,
            @RequestParam int row,
            @RequestParam(name = "classesId", required = false) String classesId,
            @RequestParam(name = "staffName", required = false) String staffName,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "departmentId", required = false) String departmentId
    ) throws Exception {


        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setPage(page);
        attendanceClassesTaskDto.setRow(row);
        attendanceClassesTaskDto.setClassId(classesId);
        attendanceClassesTaskDto.setStaffName(staffName);
        attendanceClassesTaskDto.setDepartmentId(departmentId);
        if (!StringUtil.isEmpty(date)) {
            Date reqDate = DateUtil.getDateFromString(date, DateUtil.DATE_FORMATE_STRING_B);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reqDate);
            attendanceClassesTaskDto.setTaskYear(calendar.get(Calendar.YEAR) + "");
            attendanceClassesTaskDto.setTaskMonth((calendar.get(Calendar.MONTH) + 1) + "");
            attendanceClassesTaskDto.setTaskDay(calendar.get(Calendar.DAY_OF_MONTH) + "");
        }
        ResultDto resultDto = attendanceServiceImpl.getAttendanceTasks(attendanceClassesTaskDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMonthAttendance", method = RequestMethod.GET)
    public ResponseEntity<String> getMonthAttendance(
            @RequestParam int page,
            @RequestParam int row,
            @RequestParam(name = "classesId", required = false) String classesId,
            @RequestParam(name = "staffName", required = false) String staffName,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "departmentId", required = false) String departmentId
    ) throws Exception {

        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setPage(page);
        attendanceClassesTaskDto.setRow(row);
        attendanceClassesTaskDto.setClassId(classesId);
        attendanceClassesTaskDto.setStaffName(staffName);
        attendanceClassesTaskDto.setDepartmentId(departmentId);
        if (!StringUtil.isEmpty(date)) {
            Date reqDate = DateUtil.getDateFromString(date, DateUtil.DATE_FORMATE_STRING_B);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reqDate);
            attendanceClassesTaskDto.setTaskYear(calendar.get(Calendar.YEAR) + "");
            attendanceClassesTaskDto.setTaskMonth((calendar.get(Calendar.MONTH) + 1) + "");
        }
        ResultDto resultDto = attendanceServiceImpl.getMonthAttendance(attendanceClassesTaskDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getStaffAttendanceLog", method = RequestMethod.GET)
    public ResponseEntity<String> getStaffAttendanceLog(
            @RequestParam int page,
            @RequestParam int row,
            @RequestParam(name = "staffName", required = false) String staffName,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "departmentId", required = false
            ) String departmentId,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) throws Exception {

        StaffAttendanceLogDto staffAttendanceLogDto = new StaffAttendanceLogDto();
        staffAttendanceLogDto.setPage(page);
        staffAttendanceLogDto.setRow(row);
        staffAttendanceLogDto.setStaffName(staffName);
        staffAttendanceLogDto.setDepartmentId(departmentId);
        if (!StringUtil.isEmpty(startDate)) {
            staffAttendanceLogDto.setStartDate(DateUtil.getDateFromString(startDate, DateUtil.DATE_FORMATE_STRING_B));
            staffAttendanceLogDto.setEndDate(DateUtil.getDateFromString(endDate, DateUtil.DATE_FORMATE_STRING_B));
        }

        ResultDto resultDto = attendanceServiceImpl.getStaffAttendanceLog(staffAttendanceLogDto);
        return super.createResponseEntity(resultDto);
    }


}
