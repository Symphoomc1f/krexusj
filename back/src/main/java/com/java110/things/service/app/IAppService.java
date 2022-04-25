package com.java110.things.service.app;

import com.java110.things.entity.app.AppDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.response.ResultDto;

import java.util.List;

/**
 * 应用类
 * @ClassName IAppService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IAppService {

    /**
     * 保存 应用
     *
     * @param appDto 应用信息
     * @return
     * @throws Exception
     */
    int saveApp(AppDto appDto) throws Exception;

    /**
     * 修改 应用信息
     *
     * @param appDto 设备指令信息
     * @return
     * @throws Exception
     */
    int updateApp(AppDto appDto) throws Exception;

    /**
     * 查询应用信息
     *
     * @param appDto 应用信息
     * @return
     * @throws Exception
     */
    List<AppDto> getApp(AppDto appDto) throws Exception;


}
