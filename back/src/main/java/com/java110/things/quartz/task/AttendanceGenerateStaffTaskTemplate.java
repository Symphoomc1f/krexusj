package com.java110.things.quartz.task;

import com.java110.things.dao.IAttendanceClassesServiceDao;
import com.java110.things.entity.attendance.AttendanceClassesAttrDto;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesStaffDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDetailDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.quartz.TaskSystemQuartz;
import com.java110.things.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * @ClassName AttendanceGenerateStaffTaskTemplate
 * @Description TODO 生成考勤任务 任务模板
 * @Author wuxw
 * @Date 2020/6/8 12:29
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class AttendanceGenerateStaffTaskTemplate extends TaskSystemQuartz {

    protected static final Logger logger = LoggerFactory.getLogger(AttendanceGenerateStaffTaskTemplate.class);

    private static final String CLOCK_COUNT_TWO = "2"; //打卡两次 早晚

    private static final String CLOCK_COUNT_FOUR = "4"; //打卡四次

    private static final String CLOCK_COUNT_SIX = "6"; //打卡四次

    private static final String CLOCK_TIME_MORNING_WORK = "10000";//上午上班打卡
    private static final String CLOCK_TIME_AFTERNOON_OFF_DUTY = "20000";//下午下班打卡
    private static final String CLOCK_TIME_NOON_OFF_DUTY = "11000";//中午下班打卡
    private static final String CLOCK_TIME_NOON_WORK = "21000";//中午上班打卡
    private static final String CLOCK_TIME_NIGHT_WORK = "12000";//晚上上班打卡
    private static final String CLOCK_TIME_NIGHT_OFF_DUTY = "22000";//晚上下班打卡

    private static final String CLOCK_TYPE_EVERY_DAY = "1001";//每天打卡
    private static final String CLOCK_TYPE_NEXT_DAY = "1002";//隔天打卡
    private static final String CLOCK_TYPE_CUSTOM_DAY = "1003";//自定义


    @Autowired
    private IAttendanceClassesServiceDao attendanceClassesServiceDao;

    @Override
    protected void process(TaskDto taskDto) throws Exception {
        logger.debug("AttendanceGenerateStaffTaskTemplate定时任务在执行" + taskDto.getTaskName());

        //1.0查询班次表
        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setStatusCd("0");
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceClassesServiceDao.getAttendanceClassess(attendanceClassesDto);

        if (attendanceClassesDtos == null || attendanceClassesDtos.size() < 1) {
            return;
        }

        for (AttendanceClassesDto tmpAttendanceClassesDto : attendanceClassesDtos) {
            doDealClasses(tmpAttendanceClassesDto);
        }

    }


    /**
     * 根据班次 生成班次考勤任务
     *
     * @param tmpAttendanceClassesDto
     */
    private void doDealClasses(AttendanceClassesDto tmpAttendanceClassesDto) {

        String clockType = tmpAttendanceClassesDto.getClockType();
        if (CLOCK_TYPE_EVERY_DAY.equals(clockType)) {
            //每天 不处理
        } else if (CLOCK_TYPE_NEXT_DAY.equals(clockType)) { //隔天
            AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
            attendanceClassesTaskDto.setClassId(tmpAttendanceClassesDto.getClassesId());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            attendanceClassesTaskDto.setTaskYear(calendar.get(Calendar.YEAR) + "");
            attendanceClassesTaskDto.setTaskMonth((calendar.get(Calendar.MONTH) + 1) + "");
            attendanceClassesTaskDto.setTaskDay(calendar.get(Calendar.DATE) + "");
            long count = attendanceClassesServiceDao.getAttendanceClassesTaskCount(attendanceClassesTaskDto);

            if (count > 0) { //昨天已经生成今天无需生成考勤记录
                return;
            }
        } else if (CLOCK_TYPE_CUSTOM_DAY.equals(clockType)) { //星期自定义
            String clockTypeValue = tmpAttendanceClassesDto.getClockTypeValue();
            Calendar calendar = Calendar.getInstance();
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
            if (!clockTypeValue.contains(weekDay + "")) {
                return;
            }
        }
        AttendanceClassesAttrDto attendanceClassesAttrDto = new AttendanceClassesAttrDto();
        attendanceClassesAttrDto.setClassesId(tmpAttendanceClassesDto.getClassesId());
        List<AttendanceClassesAttrDto> attendanceClassesAttrDtos = attendanceClassesServiceDao.getAttendanceClassesAttrs(attendanceClassesAttrDto);

        generateClockTask(tmpAttendanceClassesDto, attendanceClassesAttrDtos);


    }

    /**
     * 生成两次打卡任务
     *
     * @param tmpAttendanceClassesDto
     * @param attendanceClassesAttrDtos
     */
    private void generateClockTask(AttendanceClassesDto tmpAttendanceClassesDto, List<AttendanceClassesAttrDto> attendanceClassesAttrDtos) {
        String clockCount = tmpAttendanceClassesDto.getClockCount();
        AttendanceClassesStaffDto attendanceClassesStaffDto = new AttendanceClassesStaffDto();
        attendanceClassesStaffDto.setClassesId(tmpAttendanceClassesDto.getClassesId());
        attendanceClassesStaffDto.setStatusCd("0");
        List<AttendanceClassesStaffDto> attendanceClassesStaffDtos = attendanceClassesServiceDao.getAttendanceClassesStaffs(attendanceClassesStaffDto);
        if (attendanceClassesStaffDtos == null || attendanceClassesAttrDtos.size() < 1) {
            return;
        }
        String startTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_MORNING_WORK);
        String endTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_AFTERNOON_OFF_DUTY);
        for (AttendanceClassesStaffDto tmpAttendanceClassesStaffDto : attendanceClassesStaffDtos) {
            AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
            Calendar calendar = Calendar.getInstance();
            attendanceClassesTaskDto.setTaskYear(calendar.get(Calendar.YEAR) + "");
            attendanceClassesTaskDto.setTaskMonth((calendar.get(Calendar.MONTH) + 1) + "");
            attendanceClassesTaskDto.setTaskDay(calendar.get(Calendar.DATE) + "");
            attendanceClassesTaskDto.setStaffId(tmpAttendanceClassesStaffDto.getStaffId());
            attendanceClassesTaskDto.setClassId(tmpAttendanceClassesStaffDto.getClassesId());
            long count = attendanceClassesServiceDao.getAttendanceClassesTaskCount(attendanceClassesTaskDto);
            if (count > 0) {
                continue;//已经生成过任务 不能再生成任务
            }
            String taskId = generateStaffClockTask(tmpAttendanceClassesStaffDto);
            switch (clockCount) {
                case CLOCK_COUNT_TWO: //打卡两次
                    insertStaffTaskDetail(startTime, endTime, taskId);
                    break;
                case CLOCK_COUNT_FOUR: //打卡四次
                    insertStaffTaskDetail(startTime, endTime, taskId);
                    startTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_NOON_OFF_DUTY);
                    endTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_NOON_WORK);
                    insertStaffTaskDetail(startTime, endTime, taskId);
                    break;
                case CLOCK_COUNT_SIX: //打卡六次
                    insertStaffTaskDetail(startTime, endTime, taskId);
                    startTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_NOON_OFF_DUTY);
                    endTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_NOON_WORK);
                    insertStaffTaskDetail(startTime, endTime, taskId);
                    startTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_NIGHT_WORK);
                    endTime = getClassessAttrValue(attendanceClassesAttrDtos, CLOCK_TIME_NIGHT_OFF_DUTY);
                    insertStaffTaskDetail(startTime, endTime, taskId);
                    break;
            }
        }
    }

    /**
     * 插入考勤明细
     *
     * @param startTime
     * @param endTime
     */
    private void insertStaffTaskDetail(String startTime, String endTime, String taskId) {
        AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
        attendanceClassesTaskDetailDto.setTaskId(taskId);
        attendanceClassesTaskDetailDto.setDetailId(SeqUtil.getId());
        attendanceClassesTaskDetailDto.setSpecCd(CLOCK_TIME_MORNING_WORK);
        attendanceClassesTaskDetailDto.setValue(startTime);
        attendanceClassesTaskDetailDto.setState("10000");
        attendanceClassesServiceDao.saveAttendanceClassesTaskDetail(attendanceClassesTaskDetailDto);
         attendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
        attendanceClassesTaskDetailDto.setTaskId(taskId);
        attendanceClassesTaskDetailDto.setDetailId(SeqUtil.getId());
        attendanceClassesTaskDetailDto.setSpecCd(CLOCK_TIME_AFTERNOON_OFF_DUTY);
        attendanceClassesTaskDetailDto.setValue(endTime);
        attendanceClassesTaskDetailDto.setState("10000");
        attendanceClassesServiceDao.saveAttendanceClassesTaskDetail(attendanceClassesTaskDetailDto);
    }

    /**
     * 生成 考勤任务
     *
     * @param tmpAttendanceClassesStaffDto
     */
    private String generateStaffClockTask(AttendanceClassesStaffDto tmpAttendanceClassesStaffDto) {
        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setClassId(tmpAttendanceClassesStaffDto.getClassesId());
        attendanceClassesTaskDto.setStaffId(tmpAttendanceClassesStaffDto.getStaffId());
        attendanceClassesTaskDto.setState("10000");
        Calendar calendar = Calendar.getInstance();
        attendanceClassesTaskDto.setTaskYear(calendar.get(Calendar.YEAR) + "");
        attendanceClassesTaskDto.setTaskMonth((calendar.get(Calendar.MONTH) + 1) + "");
        attendanceClassesTaskDto.setTaskDay(calendar.get(Calendar.DATE) + "");
        attendanceClassesTaskDto.setTaskId(SeqUtil.getId());
        attendanceClassesServiceDao.saveAttendanceClassesTask(attendanceClassesTaskDto);
        return attendanceClassesTaskDto.getTaskId();
    }

    private String getClassessAttrValue(List<AttendanceClassesAttrDto> attendanceClassesAttrDtos, String specCd) {
        for (AttendanceClassesAttrDto attendanceClassesAttrDto : attendanceClassesAttrDtos) {
            if (specCd.equals(attendanceClassesAttrDto.getSpecCd())) {
                return attendanceClassesAttrDto.getValue();
            }
        }

        return null;
    }
}
