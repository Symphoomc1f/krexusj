package com.java110.things.service.machine.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IMachineCmdServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.machine.IMachineCmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MachineCmdServiceImpl
 * @Description TODO 设备指令管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("machineCmdServiceImpl")
public class MachineCmdServiceImpl implements IMachineCmdService {

    @Autowired
    private IMachineCmdServiceDao machineCmdServiceDao;

    /**
     * 添加设备指令信息
     *
     * @param machineCmdDto 设备指令对象
     * @return
     */
    @Override
    public ResultDto saveMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        int count = machineCmdServiceDao.saveMachineCmd(machineCmdDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 查询设备指令信息
     *
     * @param machineCmdDto 设备指令信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        int page = machineCmdDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            machineCmdDto.setPage((page - 1) * machineCmdDto.getRow());
        }
        long count = machineCmdServiceDao.getMachineCmdCount(machineCmdDto);
        int totalPage = (int) Math.ceil((double) count / (double) machineCmdDto.getRow());
        List<MachineCmdDto> machineCmdDtoList = null;
        if (count > 0) {
            machineCmdDtoList = machineCmdServiceDao.getMachineCmds(machineCmdDto);
        } else {
            machineCmdDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, machineCmdDtoList);
        return resultDto;
    }

    @Override
    public ResultDto updateMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        int count = machineCmdServiceDao.updateMachineCmd(machineCmdDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 删除指令
     * @param machineCmdDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto deleteMachineCmd(MachineCmdDto machineCmdDto)  throws Exception {
        int count = machineCmdServiceDao.delete(machineCmdDto.getCmdId());
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


}
