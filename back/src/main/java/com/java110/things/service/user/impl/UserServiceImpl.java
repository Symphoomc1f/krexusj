package com.java110.things.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IUserServiceDao;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("userServiceImpl")
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserServiceDao userServiceDao;

    /**
     * 登录功能
     *
     * @param userDto 用户对象
     * @return
     */
    @Override
    public ResultDto login(UserDto userDto) throws Exception {

        userDto.setPassword(AuthenticationFactory.md5UserPassword(userDto.getPassword()));
        UserDto tmpUserDto = userServiceDao.getUser(userDto);

        if (tmpUserDto == null) {
            throw new ServiceException(Result.SYS_ERROR, "用户名或密码错误");
        }

        Map userMap = new HashMap();
        userMap.put(SystemConstant.LOGIN_USER_ID, tmpUserDto.getUserId());
        userMap.put(SystemConstant.LOGIN_USER_NAME, tmpUserDto.getUserName());
        String token = AuthenticationFactory.createAndSaveToken(userMap);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, jsonObject.toJSONString());

        return resultDto;
    }
}
