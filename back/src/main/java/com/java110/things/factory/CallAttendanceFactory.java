package com.java110.things.factory;

import com.java110.things.service.accessControl.ICallAccessControlService;
import com.java110.things.service.attendance.ICallAttendanceService;

/**
 * @ClassName NotifyAccessControlFactory
 * @Description TODO 获取 notifyAccessControlServcieImpl 对象工厂类
 * @Author wuxw
 * @Date 2020/5/16 10:06
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
public class CallAttendanceFactory {

    public static ICallAttendanceService getCallAttendanceService() {
        ICallAttendanceService callAttendanceService = ApplicationContextFactory.getBean("callAttendanceServiceImpl", ICallAttendanceService.class);
        return callAttendanceService;
    }
}
