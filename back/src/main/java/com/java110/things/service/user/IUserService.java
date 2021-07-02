package com.java110.things.service.user;

import com.java110.things.entity.user.UserDto;
import org.springframework.http.ResponseEntity;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IUserService {

    public ResponseEntity<String> login(UserDto userDto);
}
