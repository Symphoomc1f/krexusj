package com.java110.things.dao;

import com.java110.things.entity.user.StaffDto;

import java.util.List;

/**
 * @ClassName QunyingStaffServiceDao
 * @Description TODO 员工管理
 * @Author wuxw
 * @Date 2020/6/7 22:49
 * @Version 1.0
 * add by wuxw 2020/6/7
 **/
public interface IQunyingStaffServiceDao {

    /**
     * 保存设备人脸信息
     *
     * @param staffDto 设备人脸信息
     * @return 返回影响记录数
     */
    int saveStaff(StaffDto staffDto);

    /**
     * 查询设备人脸信息
     *
     * @param staffDto 设备人脸信息
     * @return
     */
    List<StaffDto> getStaffs(StaffDto staffDto);

    /**
     * 查询设备人脸总记录数
     *
     * @param staffDto 设备人脸信息
     * @return
     */
    long getStaffCount(StaffDto staffDto);

    /**
     * 修改设备人脸信息
     *
     * @param staffDto 设备人脸信息
     * @return 返回影响记录数
     */
    int updateStaff(StaffDto staffDto);
}
