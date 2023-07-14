package com.java110.things.service.machine;

import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineAttrDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;

import java.util.List;


/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IMachineService {

    /**
     * 保存设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveMachine(MachineDto machineDto) throws Exception;
    /**
     * 保存设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto updateMachine(MachineDto machineDto) throws Exception;

    /**
     * 获取设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto getMachine(MachineDto machineDto) ;

    /**
     * 获取设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    List<MachineDto> queryMachines(MachineDto machineDto) ;

    /**
     * 删除设备
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto deleteMachine(MachineDto machineDto) throws Exception;

    /**
     * 重启设备
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto restartMachine(MachineDto machineDto) throws Exception;


    /**
     * 开门
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto openDoor(MachineDto machineDto, ParkingAreaTextDto parkingAreaTextDto) throws Exception;

    /**
     * 保存设备属性信息
     * @param machineAttrDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveMachineAttr(MachineAttrDto machineAttrDto) throws Exception;
    /**
     * 保存设备属性信息
     * @param machineAttrDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto updateMachineAttr(MachineAttrDto machineAttrDto) throws Exception;


    /**
     * 获取设备属性信息
     * @param machineAttrDto 设备信息
     * @return
     * @throws Exception
     */
    List<MachineAttrDto> queryMachineAttrs(MachineAttrDto machineAttrDto) throws Exception;

    /**
     * 获取二维码
     * @param userFaceDto
     * @return
     */
    ResultDto getQRcode(UserFaceDto userFaceDto) throws Exception;
}
