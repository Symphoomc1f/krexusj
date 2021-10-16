package com.java110.things.dao;

import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.user.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
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

    /**
     * 查询用户列表数据
     * @param userDto
     * @return
     */
    List<UserDto> getUserList(UserDto userDto);

    /**
     * 查询用户总记录数
     *
     * @param userDto 用户信息
     * @return
     */
    long getUserCount(UserDto userDto);

    /**
     * 保存用户信息
     *
     * @param userDto 用户信息
     * @return 返回影响记录数
     */
    int insertUser(UserDto userDto);

    /**
     * 修改用户信息
     *
     * @param userDto 用户信息
     * @return 返回影响记录数
     */
    int updateUser(UserDto userDto);

    int delete(String value);
}
