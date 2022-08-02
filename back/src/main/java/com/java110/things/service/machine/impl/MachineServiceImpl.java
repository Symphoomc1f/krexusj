package com.java110.things.service.machine.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
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
     * 查询设备信息
     *
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    @Override
    public List<MachineDto> queryMachines(MachineDto machineDto) throws Exception {
        int page = machineDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            machineDto.setPage((page - 1) * machineDto.getRow());
        }
        List<MachineDto> machineDtoList = null;
        machineDtoList = machineServiceDao.getMachines(machineDto);
        return machineDtoList;
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
    public ResultDto saveMachine(MachineDto machineDto) throws Exception {
        //初始化设备信息
        AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).initAssessControlProcess(machineDto);
        int count = machineServiceDao.saveMachine(machineDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(machineDto.getTaskId())) {
            data.put("taskId", machineDto.getTaskId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    /**
     * 修改设备信息
     *
     * @param machineDto 设备对象
     * @return
     */
    @Override
    public ResultDto updateMachine(MachineDto machineDto) throws Exception {
        //重新初始化设备信息
        String reInitSwitch = MappingCacheFactory.getValue("ACCESS_CONTROL_REINIT_SWITCH");
        if ("ON".equals(reInitSwitch)) {
            AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).initAssessControlProcess(machineDto);
        }

        int count = machineServiceDao.updateMachine(machineDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(machineDto.getTaskId())) {
            data.put("taskId", machineDto.getTaskId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) throws Exception {
        machineDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = machineServiceDao.updateMachine(machineDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(machineDto.getTaskId())) {
            data.put("taskId", machineDto.getTaskId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto restartMachine(MachineDto machineDto) throws Exception {
        List<MachineDto> machineDtoList = machineServiceDao.getMachines(machineDto);
        Assert.listOnlyOne(machineDtoList, "设备不存在");
        machineDto = machineDtoList.get(0);
        if (MachineConstant.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).restartMachine(machineDto);
        } else if (MachineConstant.MACHINE_TYPE_ATTENDANCE.equals(machineDto.getMachineTypeCd())) {
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_NO_DEAL);
            machineCmdDto.setMachineCode(machineDto.getMachineCode());
            machineCmdDto.setMachineId(machineDto.getMachineId());
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setCommunityId(machineDto.getCommunityId());
            machineCmdDto.setCmdCode(MachineConstant.CMD_RESTART);
            machineCmdDto.setCmdName("重启设备");
            machineCmdDto.setObjType(MachineConstant.MACHINE_CMD_OBJ_TYPE_SYSTEM);
            machineCmdDto.setObjTypeValue("-1");
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);
        }
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(machineDto.getTaskId())) {
            data.put("taskId", machineDto.getTaskId());
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
    }

    @Override
    public ResultDto openDoor(MachineDto machineDto) throws Exception {
        List<MachineDto> machineDtoList = machineServiceDao.getMachines(machineDto);
        Assert.listOnlyOne(machineDtoList, "设备不存在");
        machineDto = machineDtoList.get(0);
        AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).openDoor(machineDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(machineDto.getTaskId())) {
            data.put("taskId", machineDto.getTaskId());
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
    }


}
