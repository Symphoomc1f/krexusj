package com.java110.things.service;

import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.entity.user.UserDto;

import java.util.List;

/**
 * @ClassName INotifyAccessControlServcie
 * @Description TODO 门禁硬件回调 接口类
 * @Author wuxw
 * @Date 2020/5/15 19:12
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
public interface ICallAccessControlService {

    /**
     * 门禁 上报,当门禁上线时 建议将门禁自动上报，系统管理页面和云端可以自动注册设备
     * @param machineDto 设备对象
     */
    void uploadMachine(MachineDto machineDto);

    /**
     * 记录操作日志
     * @param operateLogDto 日志对象，当logId 在数据库中不存在是做添加，存在时 做修改
     */
    void saveOrUpdateOperateLog(OperateLogDto operateLogDto);

    /**
     * 人脸推送接口
     * 这里只处理 抓拍图片记录，模板图片本身就存在 不做记录
     *
     * @param openDoorDto 必填信息
     */
    void saveFaceResult(OpenDoorDto openDoorDto) throws Exception;

    /**
     * 获取用户房屋信息
     * @param userFaceDto 根据用户人脸信息
     * @return 返回房屋列表
     */
    List<RoomDto> getRooms(UserFaceDto userFaceDto)  throws Exception;

    /**
     * 查询费用信息
     * @param roomDto 房屋信息
     * @return 费用列表
     */
    List<FeeDto> getFees(RoomDto roomDto)  throws Exception;


}
