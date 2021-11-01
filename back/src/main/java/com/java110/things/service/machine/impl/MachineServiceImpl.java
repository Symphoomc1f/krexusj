package com.java110.things.service.machine.impl;

import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MachineServiceImpl
 * @Description TODO 设备管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("machineServiceImpl")
public class MachineServiceImpl implements IMachineService {

    @Autowired
    private IMachineServiceDao machineServiceDao;

    @Autowired
    private IMachineCmdService machineCmdServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    /**
     * 添加设备信息
     *
     * @param machineDto 设备对象
     * @return
     */
    @Override
    public ResultDto saveMachine(MachineDto machineDto) throws Exception {
        int count = machineServiceDao.saveMachine(machineDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 查询设备信息
     *
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getMachine(MachineDto machineDto) throws Exception {
        int page = machineDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            machineDto.setPage((page - 1) * machineDto.getRow());
        }
        long count = machineServiceDao.getMachineCount(machineDto);
        int totalPage = (int) Math.ceil((double) count / (double) machineDto.getRow());
        List<MachineDto> machineDtoList = null;
        if (count > 0) {
            machineDtoList = machineServiceDao.getMachines(machineDto);
        } else {
            machineDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, machineDtoList);
        return resultDto;
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) throws Exception {
        machineDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = machineServiceDao.updateMachine(machineDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto restartMachine(MachineDto machineDto) throws Exception {

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

        if (MachineConstant.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            AccessControlProcessFactory.getAssessControlProcessImpl().restartMachine(machineDto);
        } else if (MachineConstant.MACHINE_TYPE_ATTENDANCE.equals(machineDto.getMachineTypeCd())) {
            //AttendanceProcessFactory.getAttendanceProcessImpl().restartAttendanceMachine(machineDto);
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_NO_DEAL);
            machineCmdDto.setMachineCode(machineDto.getMachineCode());
            machineCmdDto.setMachineId(machineDto.getMachineId());
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setCommunityId(communityDtos.get(0).getCommunityId());
            machineCmdDto.setCmdCode(MachineConstant.CMD_RESTART);
            machineCmdDto.setCmdName("重启设备");
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
    }

    @Override
    public ResultDto openDoor(MachineDto machineDto) throws Exception {
        AccessControlProcessFactory.getAssessControlProcessImpl().openDoor(machineDto);
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
    }


}
