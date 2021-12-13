package com.java110.things.Controller.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.attendance.*;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.service.attendance.IAttendanceService;
import com.java110.things.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
                                             @RequestParam int row,
                                             @RequestParam(name = "classesName", required = false) String classesName
                                            ) throws Exception {


        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setPage(page);
        attendanceClassesDto.setRow(row);
        attendanceClassesDto.setClassesName(classesName);

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
     * 删除班次
     *
     * @param classesId 表id
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteAttClass", method = RequestMethod.POST)
    public ResponseEntity<String> deleteAttClass(@RequestBody String classesId) throws Exception {
        JSONObject paramObj = super.getParamJson(classesId);
        Assert.hasKeyAndValue(paramObj, "classesId", "请求报文中未包含班次ID");
        ResultDto resultDto = attendanceServiceImpl.deleteAttendanceClassesDto(BeanConvertUtil.covertBean(paramObj, AttendanceClassesDto.class));
        return super.createResponseEntity(resultDto);
    }

    /*
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


    /**
     * 更新班次
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateAttClass", method = RequestMethod.POST)
    public ResponseEntity<String> updateAttClass(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "classesId", "请求报文中未包含班Id");
        Assert.hasKeyAndValue(paramObj, "classesName", "请求报文中未包含班次名");
        Assert.hasKeyAndValue(paramObj, "timeOffset", "请求报文中未包含打卡时间范围");
        Assert.hasKeyAndValue(paramObj, "clockCount", "请求报文中未包含打卡次数");
        Assert.hasKeyAndValue(paramObj, "clockType", "请求报文中未包含打卡类型");
        Assert.hasKeyAndValue(paramObj, "clockTypeValue", "请求报文中未包含打卡规则");
        Assert.hasKeyAndValue(paramObj, "lateOffset", "请求报文中未包含迟到时间范围");
        Assert.hasKeyAndValue(paramObj, "leaveOffset", "请求报文中未包含早退时间范围");
        ResultDto resultDto = attendanceServiceImpl.updateAttendanceClasses(BeanConvertUtil.covertBean(paramObj, AttendanceClassesDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加班次
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/insertAttClass", method = RequestMethod.POST)
    public ResponseEntity<String> insertAttClass(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "classesName", "请求报文中未包含班次名称");
        Assert.hasKeyAndValue(paramObj, "timeOffset", "请求报文中未包含打卡范围");
        Assert.hasKeyAndValue(paramObj, "clockCount", "请求报文中未包含打卡次数");
        Assert.hasKeyAndValue(paramObj, "clockType", "请求报文中未包含打卡类型");
        Assert.hasKeyAndValue(paramObj, "clockTypeValue", "请求报文中未包含打卡规则");
        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setClassesId(SeqUtil.getId());
        attendanceClassesDto.setClassesName(paramObj.get("classesName").toString());
        attendanceClassesDto.setStatusCd("0");
        attendanceClassesDto.setTimeOffset(paramObj.get("timeOffset").toString());
        attendanceClassesDto.setClockCount(paramObj.get("clockCount").toString());
        attendanceClassesDto.setClockType(paramObj.get("clockType").toString());
        attendanceClassesDto.setClockTypeValue(paramObj.get("clockTypeValue").toString());
        attendanceClassesDto.setLateOffset(paramObj.get("lateOffset").toString());
        attendanceClassesDto.setLeaveOffset(paramObj.get("leaveOffset").toString());
        List<AttendanceClassesAttrDto> attrDtoList = new ArrayList<>();
        AttendanceClassesAttrDto attrDto = new AttendanceClassesAttrDto();
        String cc = paramObj.get("clockCount").toString();
        if (cc.equals("2")){
            Assert.hasKeyAndValue(paramObj, "startTime1", "请求报文中未包含上午上班打卡时间10000");
            Assert.hasKeyAndValue(paramObj, "endTime1", "请求报文中未包含下午下班打卡时间20000");
            attrDto.setSpecCd("10000");
            attrDto.setValue(paramObj.get("startTime1").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("20000");
            attrDto.setValue(paramObj.get("endTime1").toString());
            attrDtoList.add(attrDto);
        }
        if (cc.equals("4")){
            Assert.hasKeyAndValue(paramObj, "startTime1", "请求报文中未包含上午上班打卡时间10000");
            Assert.hasKeyAndValue(paramObj, "endTime1", "请求报文中未包含下午下班打卡时间20000");
            Assert.hasKeyAndValue(paramObj, "startTime2", "请求报文中未包含中午上班打卡时间21000");
            Assert.hasKeyAndValue(paramObj, "endTime2", "请求报文中未包含中午下班打卡时间11000");
            attrDto.setSpecCd("10000");
            attrDto.setValue(paramObj.get("startTime1").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("20000");
            attrDto.setValue(paramObj.get("endTime1").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("21000");
            attrDto.setValue(paramObj.get("startTime2").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("11000");
            attrDto.setValue(paramObj.get("endTime2").toString());
            attrDtoList.add(attrDto);
        }
        if (cc.equals("6")){
            Assert.hasKeyAndValue(paramObj, "startTime1", "请求报文中未包含上午上班打卡时间10000");
            Assert.hasKeyAndValue(paramObj, "endTime1", "请求报文中未包含下午下班打卡时间20000");
            Assert.hasKeyAndValue(paramObj, "startTime2", "请求报文中未包含中午上班打卡时间21000");
            Assert.hasKeyAndValue(paramObj, "endTime2", "请求报文中未包含中午下班打卡时间11000");
            Assert.hasKeyAndValue(paramObj, "startTime3", "请求报文中未包含晚上上班打卡时间12000");
            Assert.hasKeyAndValue(paramObj, "endTime3", "请求报文中未包含晚上下班打卡时间22000");
            attrDto.setSpecCd("10000");
            attrDto.setValue(paramObj.get("startTime1").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("20000");
            attrDto.setValue(paramObj.get("endTime1").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("21000");
            attrDto.setValue(paramObj.get("startTime2").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("11000");
            attrDto.setValue(paramObj.get("endTime2").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("12000");
            attrDto.setValue(paramObj.get("startTime3").toString());
            attrDtoList.add(attrDto);
            attrDto = new AttendanceClassesAttrDto();
            attrDto.setSpecCd("22000");
            attrDto.setValue(paramObj.get("endTime3").toString());
            attrDtoList.add(attrDto);
        }
        ResultDto resultDto = attendanceServiceImpl.insertAttendanceClassesDto(attendanceClassesDto,attrDtoList);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 返回上下班时间配置
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getClassesAttrs", method = RequestMethod.GET)
    public ResponseEntity<String> getClassesAttrs(@RequestParam String classId) throws Exception {
        AttendanceClassesAttrDto attrDto = new AttendanceClassesAttrDto();
        attrDto.setClassesId(classId);
        ResultDto resultDto = attendanceServiceImpl.getAttendanceClassesAttrs(attrDto);
        return super.createResponseEntity(resultDto);
    }
}
