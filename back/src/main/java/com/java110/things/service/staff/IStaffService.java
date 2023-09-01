package com.java110.things.service.staff;

import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;

import java.util.List;

/**
 * @ClassName IStaffService
 * @Description TODO 小区服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IStaffService {

    /**
     * 保存车辆信息
     *
     * @param staffDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto saveStaff(StaffDto staffDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param staffDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto getStaff(StaffDto staffDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param staffDto 车辆信息
     * @return
     * @throws Exception
     */
    List<StaffDto> queryStaffs(StaffDto staffDto) throws Exception;

    /**
     * 修改车辆信息
     *
     * @param staffDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto updateStaff(StaffDto staffDto) throws Exception;


    /**
     * 删除设备
     *
     * @param staffDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto deleteStaff(StaffDto staffDto) throws Exception;

}
