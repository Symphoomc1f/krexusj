package com.java110.things.service.staff.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IStaffServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.service.staff.IStaffService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName StaffServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("staffServiceImpl")
public class StaffServiceImpl implements IStaffService {

    @Autowired
    private IStaffServiceDao staffServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMachineService machineService;

    /**
     * 添加小区信息
     *
     * @param staffDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveStaff(StaffDto staffDto) throws Exception {
        ResultDto resultDto = null;

        int count = staffServiceDao.saveStaff(staffDto);

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }



    /**
     * 查询小区信息
     *
     * @param staffDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getStaff(StaffDto staffDto) throws Exception {

        if (staffDto.getPage() != PageDto.DEFAULT_PAGE) {
            staffDto.setPage((staffDto.getPage() - 1) * staffDto.getRow());
        }
        long count = staffServiceDao.getStaffCount(staffDto);
        int totalPage = (int) Math.ceil((double) count / (double) staffDto.getRow());
        List<StaffDto> staffDtoList = null;
        if (count > 0) {
            staffDtoList = staffServiceDao.getStaffs(staffDto);
            //刷新人脸地
        } else {
            staffDtoList = new ArrayList<>();
        }

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, staffDtoList);
        return resultDto;
    }

    @Override
    public List<StaffDto> queryStaffs(StaffDto staffDto) throws Exception {
        if (staffDto.getPage() != PageDto.DEFAULT_PAGE) {
            staffDto.setPage((staffDto.getPage() - 1) * staffDto.getRow());
        }
        List<StaffDto> staffDtoList = null;

        staffDtoList = staffServiceDao.getStaffs(staffDto);
        //刷新人脸地
        return staffDtoList;

    }


    @Override
    public ResultDto updateStaff(StaffDto staffDto) throws Exception {
        //修改传送第三方平台
        ResultDto resultDto = null;

        int count = staffServiceDao.updateStaff(staffDto);

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }



    @Override
    public ResultDto deleteStaff(StaffDto staffDto) throws Exception {
        ResultDto resultDto = null;

        int count = staffServiceDao.updateStaff(staffDto);
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


}
