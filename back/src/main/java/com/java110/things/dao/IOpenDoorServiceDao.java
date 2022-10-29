package com.java110.things.dao;

import com.java110.things.entity.openDoor.OpenDoorDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IOpenDoorServiceDao
 * @Description TODO 开门记录dao
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IOpenDoorServiceDao {

    /**
     * 保存设备人脸信息
     *
     * @param openDoorDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveOpenDoor(OpenDoorDto openDoorDto);

    /**
     * 查询设备人脸信息
     *
     * @param openDoorDto 设备人脸信息
     * @return
     */
    List<OpenDoorDto> getOpenDoors(OpenDoorDto openDoorDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param openDoorDto 设备人脸信息
     * @return
     */
    long getOpenDoorCount(OpenDoorDto openDoorDto);


}
