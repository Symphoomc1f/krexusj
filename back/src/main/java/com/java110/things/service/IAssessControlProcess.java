package com.java110.things.service;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;

import java.util.List;

/**
 * 门禁处理接口类，各大门禁厂商 需要实现这个类，实现相应的方法
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface IAssessControlProcess {

    /**
     * 初始化方法
     */
    void initAssessControlProcess();

    /**
     * 查询设备中 人脸数量
     *
     * @param machineDto 设备信息，其中包括设备编码，如 mac 设备信号，或者设备IP
     * @return 返回人脸数
     */
    int getFaceNum(MachineDto machineDto);

    /**
     * 根据设备编码和 faceId 查询是否有人脸
     * @param machineDto 设备信息
     * @param userFaceDto 用户信息
     * @return 如果有人脸 返回 faceId,没有则返回 null
     */
    String getFace(MachineDto machineDto, UserFaceDto userFaceDto);


    /**
     * 添加人脸
     * @param machineDto 硬件信息
     * @param userFaceDto 用户人脸信息
     */
    void addFace(MachineDto machineDto,UserFaceDto userFaceDto);


    /**
     * 更新人脸
     * @param machineDto 硬件信息
     * @param userFaceDto 用户人脸信息
     */
    void updateFace(MachineDto machineDto,UserFaceDto userFaceDto);


    /**
     * 删除人脸
     * @param machineDto 硬件信息
     * @param heartbeatTaskDto 任务ID
     */
    void deleteFace(MachineDto machineDto,HeartbeatTaskDto heartbeatTaskDto);


    /**
     * 清空人脸
     * @param machineDto 硬件信息
     */
    void clearFace(MachineDto machineDto);


    /**
     * 扫描门禁信息
     * @return
     */
    List<MachineDto> scanMachine();



}
