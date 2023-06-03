package com.java110.things.service.machine.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineAttrDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.CarMachineProcessFactory;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.DateUtil;
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
    public List<MachineDto> queryMachines(MachineDto machineDto) {
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
            refreshMachineAttr(machineDtoList);
        } else {
            machineDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, machineDtoList);
        return resultDto;
    }

    private void refreshMachineAttr(List<MachineDto> machineDtoList) {

        if (machineDtoList == null || machineDtoList.size() < 1) {
            return;
        }

        List<String> machineIds = new ArrayList<>();
        for (MachineDto machineDto : machineDtoList) {
            machineIds.add(machineDto.getMachineId());
        }

        MachineAttrDto machineAttrDto = new MachineAttrDto();
        machineAttrDto.setCommunityId(machineDtoList.get(0).getCommunityId());
        machineAttrDto.setMachineIds(machineIds.toArray(new String[machineIds.size()]));
        List<MachineAttrDto> machineAttrDtos = machineServiceDao.getMachineAttrs(machineAttrDto);

        List<MachineAttrDto> tmpMachineAttrDtos = null;
        for (MachineDto machineDto : machineDtoList) {
            tmpMachineAttrDtos = new ArrayList<>();
            for (MachineAttrDto tmpMachineAttrDto : machineAttrDtos) {
                if (machineDto.getMachineId().equals(tmpMachineAttrDto.getMachineId())) {
                    tmpMachineAttrDtos.add(tmpMachineAttrDto);
                }
            }
            machineDto.setMachineAttrDtos(tmpMachineAttrDtos);
        }
    }


    @Override
    public ResultDto saveMachine(MachineDto machineDto) throws Exception {
        //初始化设备信息
        ResultDto resultDto = null;
        if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).addMachine(machineDto);
            if (resultDto != null && resultDto.getCode() != ResultDto.SUCCESS) {
                return resultDto;
            }
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).initAssessControlProcess(machineDto);
        } else if (MachineDto.MACHINE_TYPE_CAR.equals(machineDto.getMachineTypeCd())) {
            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).initCar(machineDto);
        } else if (MachineDto.MACHINE_TYPE_OTHER_CAR.equals(machineDto.getMachineTypeCd())) {
            CarProcessFactory.getCarImpl(machineDto.getHmId()).initCar(machineDto);
        }

        if (resultDto != null && resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }
        machineDto.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));

        if(StringUtil.isEmpty(machineDto.getThirdMachineId())){
            machineDto.setThirdMachineId("-1");
        }
        int count = machineServiceDao.saveMachine(machineDto);

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
            MachineDto tmpMachineDto = new MachineDto();
            tmpMachineDto.setExtMachineId(machineDto.getExtMachineId());
            List<MachineDto> machineDtos = queryMachines(tmpMachineDto);
            Assert.listOnlyOne(machineDtos, "未找到设备信息");

            if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDtos.get(0).getMachineTypeCd())) {
                AccessControlProcessFactory.getAssessControlProcessImpl(machineDtos.get(0).getHmId()).initAssessControlProcess(machineDtos.get(0));
            } else if (MachineDto.MACHINE_TYPE_CAR.equals(machineDtos.get(0).getMachineTypeCd())) {
                CarMachineProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).initCar(machineDtos.get(0));
            } else if (MachineDto.MACHINE_TYPE_OTHER_CAR.equals(machineDtos.get(0).getMachineTypeCd())) {
                CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).initCar(machineDtos.get(0));
            }
        }

        if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).updateMachine(machineDto);
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
        if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).deleteMachine(machineDto);
        }
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
        if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).restartMachine(machineDto);
        } else if (MachineDto.MACHINE_TYPE_ATTENDANCE.equals(machineDto.getMachineTypeCd())) {
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
        } else if (MachineDto.MACHINE_TYPE_CAR.equals(machineDto.getMachineTypeCd())) {
            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).restartMachine(machineDto);
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
        if (machineDtoList == null || machineDtoList.size() < 1) {
            throw new IllegalArgumentException("设备不存在");
        }
        machineDto = machineDtoList.get(0);
        //初始化设备信息
        if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).openDoor(machineDto);
        } else if (MachineDto.MACHINE_TYPE_CAR.equals(machineDto.getMachineTypeCd())) {
            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).openDoor(machineDto);
        }
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(machineDto.getTaskId())) {
            data.put("taskId", machineDto.getTaskId());
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
    }


    /**********************以下为属性相关 **********************/
    /**
     * 查询设备信息
     *
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    @Override
    public List<MachineAttrDto> queryMachineAttrs(MachineAttrDto machineDto) throws Exception {
        int page = machineDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            machineDto.setPage((page - 1) * machineDto.getRow());
        }
        List<MachineAttrDto> machineDtoList = null;
        machineDtoList = machineServiceDao.getMachineAttrs(machineDto);
        return machineDtoList;
    }

    @Override
    public ResultDto getQRcode(UserFaceDto userFaceDto) throws Exception {
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(userFaceDto.getMachineCode());
        List<MachineDto> machineDtoList = machineServiceDao.getMachines(machineDto);
        if (machineDtoList == null || machineDtoList.size() < 1) {
            throw new IllegalArgumentException("设备不存在");
        }
        machineDto = machineDtoList.get(0);
        ResultDto resultDto = null;
        //初始化设备信息
        if (MachineDto.MACHINE_TYPE_ACCESS_CONTROL.equals(machineDto.getMachineTypeCd())) {
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).getQRcode(userFaceDto);
        } else {
            resultDto = new ResultDto(ResponseConstant.ERROR, "该设备不支持");
        }
        return resultDto;
    }


    @Override
    public ResultDto saveMachineAttr(MachineAttrDto machineAttrDto) throws Exception {
        //初始化设备信息

        int count = machineServiceDao.saveMachineAttr(machineAttrDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
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
     * @param machineAttrDto 设备对象
     * @return
     */
    @Override
    public ResultDto updateMachineAttr(MachineAttrDto machineAttrDto) throws Exception {


        int count = machineServiceDao.updateMachineAttr(machineAttrDto);
        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }


}
