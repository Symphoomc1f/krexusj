package com.java110.things.service.hc.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.dao.IAttendanceClassesServiceDao;
import com.java110.things.entity.app.AppAttrDto;
import com.java110.things.entity.app.AppDto;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDetailDto;
import com.java110.things.entity.attendance.AttendanceClassesTaskDto;
import com.java110.things.entity.attendance.StaffAttendanceLogDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.service.app.IAppService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.hc.IAttendanceCallHcService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.service.staff.IStaffService;
import com.java110.things.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用HC小区管理系统实现类
 * <p>
 * 演示地址：demo.homecommunity.cn
 * 代码：https://gitee.com/wuxw7/MicroCommunity
 *
 * @ClassName CarCallHcServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2021/1/18 20:54
 * @Version 1.0
 * add by wuxw 2021/1/18
 **/
@Service
public class AttendanceCallHcServiceImpl implements IAttendanceCallHcService {

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IAppService appServiceImpl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IAttendanceClassesServiceDao attendanceClassesServiceDao;

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private IStaffService staffServiceImpl;

    @Override
    @Async
    public void upload(String taskId) throws Exception {

        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setTaskId(taskId);
        List<AttendanceClassesTaskDto> attendanceClassesTaskDtos = attendanceClassesServiceDao.getAttendanceClassesTasks(attendanceClassesTaskDto);

        Assert.listOnlyOne(attendanceClassesTaskDtos, "未找到任务");
        attendanceClassesTaskDto = attendanceClassesTaskDtos.get(0);
        //根据设备查询小区ID
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(attendanceClassesTaskDto.getDepartmentId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_DEPARTMENT);
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("考勤对应考勤机不存在");
        }


        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDtos.get(0).getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未包含小区信息");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_ATTENDANCE_TASK);

        if (appAttrDto == null) {
            return;
        }

        String value = appAttrDto.getValue();
        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }

        //查询班组
        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setClassesId(attendanceClassesTaskDto.getClassId());
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceClassesServiceDao.getAttendanceClassess(attendanceClassesDto);

        Assert.listOnlyOne(attendanceClassesDtos, "未找到考勤班组");

        //查询员工
        StaffDto staffDto = new StaffDto();
        staffDto.setStaffId(attendanceClassesTaskDto.getStaffId());
        List<StaffDto> staffDtos = staffServiceImpl.queryStaffs(staffDto);

        Assert.listOnlyOne(staffDtos, "员工不存在");


        //查询考勤明细
        AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
        attendanceClassesTaskDetailDto.setTaskId(taskId);
        List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetailDtos
                = attendanceClassesServiceDao.getAttendanceClassesTaskDetails(attendanceClassesTaskDetailDto);
        attendanceClassesTaskDto.setAttendanceClassesTaskDetails(attendanceClassesTaskDetailDtos);
        attendanceClassesTaskDto.setClassId(attendanceClassesDtos.get(0).getExtClassesId());
        attendanceClassesTaskDto.setStaffId(staffDtos.get(0).getExtStaffId());
        String url = value;
        Map<String, String> headers = new HashMap<>();

        headers.put("communityId", communityDtos.get(0).getCommunityId());

        String data = JSONObject.toJSONString(attendanceClassesTaskDto);
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data, headers, HttpMethod.POST, securityCode);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "上传考勤任务失败" + tmpResponseEntity.getBody());
        }

    }

    @Override
    @Async
    public void checkIn(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto, boolean finishAllTaskDetail) throws Exception {
        AttendanceClassesTaskDto attendanceClassesTaskDto = new AttendanceClassesTaskDto();
        attendanceClassesTaskDto.setTaskId(attendanceClassesTaskDetailDto.getTaskId());
        List<AttendanceClassesTaskDto> attendanceClassesTaskDtos = attendanceClassesServiceDao.getAttendanceClassesTasks(attendanceClassesTaskDto);

        Assert.listOnlyOne(attendanceClassesTaskDtos, "未找到任务");
        attendanceClassesTaskDto = attendanceClassesTaskDtos.get(0);
        //根据设备查询小区ID
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(attendanceClassesTaskDto.getDepartmentId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_DEPARTMENT);
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("考勤对应考勤机不存在");
        }


        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDtos.get(0).getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未包含小区信息");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_ATTENDANCE_TASK_DETAIL);

        if (appAttrDto == null) {
            return;
        }

        String value = appAttrDto.getValue();

        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }


        //查询考勤明细
        String url = value;
        Map<String, String> headers = new HashMap<>();

        headers.put("communityId", communityDtos.get(0).getCommunityId());

        JSONObject tmpAttendanceClassesTaskDetailDto = JSONObject.parseObject(JSONObject.toJSONString(attendanceClassesTaskDetailDto));

        tmpAttendanceClassesTaskDetailDto.put("finishAllTaskDetail", finishAllTaskDetail);
        String data = JSONObject.toJSONString(tmpAttendanceClassesTaskDetailDto);
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data, headers, HttpMethod.POST, securityCode);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "打卡同步 HC失败" + tmpResponseEntity.getBody());
        }
    }

    @Override
    @Async
    public void checkInTime(StaffAttendanceLogDto staffAttendanceLogDto) throws Exception {

        StaffDto staffDto = new StaffDto();
        staffDto.setStaffId(staffAttendanceLogDto.getStaffId());
        List<StaffDto> staffDtos = staffServiceImpl.queryStaffs(staffDto);

        Assert.listOnlyOne(staffDtos, "员工不存在");

        //根据设备查询小区ID
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(staffDtos.get(0).getDepartmentId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_DEPARTMENT);
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            throw new IllegalArgumentException("考勤对应考勤机不存在");
        }


        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDtos.get(0).getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未包含小区信息");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_ATTENDANCE_LOG);

        if (appAttrDto == null) {
            return;
        }

        String value = appAttrDto.getValue();

        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }


        //查询考勤明细
        String url = value;
        Map<String, String> headers = new HashMap<>();

        headers.put("communityId", communityDtos.get(0).getCommunityId());

        JSONObject tmpAttendanceClassesTaskDetailDto = JSONObject.parseObject(JSONObject.toJSONString(staffAttendanceLogDto));
        tmpAttendanceClassesTaskDetailDto.put("departmentId", staffDtos.get(0).getDepartmentId());
        tmpAttendanceClassesTaskDetailDto.put("departmentName", staffDtos.get(0).getDepartmentId());
        tmpAttendanceClassesTaskDetailDto.put("staffName", staffDtos.get(0).getStaffName());
        tmpAttendanceClassesTaskDetailDto.put("staffId", staffDtos.get(0).getExtStaffId());

        String data = JSONObject.toJSONString(tmpAttendanceClassesTaskDetailDto);
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data, headers, HttpMethod.POST, securityCode);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "打卡同步 HC失败" + tmpResponseEntity.getBody());
        }
    }
}
