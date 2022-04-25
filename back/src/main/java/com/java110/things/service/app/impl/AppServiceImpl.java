package com.java110.things.service.app.impl;

import com.java110.things.dao.IAppServiceDao;
import com.java110.things.entity.app.AppDto;
import com.java110.things.service.app.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @ClassName AppServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("appServiceImpl")
public class AppServiceImpl implements IAppService {

    @Autowired
    private IAppServiceDao appServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param appDto 小区对象
     * @return
     */
    @Override
    public int saveApp(AppDto appDto) throws Exception {

        int count = appServiceDao.saveApp(appDto);
        return count;
    }

    /**
     * 查询小区信息
     *
     * @param appDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<AppDto> getApp(AppDto appDto) throws Exception {


        List<AppDto> appDtoList = appServiceDao.getApps(appDto);
        return appDtoList;
    }

    @Override
    public int updateApp(AppDto appDto) throws Exception {
        int count = appServiceDao.updateApp(appDto);
        return count;
    }


}
