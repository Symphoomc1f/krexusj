package com.java110.things.service.attendance.qunying;

import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.service.attendance.IAttendanceProcess;
import org.springframework.stereotype.Service;

/**
 * @ClassName QunyingHeartbeatServiceImpl
 * @Description TODO 群英适配器
 * @Author wuxw
 * @Date 2020/5/26 17:50
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
@Service("qunyingAttendanceProcessAdapt")
public class QunyingAttendanceProcessAdapt implements IAttendanceProcess {
    @Override
    public ResultQunyingDto heartbeat(MachineDto machineDto) {


        return null;
    }
}
