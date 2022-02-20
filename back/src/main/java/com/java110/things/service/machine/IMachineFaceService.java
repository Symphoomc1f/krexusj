package com.java110.things.service.machine;

import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IMachineFaceService {

    /**
     * 保存设备信息
     *
     * @param machineFaceDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveMachineFace(MachineFaceDto machineFaceDto) throws Exception;

    /**
     * 修改设备人脸
     *
     * @param machineFaceDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto updateMachineFace(MachineFaceDto machineFaceDto) throws Exception;

    /**
     * 获取设备信息
     *
     * @param machineFaceDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto getMachineFace(MachineFaceDto machineFaceDto) throws Exception;

    /**
     * 删除设备
     *
     * @param machineFaceDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto deleteMachineFace(MachineFaceDto machineFaceDto) throws Exception;



}
