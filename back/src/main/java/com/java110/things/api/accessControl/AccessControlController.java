package com.java110.things.api.accessControl;

import com.java110.things.Controller.BaseController;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CommunityController
 * @Description TODO 门禁推送地址类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/accessControl")
public class AccessControlController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AccessControlController.class);

    @Autowired
    private IMachineService machineServiceImpl;

    /**
     * 添加设备接口类
     * <p>
     * 门禁配置地址为：/api/accessControl/faceResult/设备编码
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/faceResult/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> faceResult(@RequestBody String param, @PathVariable(value = "machineCode") String machineCode) throws Exception {

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "未包含该设备");
        logger.debug("请求报文：" + param);
        IAssessControlProcess assessControlProcess = AccessControlProcessFactory.getAssessControlProcessImpl(machineDtos.get(0).getHmId());

        return new ResponseEntity<String>(assessControlProcess.httpFaceResult(param), HttpStatus.OK);
    }

    /**
     * 设备心跳
     * <p>
     * 门禁配置地址为：/api/accessControl/heartBeat/设备编码
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/heartBeat/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> heartBeat(@RequestBody String param, @PathVariable(value = "machineCode") String machineCode) throws Exception {

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "未包含该设备");
        logger.debug("请求报文：" + param);
        IAssessControlProcess assessControlProcess = AccessControlProcessFactory.getAssessControlProcessImpl(machineDtos.get(0).getHmId());

        return new ResponseEntity<String>(assessControlProcess.heartbeat(param, machineCode), HttpStatus.OK);
    }

}
