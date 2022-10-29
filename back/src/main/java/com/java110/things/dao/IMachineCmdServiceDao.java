package com.java110.things.dao;

import com.java110.things.entity.machine.MachineCmdDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IMachineCmdServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IMachineCmdServiceDao {

    /**
     * 保存设备信息
     *
     * @param machineCmdDto 设备信息
     * @return 返回影响记录数
     */
    int saveMachineCmd(MachineCmdDto machineCmdDto);

    /**
     * 查询设备信息
     *
     * @param machineCmdDto 设备信息
     * @return
     */
    List<MachineCmdDto> getMachineCmds(MachineCmdDto machineCmdDto);

    /**
     * 查询设备总记录数
     *
     * @param machineCmdDto 设备信息
     * @return
     */
    long getMachineCmdCount(MachineCmdDto machineCmdDto);

    /**
     * 修改设备信息
     *
     * @param machineCmdDto 设备信息
     * @return 返回影响记录数
     */
    int updateMachineCmd(MachineCmdDto machineCmdDto);

    /**
     * 删除指令
     *
     * @param value 指令id
     * @return 返回影响记录数
     */
    int delete(String value);
}
