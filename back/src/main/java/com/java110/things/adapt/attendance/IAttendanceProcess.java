package com.java110.things.adapt.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.accessControl.SyncGetTaskResultDto;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IDealQunyingGetService
 * @Description TODO 考勤机心跳接口类
 * @Author wuxw
 * @Date 2020/5/26 17:38
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
public interface IAttendanceProcess {


    ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto);

    /**
     * 查询设备是否存在
     *
     * @param machineDto 设备信息
     * @return
     */
    void initMachine(MachineDto machineDto);


    /**
     * 重启设备
     *
     * @param machineCmdDto 设备信息
     */
    void restartAttendanceMachine(MachineCmdDto machineCmdDto, JSONObject paramOut);


    /**
     * 添加人脸
     *
     * @param machineCmdDto 云端获取任务结果
     * @param paramOut      返回结果
     */
    void addFace(MachineCmdDto machineCmdDto, JSONObject paramOut) throws Exception;

    /**
     * 添加人脸
     *
     * @param syncGetTaskResultDto 云端获取任务结果
     * @param paramOut             返回结果
     */
    void addFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut);


    /**
     * 更新人脸
     *
     * @param syncGetTaskResultDto 硬件信息
     * @param paramOut             返回结果
     */
    void updateFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut);


    /**
     * 删除人脸
     *
     * @param machineCmdDto 硬件信息
     * @param paramOut             返回结果
     */
    void deleteFace(MachineCmdDto machineCmdDto, JSONObject paramOut) throws Exception;


    /**
     * 清空人脸
     *
     * @param syncGetTaskResultDto 硬件信息
     * @param paramOut             返回结果
     */
    void clearFace(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut);

    /**
     * 返回默认结果值，在没有指令的情况下返回设备的 结果值
     *
     * @return
     */
    String getDefaultResult();
}
