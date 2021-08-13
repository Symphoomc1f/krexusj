package com.java110.things.dao;

import com.java110.things.entity.machine.MachineFaceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IMachineFaceServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IMachineFaceServiceDao {

    /**
     * 保存设备人脸信息
     *
     * @param machineFaceDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveMachineFace(MachineFaceDto machineFaceDto);

    /**
     * 查询设备人脸信息
     *
     * @param machineFaceDto 设备人脸信息
     * @return
     */
    List<MachineFaceDto> getMachineFaces(MachineFaceDto machineFaceDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param machineFaceDto 设备人脸信息
     * @return
     */
    long getMachineFaceCount(MachineFaceDto machineFaceDto);

    /**
     * 修改设备人脸信息
     *
     * @param machineFaceDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateMachineFace(MachineFaceDto machineFaceDto);

}
