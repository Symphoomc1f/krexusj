package com.java110.things.service.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.service.INotifyAccessControlService;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @ClassName NotifyAccessControlServcieImpl
 * @Description TODO 门禁 反向调用接口实现类
 * @Author wuxw
 * @Date 2020/5/15 19:11
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Service("notifyAccessControlServcieImpl")
public class NotifyAccessControlServiceImpl implements INotifyAccessControlService {
    Logger logger = LoggerFactory.getLogger(NotifyAccessControlServiceImpl.class);

    @Autowired
    IMachineServiceDao machineServiceDao;

    @Override
    public void uploadMachine(MachineDto machineDto) {

        if (machineDto == null) {
            throw new ServiceException(Result.SYS_ERROR, "设备信息不能为空");
        }

        logger.debug("machineDto", machineDto.toString());
        //如果设备ID为空的情况
        if (StringUtil.isEmpty(machineDto.getMachineId())) {
            machineDto.setMachineId(UUID.randomUUID().toString());
        }
        //设备编码
        if (StringUtil.isEmpty(machineDto.getMachineCode())) {
            throw new ServiceException(Result.SYS_ERROR, "未包含设备编码，如果设备没有编码可以写mac或者ip,只要标识唯一就好");
        }
        //设备IP
        if (StringUtil.isEmpty(machineDto.getMachineIp())) {
            throw new ServiceException(Result.SYS_ERROR, "未包含设备ip");
        }
        //设备mac
        if (StringUtil.isEmpty(machineDto.getMachineMac())) {
            machineDto.setMachineMac(machineDto.getMachineIp());
        }
        // 设备名称
        if (StringUtil.isEmpty(machineDto.getMachineName())) {
            machineDto.setMachineName(machineDto.getMachineCode());
        }
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);

        MachineDto tmpMachineDto = new MachineDto();
        tmpMachineDto.setMachineVersion(machineDto.getMachineVersion());
        tmpMachineDto.setMachineCode(machineDto.getMachineCode());
        tmpMachineDto.setMachineIp(machineDto.getMachineIp());
        tmpMachineDto.setMachineMac(machineDto.getMachineMac());

        long machineCnt = machineServiceDao.getMachineCount(tmpMachineDto);
        if (machineCnt > 0) {
            logger.debug("该设备已经添加无需再添加"+machineServiceDao.toString());
            return;
        }

        machineServiceDao.saveMachine(machineDto);

    }
}
