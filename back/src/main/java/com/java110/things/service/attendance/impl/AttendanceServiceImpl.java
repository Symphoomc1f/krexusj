package com.java110.things.service.attendance.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.SyncGetTaskResultDto;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.AttendanceProcessFactory;
import com.java110.things.factory.CallAttendanceFactory;
import com.java110.things.factory.GetCloudFaceFactory;
import com.java110.things.service.attendance.IAttendanceProcess;
import com.java110.things.service.attendance.IAttendanceService;
import com.java110.things.service.attendance.ICallAttendanceService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName AttendanceServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/29 18:35
 * @Version 1.0
 * add by wuxw 2020/5/29
 **/

@Service("attendanceServiceImpl")
public class AttendanceServiceImpl implements IAttendanceService {
    private static Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);


    @Autowired
    private IMachineCmdService machineCmdServiceImpl;

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    /**
     * 获取 考勤机处理类
     *
     * @return
     */
    private IAttendanceProcess getAttendanceProcess() throws Exception {
        return AttendanceProcessFactory.getAttendanceProcessImpl();
    }

    @Override
    public ResultDto heartbeat(MachineDto machineDto) {
        String result = null;
        try {
            //查询是否存在该设备
            getAttendanceProcess().initMachine(machineDto);

            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setMachineCode(machineDto.getMachineCode());
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_NO_DEAL);
            machineCmdDto.setPage(1);
            machineCmdDto.setRow(5);
            //查询待执行的指令
            List<MachineCmdDto> machineCmdDtos = getMachineCmd(machineCmdDto);

            if (machineCmdDtos != null && machineCmdDtos.size() > 0) {
                JSONObject paramOut = new JSONObject();
                for (MachineCmdDto tmpMachineCmdDto : machineCmdDtos) {
                    parseCmd(tmpMachineCmdDto, paramOut);
                }
                result = paramOut.toJSONString();
            }

            if (StringUtil.isEmpty(result)) {
                //请求云端是否有 指令
                result = getCmdFromCloud(machineDto);
            }
        } catch (Exception e) {
            logger.error("设备获取指令失败", e);
        } finally {
            if (StringUtil.isEmpty(result)) {
                try {
                    result = getAttendanceProcess().getDefaultResult();
                } catch (Exception e) {
                    logger.error("设备获取指令失败", e);
                    result = "读取默认返回结果失败";
                }
            }
        }

        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, result);
    }

    private String getCmdFromCloud(MachineDto machineDto) throws Exception {

        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);
        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询设备失败");
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();

        Assert.listOnlyOne(machineDtos, "设备未找到");
        //查询 小区信息
        CommunityDto communityDto = new CommunityDto();
        resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询小区信息失败");
        }
        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();
        Assert.listOnlyOne(communityDtos, "当前还没有设置小区，请先设置小区");

        List<SyncGetTaskResultDto> syncGetTaskResultDtos = GetCloudFaceFactory.doHeartbeatCloud(machineDtos.get(0), communityDtos.get(0));

        if (syncGetTaskResultDtos == null || syncGetTaskResultDtos.size() < 1) {
            return null;
        }
        JSONObject paramOut = new JSONObject();
        for (SyncGetTaskResultDto syncGetTaskResultDto : syncGetTaskResultDtos) {
            parseCloudCmd(syncGetTaskResultDto, paramOut);
        }
        return paramOut.toJSONString();
    }

    /**
     * 解析云端 指令
     *
     * @param syncGetTaskResultDto
     * @param paramOut
     */
    private void parseCloudCmd(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {

        JSONObject resultJson = null;
        try {
            switch (syncGetTaskResultDto.getCmd()) {
                case MachineConstant
                        .CMD_CREATE_FACE:
                    getAttendanceProcess().addFace(syncGetTaskResultDto, paramOut);
                    break;
                case MachineConstant
                        .CMD_DELETE_FACE:
                    getAttendanceProcess().deleteFace(syncGetTaskResultDto, paramOut);
                    break;
                case MachineConstant
                        .CMD_CLEAR_FACE:
                    getAttendanceProcess().clearFace(syncGetTaskResultDto, paramOut);
                    break;
            }

            //修改指令状态
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_DEALING);
            machineCmdDto.setMachineCode(syncGetTaskResultDto.getMachineDto().getMachineCode());
            machineCmdDto.setMachineId(syncGetTaskResultDto.getMachineDto().getMachineId());
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setCommunityId(syncGetTaskResultDto.getCommunityDto().getCommunityId());
            machineCmdDto.setCmdCode(syncGetTaskResultDto.getCmd());
            machineCmdDto.setCmdName("");
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);

        } catch (Exception e) {
            logger.error("解析指令处理", e);
        }
    }


    /**
     * 解析指令
     *
     * @param machineCmdDto 指令集
     * @return
     */
    private void parseCmd(MachineCmdDto machineCmdDto, JSONObject paramOut) {
        JSONObject resultJson = null;
        try {
            switch (machineCmdDto.getCmdCode()) {
                case MachineConstant
                        .CMD_RESTART:
                    getAttendanceProcess().restartAttendanceMachine(machineCmdDto, paramOut);
                    break;
                case MachineConstant.CMD_CREATE_FACE:
                    getAttendanceProcess().addFace(machineCmdDto, paramOut);
                    break;
            }

            //修改指令状态
            MachineCmdDto tmpMachineCmdDto = new MachineCmdDto();
            tmpMachineCmdDto.setCmdId(machineCmdDto.getCmdId());
            tmpMachineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_DEALING);
            machineCmdServiceImpl.updateMachineCmd(tmpMachineCmdDto);

        } catch (Exception e) {
            logger.error("解析指令处理", e);
        }
    }


    @Override
    public ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto) {
        try {
            return getAttendanceProcess().attendanceUploadData(attendanceUploadDto);
        } catch (Exception e) {
            logger.error("获取设备失败", e);
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, "读取默认协议失败");

    }

    /**
     * 查询设备指令
     *
     * @param machineCmdDto 设备信息
     * @return
     */
    private List<MachineCmdDto> getMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
        List<MachineCmdDto> machineCmdDtos = callAttendanceService.getMachineCmds(machineCmdDto);
        return machineCmdDtos;
    }


}
