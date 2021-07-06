package com.java110.things.dao;

import com.java110.things.entity.machine.MachineDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName IMachineServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IMachineServiceDao {

    /**
     * 保存设备信息
     * @param machineDto 设备信息
     * @return 返回影响记录数
     */
    int saveMachine(MachineDto machineDto);
}
