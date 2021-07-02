package com.java110.things.service.user.impl;

import com.java110.things.entity.user.UserDto;
import com.java110.things.service.user.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    /**
     * 登录功能
     *
     * @param userDto 用户对象
     * @return
     */
    @Override
    public ResponseEntity<String> login(UserDto userDto) {

        return null;
    }
}
