package com.java110.things.adapt.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IAttendanceClassesServiceDao;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDetailDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDto;
import com.java110.things.entity.attendance.ClockInDto;
import com.java110.things.entity.attendance.ClockInResultDto;
import com.java110.things.entity.attendance.StaffAttendanceLogDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.util.Assert;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName CallAttendanceServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/27 16:04
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
@Service("callAttendanceServiceImpl")
public class CallAttendanceServiceImpl implements ICallAttendanceService {

    private static Logger logger = LoggerFactory.getLogger(CallAttendanceServiceImpl.class);

    private static final int DEFAULT_PAGE = 1; // 默认获取指令 页
    private static final int DEFAULT_ROW = 5; //默认指令获取最大数

    private static final String CLOCK_COUNT_TWO = "2"; //打卡两次 早晚

    private static final String CLOCK_COUNT_FOUR = "4"; //打卡四次

    private static final String CLOCK_COUNT_SIX = "6"; //打卡四次

    private static final String CLOCK_TIME_MORNING_WORK = "10000";//上午上班打卡
    private static final String CLOCK_TIME_AFTERNOON_OFF_DUTY = "20000";//下午下班打卡
    private static final String CLOCK_TIME_NOON_OFF_DUTY = "11000";//中午下班打卡
    private static final String CLOCK_TIME_NOON_WORK = "21000";//中午上班打卡
    private static final String CLOCK_TIME_NIGHT_WORK = "12000";//晚上上班打卡
    private static final String CLOCK_TIME_NIGHT_OFF_DUTY = "22000";//晚上下班打卡
    public static final String FACE_RESULT = "-result";
    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IMachineCmdService machineCmdServiceImpl;


    @Autowired
    IMachineServiceDao machineServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IAttendanceClassesServiceDao attendanceClassesServiceDao;

    @Override
    public MachineDto getMachine(MachineDto machineDto) {

        List<MachineDto> machineDtos = machineServiceDao.getMachines(machineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            return null;
        }

        return machineDtos.get(0);

    }


    @Override
    public void uploadMachine(MachineDto machineDto) {

        if (machineDto == null) {
            throw new ServiceException(Result.SYS_ERROR, "设备信息不能为空");
        }

        logger.debug("machineDto", machineDto.toString());
        //如果设备ID为空的情况
        if (StringUtil.isEmpty(machineDto.getMachineId())) {
            machineDto.setMachineId(UUID.randomUUID().toString());
        }
        //设备编码
        if (StringUtil.isEmpty(machineDto.getMachineCode())) {
            throw new ServiceException(Result.SYS_ERROR, "未包含设备编码，如果设备没有编码可以写mac或者ip,只要标识唯一就好");
        }
        //设备IP
        if (StringUtil.isEmpty(machineDto.getMachineIp())) {
            throw new ServiceException(Result.SYS_ERROR, "未包含设备ip");
        }
        //设备mac
        if (StringUtil.isEmpty(machineDto.getMachineMac())) {
            machineDto.setMachineMac(machineDto.getMachineIp());
        }
        // 设备名称
        if (StringUtil.isEmpty(machineDto.getMachineName())) {
            machineDto.setMachineName(machineDto.getMachineCode());
        }
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);

        MachineDto tmpMachineDto = new MachineDto();
        tmpMachineDto.setMachineVersion(machineDto.getMachineVersion());
        tmpMachineDto.setMachineCode(machineDto.getMachineCode());
        tmpMachineDto.setMachineIp(machineDto.getMachineIp());
        tmpMachineDto.setMachineMac(machineDto.getMachineMac());

        long machineCnt = machineServiceDao.getMachineCount(tmpMachineDto);
        if (machineCnt > 0) {
            logger.debug("该设备已经添加无需再添加" + tmpMachineDto.toString());
            return;
        }

        machineServiceDao.saveMachine(machineDto);

        try {
            //上报云端
            uploadCloud(machineDto);
        } catch (Exception e) {
            logger.error("上报云端失败", e);
            throw new ServiceException(Result.SYS_ERROR, "上报云端失败" + e);

        }
    }

    @Override
    public List<MachineCmdDto> getMachineCmds(MachineCmdDto machineCmdDto) throws Exception {

        Assert.notNull(machineCmdDto, "设备信息不能为空");

        Assert.hasText(machineCmdDto.getMachineCode(), "设备编码不能为空");

        machineCmdDto.setPage(DEFAULT_PAGE);
        if (machineCmdDto.getPage() < 1) {
            machineCmdDto.setRow(DEFAULT_ROW);
        }
        ResultDto resultDto = machineCmdServiceImpl.getMachineCmd(machineCmdDto);

        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return null;
        }

        List<MachineCmdDto> machineCmdDtos = (List<MachineCmdDto>) resultDto.getData();

        return machineCmdDtos;
    }

    /**
     * 插入指令 给设备
     *
     * @param machineCmdDto
     * @throws Exception
     */
    @Override
    public void saveMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        machineCmdServiceImpl.saveMachineCmd(machineCmdDto);
    }

    /**
     * 插入指令 给设备
     *
     * @param machineCmdDto
     * @throws Exception
     */
    @Override
    public void insertMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        List<MachineDto> machineDtos = null;
        if (StringUtil.isEmpty(machineCmdDto.getMachineCode())) {
            //查询所有 考勤设备
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineDtos = machineServiceDao.getMachines(machineDto);
        }
        if (machineDtos == null || machineDtos.size() < 1) {
            machineDtos = new ArrayList<>();
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineDto.setMachineCode(machineCmdDto.getMachineCode());
            machineDto.setMachineId(machineCmdDto.getMachineId());
            machineDtos.add(machineDto);
        }

        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询小区信息失败");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "当前还没有设置小区，请先设置小区");
        }

        for (MachineDto machineDto : machineDtos) {
            machineCmdDto.setMachineCode(machineDto.getMachineCode());
            machineCmdDto.setMachineId(machineDto.getMachineId());
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_NO_DEAL);
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setCommunityId(communityDtos.get(0).getCommunityId());
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);
        }
    }

    /**
     * 开始考勤
     *
     * @param clockInDto
     * @return
     */
    @Override
    public ClockInResultDto clockIn(ClockInDto clockInDto) throws Exception {

        //先写考勤日志
        StaffAttendanceLogDto staffAttendanceLogDto = new StaffAttendanceLogDto();
        staffAttendanceLogDto.setClockTime(clockInDto.getClockInTime());
        staffAttendanceLogDto.setLogId(SeqUtil.getId());
        staffAttendanceLogDto.setStaffId(clockInDto.getStaffId());
        staffAttendanceLogDto.setReqParam(clockInDto.getPic());
        attendanceClassesServiceDao.saveStaffAttendanceLog(staffAttendanceLogDto);

        ClockInResultDto clockInResultDto = null;
        Calendar calendar = Calendar.getInstance();

        //根据员工查询今日未考勤任务
        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setStaffId(clockInDto.getStaffId());
        attendanceClassesTaskDto.setTaskYear(calendar.get(Calendar.YEAR) + "");
        attendanceClassesTaskDto.setTaskMonth((calendar.get(Calendar.MONTH) + 1) + "");
        attendanceClassesTaskDto.setTaskDay(calendar.get(Calendar.DATE) + "");
        attendanceClassesTaskDto.setStates(new String[]{"10000", "20000"});
        List<AttendanceClassesTaskDto> attendanceClassesTaskDtos = attendanceClassesServiceDao.getAttendanceClassesTasks(attendanceClassesTaskDto);
        if (attendanceClassesTaskDtos == null || attendanceClassesTaskDtos.size() < 1) {
            logger.debug("该员工今天没有考勤任务" + JSONObject.toJSONString(clockInDto));
            return new ClockInResultDto(ClockInResultDto.CODE_ERROR, "该员工今天没有考勤任务");
        }

        //一个员工不应该有多条考勤任务 如果有多条 我们只取一条
        AttendanceClassesTaskDto tmpAttendanceClassesTaskDto = attendanceClassesTaskDtos.get(0);
        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setClassesId(tmpAttendanceClassesTaskDto.getClassId());
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceClassesServiceDao.getAttendanceClassess(attendanceClassesDto);

        if (attendanceClassesDtos == null || attendanceClassesDtos.size() < 1) {
            return new ClockInResultDto(ClockInResultDto.CODE_ERROR, "班次异常，未找到班次数据");
        }

        attendanceClassesDto = attendanceClassesDtos.get(0);
        Assert.isInteger(attendanceClassesDto.getTimeOffset(), "不是有效的整数");
        int timeOffset = Integer.parseInt(attendanceClassesDto.getTimeOffset());
        AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
        attendanceClassesTaskDetailDto.setTaskId(tmpAttendanceClassesTaskDto.getTaskId());
        attendanceClassesTaskDetailDto.setState("10000");
        List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetailDtos =
                attendanceClassesServiceDao.getAttendanceClassesTaskDetails(attendanceClassesTaskDetailDto);

        Date clockInTime = DateUtil.getDateFromString(clockInDto.getClockInTime(), DateUtil.DATE_FORMATE_STRING_A);

        Map<String, AttendanceClassesTaskDetailDto> mulTimeMap = new HashMap<>();
        //1.0 判断 在哪个考勤范围内 属于正常考勤
        for (AttendanceClassesTaskDetailDto tmpAttendanceClassesTaskDetailDto : attendanceClassesTaskDetailDtos) {
            if (!CLOCK_TIME_MORNING_WORK.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                    && !CLOCK_TIME_AFTERNOON_OFF_DUTY.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                    && !CLOCK_TIME_NOON_OFF_DUTY.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                    && !CLOCK_TIME_NOON_WORK.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                    && !CLOCK_TIME_NIGHT_WORK.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                    && !CLOCK_TIME_NIGHT_OFF_DUTY.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())) {
                continue;
            }

            try {
                String timeStr = tmpAttendanceClassesTaskDetailDto.getValue() + ":00";
                String dateStr = DateUtil.getFormatTimeString(new Date(), DateUtil.DATE_FORMATE_STRING_B);

                Date hopeTime = DateUtil.getDateFromString(dateStr + " " + timeStr, DateUtil.DATE_FORMATE_STRING_A);
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(hopeTime);
                startCal.add(Calendar.MINUTE, (timeOffset * -1));

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(hopeTime);
                endCal.add(Calendar.MINUTE, timeOffset);

                boolean timeIn = belongCalendar(clockInTime, startCal.getTime(), endCal.getTime());
                //如果在时间段内
                if (timeIn) { //正常考勤范围内
                    doClockIn(tmpAttendanceClassesTaskDetailDto, clockInDto);
                    return new ClockInResultDto(ClockInResultDto.CODE_SUCCESS, "正常考勤");
                }

                //计算时间差
                long mulTime = clockInTime.getTime() - hopeTime.getTime();
                mulTimeMap.put(mulTime + "", tmpAttendanceClassesTaskDetailDto);
            } catch (Exception e) {
                logger.error("班次时间设置有误", e);
                return new ClockInResultDto(ClockInResultDto.CODE_ERROR, "班次时间设置有误");
            }
        }


        //2.0 判断距离哪个考勤时间最近
        Map returnMap = getMinMulTime(mulTimeMap);
        AttendanceClassesTaskDetailDto tmpAttendanceClassesTaskDetailDto =
                (AttendanceClassesTaskDetailDto) returnMap.get("curAttendanceClassesTaskDetailDto");
        String mulTime = returnMap.get("curTime").toString();


        if (CLOCK_TIME_MORNING_WORK.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                || CLOCK_TIME_NOON_WORK.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())
                || CLOCK_TIME_NIGHT_WORK.equals(tmpAttendanceClassesTaskDetailDto.getSpecCd())) { //距离上班时间最近

            if (mulTime.startsWith("-")) { //还没有到打卡时间，不能打卡 //2.1 如果早于上班时间最近，不允许打卡
                return new ClockInResultDto(ClockInResultDto.CODE_ERROR, "还没有到考勤时间，请勿考勤");
            } else { //迟到  //2.2 如果迟于上班时间最近 ，迟到
                doClockIn(tmpAttendanceClassesTaskDetailDto, clockInDto, "40000");
                clockInResultDto = new ClockInResultDto(ClockInResultDto.CODE_SUCCESS, "考勤成功，考勤状态为迟到");
            }
        } else { //2.3 如果早于下班时间最近 早退
            if (mulTime.startsWith("-")) { //2.3 如果早于下班时间最近 早退
                doClockIn(tmpAttendanceClassesTaskDetailDto, clockInDto, "50000");
                clockInResultDto = new ClockInResultDto(ClockInResultDto.CODE_SUCCESS, "考勤成功，考勤状态为早退");
            } else { //2.4 如果迟于下班时间最近 正常考勤
                doClockIn(tmpAttendanceClassesTaskDetailDto, clockInDto, "30000");
                clockInResultDto = new ClockInResultDto(ClockInResultDto.CODE_SUCCESS, "考勤成功，请注意休息，早点下班");
            }
        }

        return clockInResultDto;
    }

    private Map getMinMulTime(Map<String, AttendanceClassesTaskDetailDto> mulMap) {
        long hisTime = 0;
        Map<String, Object> tmpMap = new HashMap<>();
        for (String key : mulMap.keySet()) {
            long curTime = Math.abs(Long.parseLong(key));
            if (hisTime == 0) {
                hisTime = curTime;
                tmpMap.put("curAttendanceClassesTaskDetailDto", mulMap.get(key));
                tmpMap.put("curTime", key);
                continue;
            }

            if (curTime < hisTime) {
                hisTime = curTime;
                tmpMap.put("curAttendanceClassesTaskDetailDto", mulMap.get(key));
                tmpMap.put("curTime", key);
            }
        }

        return tmpMap;
    }

    private void doClockIn(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto, ClockInDto clockInDto) {
        doClockIn(attendanceClassesTaskDetailDto, clockInDto, "30000");
    }

    private void doClockIn(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto, ClockInDto clockInDto, String state) {


        String facePath = "/" + clockInDto.getStaffId() + FACE_RESULT + "/" + attendanceClassesTaskDetailDto.getDetailId() + ".jpg";
        ImageFactory.GenerateImage(clockInDto.getPic(), facePath);

        AttendanceClassesTaskDetailDto tmpAttendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
        tmpAttendanceClassesTaskDetailDto.setTaskId(attendanceClassesTaskDetailDto.getTaskId());
        tmpAttendanceClassesTaskDetailDto.setDetailId(attendanceClassesTaskDetailDto.getDetailId());
        tmpAttendanceClassesTaskDetailDto.setCheckTime(clockInDto.getClockInTime());
        tmpAttendanceClassesTaskDetailDto.setState(state);
        tmpAttendanceClassesTaskDetailDto.setFacePath(facePath);
        tmpAttendanceClassesTaskDetailDto.setStatusCd("0");
        long clockFlag = attendanceClassesServiceDao.updateAttendanceClassesTaskDetailDto(tmpAttendanceClassesTaskDetailDto);

        if (clockFlag < 1) {
            return;
        }

        //判断是否所有 打卡完成，如果完成将task 表刷成完成
        tmpAttendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
        tmpAttendanceClassesTaskDetailDto.setTaskId(attendanceClassesTaskDetailDto.getTaskId());
        List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetailDtos =
                attendanceClassesServiceDao.getAttendanceClassesTaskDetails(tmpAttendanceClassesTaskDetailDto);
        boolean finishAllTaskDetail = true;
        for (AttendanceClassesTaskDetailDto tmpForAttendanceClassesTaskDetailDto : attendanceClassesTaskDetailDtos) {
            if ("10000".equals(tmpForAttendanceClassesTaskDetailDto.getState())) {
                finishAllTaskDetail = false;
                break;
            }
        }

        //还有未完成的任务
        if (!finishAllTaskDetail) {
            return;
        }
        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setTaskId(attendanceClassesTaskDetailDto.getTaskId());
        attendanceClassesTaskDto.setState("30000");
        attendanceClassesTaskDto.setStatusCd("0");
        attendanceClassesServiceDao.updateAttendanceClassesTaskDto(attendanceClassesTaskDto);

    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else if (nowTime.compareTo(beginTime) == 0 || nowTime.compareTo(endTime) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 上报云端
     *
     * @param machineDto
     */
    private void uploadCloud(MachineDto machineDto) throws Exception {
        //查询 小区信息
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询小区信息失败");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "当前还没有设置小区，请先设置小区");
        }
        String url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machine.listMachines?page=1&row=1&communityId="
                + communityDtos.get(0).getCommunityId() + "&machineCode=" + machineDto.getMachineCode();
        //查询云端是否存在该设备
        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, "", HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, responseEntity.getBody());
        }
        JSONObject result = JSONObject.parseObject(responseEntity.getBody());

        int total = result.getInteger("total");
        //云端已经存在
        if (total > 0) {
            return;
        }
        url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machine.saveMachine";
        JSONObject paramIn = new JSONObject();
        paramIn.put("machineCode", machineDto.getMachineCode());
        paramIn.put("machineVersion", machineDto.getMachineVersion());
        paramIn.put("machineName", machineDto.getMachineName());
        paramIn.put("machineIp", machineDto.getMachineIp());
        paramIn.put("machineMac", machineDto.getMachineMac());
        paramIn.put("machineTypeCd", machineDto.getMachineTypeCd());
        paramIn.put("direction", "3306");//这里默认写成进
        paramIn.put("authCode", "123");
        paramIn.put("locationTypeCd", "4000");
        paramIn.put("locationObjId", "-1");
        paramIn.put("communityId", communityDtos.get(0).getCommunityId());
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), HttpMethod.POST);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("上报云端门禁失败" + tmpResponseEntity.getBody());
        }


    }
}
