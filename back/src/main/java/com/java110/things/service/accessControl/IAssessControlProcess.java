package com.java110.things.service.accessControl;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
    void clearFace(MachineDto machineDto,HeartbeatTaskDto heartbeatTaskDto);


    /**
     * 扫描门禁信息
     * @return
     */
    List<MachineDto> scanMachine() throws Exception;


    /**
     * 接收订阅mqtt 消息，如果不是mqtt 协议对接硬件可以空
     * @param topic 主题
     * @param data 消息内容
     */
    void mqttMessageArrived(String topic, String data);


    /**
     * 重启设备
     * @param machineDto 硬件信息
     */
    void restartMachine(MachineDto machineDto);

    /**
     * 开门
     * @param machineDto  硬件信息
     */
    void openDoor(MachineDto machineDto);

    /**
     * 人脸推送结果
     * @param data 这个为设备人脸推送协议，请参考设备协议文档
     * @return 处理成功时返回 true 失败时返回false
     */
    boolean httpFaceResult(String data);



}
