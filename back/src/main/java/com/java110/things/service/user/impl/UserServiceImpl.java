package com.java110.things.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IUserServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.service.manufacturer.impl.ManufacturerServiceImpl;
import com.java110.things.service.user.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Logger logger = LoggerFactory.getLogger(ManufacturerServiceImpl.class);

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
        userMap.put(SystemConstant.LOGIN_USER_NAME, tmpUserDto.getUsername());
        String token = AuthenticationFactory.createAndSaveToken(userMap);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, jsonObject);

        return resultDto;
    }

    /**
     * 查询用户信息
     * @param userDto 用户信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getUser(UserDto userDto) throws Exception {

        UserDto tmpUserDto = userServiceDao.getUser(userDto);
        tmpUserDto.setPassword("");
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, tmpUserDto.toString());
        return resultDto;
    }

    /**
     * 退出登录
     * @param token token信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto loginOut(String token) throws Exception{
        AuthenticationFactory.deleteToken(token);
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        return resultDto;
    }

    /**
     * 修改密码
     * @param uid 用户id
     * @param oldpwd 旧密码
     * @param newpwd 新密码
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto changePassword(String uid,String oldpwd,String newpwd) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(uid);
        UserDto tmpUserDto = userServiceDao.getUser(userDto);
        if (newpwd.length() < 6) {
            logger.error("密码修改失败，密码不能小于6位");
            throw new ServiceException(Result.SYS_ERROR, "密码不能小于6位");
        }
        if (!tmpUserDto.getPassword().equals(AuthenticationFactory.md5UserPassword(oldpwd))) {
            logger.error("密码修改失败，旧密码输入错误");
            throw new ServiceException(Result.SYS_ERROR, "旧密码输入错误");
        }
        userDto = new UserDto();
        userDto.setUserId(uid);
        userDto.setPassword(AuthenticationFactory.md5UserPassword(newpwd));
        long cnt = userServiceDao.updateUserPassword(userDto);
        if (cnt < 1) {
            logger.error("密码修改失败" + JSONObject.toJSONString(userDto));
            throw new ServiceException(Result.SYS_ERROR, "密码修改失败");
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
    }

    /**
     * 查询用户信息
     *
     * @param userDto 用户信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getUserList(UserDto userDto) throws Exception {
        int page = userDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            userDto.setPage((page - 1) * userDto.getRow());
        }
        long count = userServiceDao.getUserCount(userDto);
        int totalPage = (int) Math.ceil((double) count / (double) userDto.getRow());
        List<UserDto> userList = null;
        if (count > 0) {
            userList = userServiceDao.getUserList(userDto);
        } else {
            userList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, userList);
        return resultDto;
    }

    /**
     * 添加用户信息
     *
     * @param userDto 添加用户
     * @return
     */
    @Override
    public ResultDto insertUser(UserDto userDto) throws Exception {
        userDto.setPassword(AuthenticationFactory.md5UserPassword("hc123456"));
        userDto.setLevelCd("02");
        userDto.setStatusCd("0");
        int count = userServiceDao.insertUser(userDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 更新用户信息
     * @param userDto 用户信息
     * @return
     */
    @Override
    public ResultDto updateUser(UserDto userDto) throws Exception {
        int count = userServiceDao.updateUser(userDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 删除用户
     * @param userDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto deleteUser(UserDto userDto)  throws Exception {
        int count = userServiceDao.delete(userDto.getUserId());
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }
}
