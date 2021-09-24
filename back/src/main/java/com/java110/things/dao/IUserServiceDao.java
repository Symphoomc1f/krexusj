package com.java110.things.dao;

import com.java110.things.entity.user.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @ClassName IUserServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 20:40
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
@Mapper
public interface IUserServiceDao {

    Map get(String userId);

    UserDto getUser(UserDto userDto);
    /**
     * 根据用户名、用户id 更新用户密码
     * @param userDto
     * @return
     */
    long updateUserPassword(UserDto userDto);
}
