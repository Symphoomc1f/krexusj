package com.java110.things.service.attendance.qunying;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IStaffServiceDao;
import com.java110.things.entity.accessControl.SyncGetTaskResultDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.attendance.ClockInDto;
import com.java110.things.entity.attendance.ClockInResultDto;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.factory.CallAttendanceFactory;
import com.java110.things.service.attendance.IAttendanceProcess;
import com.java110.things.service.attendance.ICallAttendanceService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName QunyingHeartbeatServiceImpl
 * @Description TODO 群英适配器
 * @Author wuxw
 * @Date 2020/5/26 17:50
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
@Service("qunyingAttendanceProcessAdapt")
public class QunyingAttendanceProcessAdapt implements IAttendanceProcess {


    private static Logger logger = LoggerFactory.getLogger(QunyingAttendanceProcessAdapt.class);

    @Autowired
    private IStaffServiceDao qunyingStaffServiceDao;

    private static final String MANUFACTURER = "QY";

    private static final int DEFAULT_STATUS = 1;

    private static final String INFO_OK = "ok";

    private static final String CMD_REBOOT = "reboot";

    //推送指纹
    private static final String UPLOAD_DATA_FINGERPRINT = "fingerprint";

    //推送员工
    private static final String UPLOAD_DATA_USER = "user";

    //推送人脸
    private static final String UPLOAD_DATA_FACE = "face";

    //推送头像
    private static final String UPLOAD_DATA_HEAD_PIC = "headpic";

    //推送打卡记录
    private static final String UPLOAD_DATA_HEAD_CLOCKIN = "clockin";

    //推送设备信息
    private static final String UPLOAD_DATA_HEAD_INFO = "info";

    //处理结果推送
    private static final String UPLOAD_DATA_HEAD_RETURN = "return";


    //推送解除绑定
    private static final String UPLOAD_DATA_HEAD_UNBOUND = "unbound";


    private void refreshParamOut(JSONObject paramOut) {
        if (paramOut.isEmpty()) {
            JSONArray data = new JSONArray();
            paramOut.put("status", 1);
            paramOut.put("info", "ok");
            paramOut.put("data", data);
        }
    }


    /**
     * 重启设备
     *
     * @param machineCmdDto 设备信息
     * @param paramOut
     */
    @Override
    public void restartAttendanceMachine(MachineCmdDto machineCmdDto, JSONObject paramOut) {
        JSONObject pOut = getCmdResult(machineCmdDto, CMD_REBOOT);
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        data.add(pOut);

    }

    @Override
    public void addFace(MachineCmdDto machineCmdDto, JSONObject paramOut) {
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        StaffDto staffDto = new StaffDto();
        staffDto.setStaffId(machineCmdDto.getObjTypeValue());
        List<StaffDto> staffDtos = qunyingStaffServiceDao.getStaffs(staffDto);
        if (staffDtos == null || staffDtos.size() < 1) {
            return;
        }
        staffDto = staffDtos.get(0);
        JSONArray face = new JSONArray();
        face.add(staffDto.getFace1());
        face.add(staffDto.getFace2());
        face.add(staffDto.getFace3());
        JSONObject staffFace = new JSONObject();
        staffFace.put("id", machineCmdDto.getCmdId());
        staffFace.put("do", "update");
        staffFace.put("data", "face");
        staffFace.put("ccid", staffDto.getStaffId());
        staffFace.put("face", face);
        data.add(staffFace);
    }

    @Override
    public void addFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        UserFaceDto userFaceDto = syncGetTaskResultDto.getUserFaceDto();

        //添加部门
        JSONObject addDept = new JSONObject();
        addDept.put("id", SeqUtil.getId());
        addDept.put("do", "update");
        addDept.put("data", "dept");
        JSONArray depts = new JSONArray();
        JSONObject dept = new JSONObject();
        dept.put("id", userFaceDto.getDepartmentId());
        dept.put("pid", "0");
        dept.put("name", userFaceDto.getDepartmentName());
        depts.add(dept);
        addDept.put("dept", depts);
        data.add(addDept);
        //员工信息
        JSONObject addStaff = new JSONObject();
        addStaff.put("id", SeqUtil.getId());
        addStaff.put("do", "update");
        addStaff.put("data", "user");
        addStaff.put("ccid", userFaceDto.getUserId());
        addStaff.put("name", userFaceDto.getName());
        addStaff.put("passwd", AuthenticationFactory.md5("123456").toLowerCase());
        addStaff.put("card", userFaceDto.getIdNumber());
        addStaff.put("deptid", userFaceDto.getDepartmentId());
        addStaff.put("auth", 0);
        data.add(addStaff);

        JSONObject staffHeadPic = new JSONObject();
        staffHeadPic.put("id", SeqUtil.getId());
        staffHeadPic.put("do", "update");
        staffHeadPic.put("data", "headpic");
        staffHeadPic.put("ccid", userFaceDto.getUserId());
        staffHeadPic.put("headpic", userFaceDto.getFaceBase64());
        data.add(staffHeadPic);

        StaffDto staffDto = new StaffDto();
        staffDto.setStaffId(userFaceDto.getUserId());
        List<StaffDto> staffDtos = qunyingStaffServiceDao.getStaffs(staffDto);

        if (staffDtos == null || staffDtos.size() < 1) {
            staffDto.setStaffId(userFaceDto.getUserId());
            staffDto.setStaffName(userFaceDto.getName());
            staffDto.setDepartmentId(userFaceDto.getDepartmentId());
            staffDto.setDepartmentName(userFaceDto.getDepartmentName());
            qunyingStaffServiceDao.saveStaff(staffDto);
            return;
        } else {
            staffDto.setStaffId(userFaceDto.getUserId());
            staffDto.setStaffName(userFaceDto.getName());
            staffDto.setDepartmentId(userFaceDto.getDepartmentId());
            staffDto.setDepartmentName(userFaceDto.getDepartmentName());
            qunyingStaffServiceDao.updateStaff(staffDto);
        }

        String face1 = staffDto.getFace1();
        if (StringUtil.isEmpty(face1)) {
            return;
        }

        JSONArray face = new JSONArray();
        face.add(staffDto.getFace1());
        face.add(staffDto.getFace2());
        face.add(staffDto.getFace3());
        JSONObject staffFace = new JSONObject();
        staffFace.put("id", syncGetTaskResultDto.getTaskId());
        staffFace.put("do", "update");
        staffFace.put("data", "face");
        staffFace.put("ccid", userFaceDto.getUserId());
        staffFace.put("face", face);
        data.add(staffFace);

    }

    @Override
    public void updateFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        UserFaceDto userFaceDto = syncGetTaskResultDto.getUserFaceDto();
        JSONArray dataInfo = new JSONArray();
        dataInfo.add("headpic");
        JSONArray ccidInfo = new JSONArray();
        ccidInfo.add(userFaceDto.getUserId());
        JSONObject addStaff = new JSONObject();
        addStaff.put("id", SeqUtil.getId());
        addStaff.put("do", "delete");
        addStaff.put("data", dataInfo);
        addStaff.put("ccid", ccidInfo);
        data.add(addStaff);
        JSONObject staffHeadPic = new JSONObject();
        staffHeadPic.put("id", syncGetTaskResultDto.getTaskId());
        staffHeadPic.put("do", "update");
        staffHeadPic.put("data", "headpic");
        staffHeadPic.put("ccid", userFaceDto.getUserId());
        staffHeadPic.put("headpic", userFaceDto.getFaceBase64());
        data.add(staffHeadPic);

    }

    @Override
    public void deleteFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        UserFaceDto userFaceDto = syncGetTaskResultDto.getUserFaceDto();
        JSONArray dataInfo = new JSONArray();
        dataInfo.add("user");
        dataInfo.add("face");
        dataInfo.add("headpic");
        dataInfo.add("clockin");
        dataInfo.add("pic");
        JSONArray ccidInfo = new JSONArray();
        ccidInfo.add(userFaceDto.getUserId());
        JSONObject addStaff = new JSONObject();
        addStaff.put("id", userFaceDto.getTaskId());
        addStaff.put("do", "delete");
        addStaff.put("data", dataInfo);
        addStaff.put("ccid", ccidInfo);
        data.add(addStaff);
    }

    @Override
    public void clearFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        UserFaceDto userFaceDto = syncGetTaskResultDto.getUserFaceDto();
        JSONArray dataInfo = new JSONArray();
        dataInfo.add("user");
        dataInfo.add("face");
        dataInfo.add("headpic");
        dataInfo.add("clockin");
        dataInfo.add("pic");
        JSONObject addStaff = new JSONObject();
        addStaff.put("id", userFaceDto.getTaskId());
        addStaff.put("do", "delete");
        addStaff.put("data", dataInfo);
        data.add(addStaff);
    }

    @Override
    public ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto) {
        JSONArray uploadData = JSONArray.parseArray(attendanceUploadDto.getData());
        JSONArray ids = new JSONArray();
        String id = "";

        for (int uploadIndex = 0; uploadIndex < uploadData.size(); uploadIndex++) {
            id = doAttendanceUploadData(uploadData.getJSONObject(uploadIndex));
            ids.add(id);
        }
        ResultQunyingDto resultQunyingDto = new ResultQunyingDto(DEFAULT_STATUS, INFO_OK, ids);
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, resultQunyingDto.toString());
    }

    @Override
    public String getDefaultResult() {

        ResultQunyingDto resultQunyingDto = new ResultQunyingDto(DEFAULT_STATUS, INFO_OK, "");
        return resultQunyingDto.toString();
    }

    private String doAttendanceUploadData(JSONObject dataObj) {

        String data = dataObj.getString("data");

        switch (data) {
            case UPLOAD_DATA_HEAD_INFO:
                uploadMachine(dataObj);
                break;
            case UPLOAD_DATA_FACE:
                uploadFace(dataObj);
                break;
            case UPLOAD_DATA_HEAD_CLOCKIN:
                clockIn(dataObj);
                break;
        }

        return dataObj.getString("id");

    }

    /**
     * 打卡记录
     *
     * @param dataObj
     */
    private void clockIn(JSONObject dataObj) {

        try {
            String staffId = dataObj.getString("ccid");
            ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
            ClockInDto clockInDto = new ClockInDto();
            clockInDto.setStaffId(staffId);
            clockInDto.setClockInTime(dataObj.getString("time"));
            clockInDto.setPic(dataObj.getString("pic"));
            ClockInResultDto resultDto = callAttendanceService.clockIn(clockInDto);
            logger.debug("考勤结果", JSONObject.toJSONString(resultDto));
        } catch (Exception e) {
            logger.error("考勤失败", e);
        }
    }

    private void uploadFace(JSONObject dataObj) {

        String staffId = dataObj.getString("ccid");
        JSONArray faces = dataObj.getJSONArray("face");

        StaffDto staffDto = new StaffDto();
        staffDto.setStaffId(staffId);
        List<StaffDto> staffDtos = qunyingStaffServiceDao.getStaffs(staffDto);

        if (staffDtos == null || staffDtos.size() < 1) {
            staffDto.setStaffId(staffId);
            staffDto.setStaffName("群英");
            staffDto.setDepartmentId("0");
            staffDto.setDepartmentName("群英部门");
            qunyingStaffServiceDao.saveStaff(staffDto);
        }

        staffDto.setFace1(faces.getString(0));
        staffDto.setFace2(faces.getString(1));
        staffDto.setFace3(faces.getString(2));
        qunyingStaffServiceDao.updateStaff(staffDto);

        try {
            //向指令表中写指令
            ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setCmdCode(MachineConstant.CMD_CREATE_FACE);
            machineCmdDto.setCmdName("下发人脸");
            machineCmdDto.setObjType(MachineConstant.MACHINE_CMD_OBJ_TYPE_FACE);
            machineCmdDto.setObjTypeValue(staffId);
            callAttendanceService.insertMachineCmd(machineCmdDto);
        } catch (Exception e) {
            logger.error("写入指令失败", e);
        }
    }

    /**
     * 上传设备
     *
     * @param machineInfo 设备信息
     */
    private void uploadMachine(JSONObject machineInfo) {

        logger.debug("设备上报的设备信息" + machineInfo.toJSONString());

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineInfo.getString(""));

    }


    /**
     * 查询设备是否存在
     *
     * @param machineDto 设备信息
     * @return
     */
    public void initMachine(MachineDto machineDto) {
        ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
        MachineDto tmpMachineDto = callAttendanceService.getMachine(machineDto);
        if (tmpMachineDto != null) {
            return;
        }

        String machineName = MANUFACTURER + SeqUtil.getMachineSeq();

        machineDto.setMachineId(UUID.randomUUID().toString());
        machineDto.setMachineIp("设备未上报");
        machineDto.setMachineMac(machineDto.getMachineCode());
        machineDto.setMachineName(machineName);
        machineDto.setMachineVersion("v2.1_2.0.3");
        machineDto.setOem("群英");
        callAttendanceService.uploadMachine(machineDto);
    }

    /**
     * 获取cmd 结果集
     *
     * @param machineCmdDto
     * @param cmd
     * @return
     */
    private JSONObject getCmdResult(MachineCmdDto machineCmdDto, String cmd) {
        JSONObject paramObj = new JSONObject();
        paramObj.put("id", machineCmdDto.getCmdId());
        paramObj.put("do", "cmd");
        paramObj.put("cmd", cmd);

        return paramObj;
    }

    /**
     * 获取cmd 结果集
     *
     * @param machineCmdDto
     * @param data
     * @return
     */
    private JSONObject getDataResult(MachineCmdDto machineCmdDto, Object data) {
        JSONObject paramObj = new JSONObject();
        paramObj.put("id", machineCmdDto.getCmdId());
        paramObj.put("do", "cmd");
        paramObj.put("data", data);

        return paramObj;
    }
}
