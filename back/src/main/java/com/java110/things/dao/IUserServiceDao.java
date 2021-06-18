package com.java110.things.dao;

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
}
