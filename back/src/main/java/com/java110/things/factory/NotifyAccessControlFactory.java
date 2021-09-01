package com.java110.things.factory;

import com.java110.things.service.accessControl.ICallAccessControlService;

/**
 * @ClassName NotifyAccessControlFactory
 * @Description TODO 获取 notifyAccessControlServcieImpl 对象工厂类
 * @Author wuxw
 * @Date 2020/5/16 10:06
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
public class NotifyAccessControlFactory {

    public static ICallAccessControlService getCallAccessControlService() {
        ICallAccessControlService callAccessControlService = ApplicationContextFactory.getBean("callAccessControlServiceImpl", ICallAccessControlService.class);
        return callAccessControlService;
    }
}
