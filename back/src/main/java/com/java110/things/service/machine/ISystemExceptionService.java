package com.java110.things.service.machine;

import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.machine.SystemExceptionDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IOperateLogService
 * @Description TODO 硬件操作日志记录类
 * @Author wuxw
 * @Date 2020/5/22 14:50
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
public interface ISystemExceptionService {

    /**
     * 保存操作日志
     * @param systemExceptionDto 操作日志对象
     * @return
     */
    void save(SystemExceptionDto systemExceptionDto);

    /**
     * 保存操作日志
     * @return
     */
    void save(String exceptionType,String objId,String machineId,String errMsg);

    /**
     * 保存操作日志
     * @return
     */
    void save(String exceptionType,String errMsg);

}
