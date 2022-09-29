package com.java110.things.adapt.car;

import com.java110.things.entity.accessControl.CarResultDto;
import com.java110.things.entity.car.CarBlackWhiteDto;
import com.java110.things.entity.car.CarDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.netty.Java110CarProtocol;

/**
 * 门禁处理接口类，各大门禁厂商 需要实现这个类，实现相应的方法
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface ICarProcess {

    /**
     * 初始化方法
     */
    void initCar();


    public void initCar(MachineDto machineDto);

    /**
     * 查询设备中 人脸数量
     *
     * @return 返回人脸数
     */
    int getCarNum();

    /**
     * 根据设备编码和 faceId 查询是否有人脸
     *
     * @param carResultDto 用户信息
     * @return 如果有人脸 返回 faceId,没有则返回 null
     */
    String getCar(CarResultDto carResultDto);


    /**
     * 添加修改车牌
     *
     * @param carResultDto 用户人脸信息
     */
    ResultDto addCar(MachineDto machineDto, CarDto carResultDto);


    ResultDto updateCar(MachineDto machineDto, CarDto carResultDto);


    /**
     * 删除车牌
     *
     * @param carResultDto 任务ID
     */
    ResultDto deleteCar(MachineDto machineDto, CarDto carResultDto);


    /**
     * 重启设备
     *
     * @param machineDto 硬件信息
     */
    void restartMachine(MachineDto machineDto);

    /**
     * 开门
     *
     * @param machineDto 硬件信息
     */
    void openDoor(MachineDto machineDto);

    /**
     * 人脸推送结果
     *
     * @param data 这个为设备人脸推送协议，请参考设备协议文档
     * @return 处理成功时返回 true 失败时返回false
     */
    String httpFaceResult(String data);

    /**
     * 接受消息
     *
     * @param content
     * @return
     */
    Java110CarProtocol accept(MachineDto machineDto, String content);

    /**
     * 查询需要支付的订单
     *
     * @return
     */
    String getNeedPayOrder();


    /**
     * 添加白名单
     *
     * @param tmpMachineDto
     * @param carBlackWhiteDto
     * @return
     */
    ResultDto addCarBlackWhite(MachineDto tmpMachineDto, CarBlackWhiteDto carBlackWhiteDto);

    /**
     * 删除白名单
     *
     * @param tmpMachineDto
     * @param carBlackWhiteDto
     * @return
     */
    ResultDto deleteCarBlackWhite(MachineDto tmpMachineDto, CarBlackWhiteDto carBlackWhiteDto);
}
