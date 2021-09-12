package com.java110.things.api.attendance;

import com.java110.things.Controller.BaseController;
import com.java110.things.factory.AttendanceProcessFactory;
import com.java110.things.service.attendance.IAttendanceProcess;

/**
 * @ClassName BaseAttendanceController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/27 15:18
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class BaseAttendanceController extends BaseController {


    /**
     * 获取 考勤机处理类
     *
     * @return
     */
    protected IAttendanceProcess getAttendanceProcess() throws Exception {
        return AttendanceProcessFactory.getAttendanceProcessImpl();
    }
}
