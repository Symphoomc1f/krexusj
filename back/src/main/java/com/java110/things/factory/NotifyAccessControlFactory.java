package com.java110.things.factory;

import com.java110.things.constant.SystemConstant;
import com.java110.things.service.INotifyAccessControlService;
import com.java110.things.util.DateUtil;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

/**
 * @ClassName NotifyAccessControlFactory
 * @Description TODO 获取 notifyAccessControlServcieImpl 对象工厂类
 * @Author wuxw
 * @Date 2020/5/16 10:06
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
public class NotifyAccessControlFactory {

    public static INotifyAccessControlService getINotifyAccessControlService() {
        INotifyAccessControlService notifyAccessControlService = ApplicationContextFactory.getBean("notifyAccessControlServcieImpl", INotifyAccessControlService.class);
        return notifyAccessControlService;
    }
}
