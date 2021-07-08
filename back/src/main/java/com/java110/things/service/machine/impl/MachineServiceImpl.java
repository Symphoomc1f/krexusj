package com.java110.things.service.machine.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IUserServiceDao;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.service.machine.IMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MachineServiceImpl
 * @Description TODO 设备管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("machineServiceImpl")
public class MachineServiceImpl implements IMachineService {

    @Autowired
    private IUserServiceDao userServiceDao;

    /**
     * 添加设备信息
     *
     * @param machineDto 设备对象
     * @return
     */
    @Override
    public ResultDto saveMachine(MachineDto machineDto) throws Exception {


        return null;
    }
}
