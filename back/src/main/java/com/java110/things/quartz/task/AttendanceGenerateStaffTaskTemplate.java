package com.java110.things.quartz.task;

import com.java110.things.dao.IAttendanceClassesServiceDao;
import com.java110.things.entity.attendance.AttendanceClassesAttrDto;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesStaffDto;
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
        String clockCount = tmpAttendanceClassesDto.getClockCount();
        AttendanceClassesAttrDto attendanceClassesAttrDto = new AttendanceClassesAttrDto();
        attendanceClassesAttrDto.setClassesId(tmpAttendanceClassesDto.getClassesId());
        List<AttendanceClassesAttrDto> attendanceClassesAttrDtos = attendanceClassesServiceDao.getAttendanceClassesAttrs(attendanceClassesAttrDto);

        switch (clockCount) {
            case CLOCK_COUNT_TWO: //打卡两次
                generateTwoClockTask(tmpAttendanceClassesDto, attendanceClassesAttrDtos);
                break;
        }


    }

    /**
     * 生成两次打卡任务
     *
     * @param tmpAttendanceClassesDto
     * @param attendanceClassesAttrDtos
     */
    private void generateTwoClockTask(AttendanceClassesDto tmpAttendanceClassesDto, List<AttendanceClassesAttrDto> attendanceClassesAttrDtos) {

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
            if(count > 0){
                continue;//已经生成过任务 不能再生成任务
            }
            generateStaffClockTask(tmpAttendanceClassesStaffDto, startTime, endTime);
        }
    }

    /**
     * 生成 考勤任务
     * @param tmpAttendanceClassesStaffDto
     * @param startTime
     * @param endTime
     */
    private void generateStaffClockTask(AttendanceClassesStaffDto tmpAttendanceClassesStaffDto, String startTime, String endTime) {
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
