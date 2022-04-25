package com.java110.things.dao;

import com.java110.things.entity.app.AppDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IAppServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IAppServiceDao {

    /**
     * 保存设备信息
     *
     * @param appDto 设备信息
     * @return 返回影响记录数
     */
    int saveApp(AppDto appDto);

    /**
     * 查询设备信息
     * @param appDto 设备信息
     * @return
     */
    List<AppDto> getApps(AppDto appDto);


    /**
     * 修改设备信息
     *
     * @param appDto 设备信息
     * @return 返回影响记录数
     */
    int updateApp(AppDto appDto);

}
