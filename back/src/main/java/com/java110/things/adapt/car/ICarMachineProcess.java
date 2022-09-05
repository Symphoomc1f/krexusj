package com.java110.things.adapt.car;

import com.java110.things.entity.machine.MachineDto;

/**
 * 道闸对接设备 适配器类
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface ICarMachineProcess {

    /**
     * 初始化方法
     */
    void initCar();

    /**
     * 初始化设备
     */
    void initCar(MachineDto machineDto);


    /**
     * 设备同步的字节信息
     *
     * @param machineDto 设备信息
     * @param bytes      字节信息
     */
    void readByte(MachineDto machineDto, byte[] bytes);


    /**
     * 重启设备
     *
     * @param machineDto 硬件信息
     */
    void restartMachine(MachineDto machineDto);

    /**
     * 道闸开门
     *
     * @param machineDto 硬件信息
     */
    void openDoor(MachineDto machineDto);

    void sendKeepAlive(MachineDto machineDto);


}
