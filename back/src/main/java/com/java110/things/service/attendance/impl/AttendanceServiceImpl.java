package com.java110.things.service.attendance.impl;

import com.alibaba.fastjson.JSONArray;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AttendanceProcessFactory;
import com.java110.things.factory.CallAttendanceFactory;
import com.java110.things.service.attendance.IAttendanceProcess;
import com.java110.things.service.attendance.IAttendanceService;
import com.java110.things.service.attendance.ICallAttendanceService;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

            //查询待执行的指令
            List<MachineCmdDto> machineCmdDtos = getMachineCmd(machineDto);

            if (machineCmdDtos != null && machineCmdDtos.size() > 0) {
                //解析指令
                result = parseCmd(machineCmdDtos);
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

    /**
     * 解析指令
     *
     * @param machineCmdDtos 指令集
     * @return
     */
    private String parseCmd(List<MachineCmdDto> machineCmdDtos) {


        return "resultQunyingDto";
    }


    @Override
    public ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto) {
        try {
            return getAttendanceProcess().attendanceUploadData(attendanceUploadDto);
        } catch (Exception e) {
            logger.error("获取设备洗衣");
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, "读取默认协议失败");

    }

    /**
     * 查询设备指令
     *
     * @param machineDto 设备信息
     * @return
     */
    private List<MachineCmdDto> getMachineCmd(MachineDto machineDto) throws Exception {
        ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
        List<MachineCmdDto> machineCmdDtos = callAttendanceService.getMachineCmds(machineDto);
        return machineCmdDtos;
    }


}
