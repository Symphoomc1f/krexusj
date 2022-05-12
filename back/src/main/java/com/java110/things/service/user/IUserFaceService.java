package com.java110.things.service.user;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IUserFaceService {

    /**
     * 保存用户信息
     *
     * @param userFaceDto 用户人脸信息
     * @return
     * @throws Exception
     */

    public ResultDto saveUserFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception;

    public ResultDto updateUserFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception;

    public ResultDto deleteUserFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) throws Exception;


    /**
     * 清空用户信息
     *
     * @param machineDto
     * @return
     * @throws Exception
     */
    public ResultDto clearUserFace(MachineDto machineDto) throws Exception;
}
