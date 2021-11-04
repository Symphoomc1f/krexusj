package com.java110.things.service.attendance.qunying;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.SyncGetTaskResultDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.factory.CallAttendanceFactory;
import com.java110.things.service.attendance.IAttendanceProcess;
import com.java110.things.service.attendance.ICallAttendanceService;
import com.java110.things.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public void addFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {
        refreshParamOut(paramOut);
        JSONArray data = paramOut.getJSONArray("data");
        UserFaceDto userFaceDto = syncGetTaskResultDto.getUserFaceDto();
        //员工信息
        JSONObject addStaff = new JSONObject();
        addStaff.put("id", SeqUtil.getId());
        addStaff.put("do", "update");
        addStaff.put("data", "user");
        addStaff.put("ccid", userFaceDto.getUserId());
        addStaff.put("name", userFaceDto.getName());
        addStaff.put("passwd", AuthenticationFactory.md5("123456").toLowerCase());
        addStaff.put("card", userFaceDto.getIdNumber());
        addStaff.put("deptid", "0");
        addStaff.put("auth", 0);
        data.add(addStaff);

        JSONObject staffHeadPic = new JSONObject();
        staffHeadPic.put("id", SeqUtil.getId());
        staffHeadPic.put("do", "update");
        staffHeadPic.put("data", "headpic");
        staffHeadPic.put("ccid", userFaceDto.getUserId());
        staffHeadPic.put("headpic", userFaceDto.getFaceBase64());
        data.add(staffHeadPic);

//        JSONArray face = new JSONArray();
//        face.add(userFaceDto.getFaceBase64());
//        face.add(userFaceDto.getFaceBase64());
//        face.add(userFaceDto.getFaceBase64());
//        JSONObject staffFace = new JSONObject();
//        staffFace.put("id", syncGetTaskResultDto.getTaskId());
//        staffFace.put("do", "update");
//        staffFace.put("data", "face");
//        staffFace.put("ccid", userFaceDto.getUserId());
//        staffFace.put("face", face);
//        data.add(staffFace);

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
        }

        return dataObj.getString("id");

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
