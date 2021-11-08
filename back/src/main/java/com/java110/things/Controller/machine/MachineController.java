package com.java110.things.Controller.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.machine.*;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.machine.*;
import com.java110.things.service.openDoor.IOpenDoorService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName MachineController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/machine")
public class MachineController extends BaseController {

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private IOperateLogService operateLogServiceImpl;

    @Autowired
    private ITransactionLogService transactionLogServiceImpl;

    @Autowired
    private IMachineFaceService machineFaceService;

    @Autowired
    private IOpenDoorService openDoorService;

    @Autowired
    private IMachineCmdService machineCmdService;

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveMachine", method = RequestMethod.POST)
    public ResponseEntity<String> saveMachine(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "machineMac", "请求报文中未包含硬件mac地址");

        Assert.hasKeyAndValue(paramObj, "machineCode", "请求报文中未包含硬件编码");

        Assert.hasKeyAndValue(paramObj, "machineVersion", "请求报文中未包含硬件版本号");

        Assert.hasKeyAndValue(paramObj, "machineIp", "请求报文中未包含硬件IP");

        Assert.hasKeyAndValue(paramObj, "machineName", "请求报文中未包含硬件名称，如果没有可以填写mac或者IP");

        if (!paramObj.containsKey("machineTypeCd")) {
            paramObj.put("machineTypeCd", MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        }

        paramObj.put("machineId", UUID.randomUUID().toString());


        ResultDto resultDto = machineServiceImpl.saveMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachines", method = RequestMethod.GET)
    public ResponseEntity<String> getMachines(@RequestParam int page,
                                              @RequestParam int row,
                                              @RequestParam String machineTypeCd) throws Exception {

        Assert.hasText(machineTypeCd, "请求报文中未包含设备类型");
        MachineDto machineDto = new MachineDto();
        machineDto.setPage(page);
        machineDto.setRow(row);
        machineDto.setMachineTypeCd(machineTypeCd);

        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 根据类型查询设备
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineCodes", method = RequestMethod.GET)

    public ResponseEntity<String> getMachineCodes(@RequestParam String machineTypeCd) throws Exception {

        Assert.hasText(machineTypeCd, "请求报文中未包含设备类型");
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineTypeCd(machineTypeCd);
        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMachine", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMachine(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        ResultDto resultDto = machineServiceImpl.deleteMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 重启设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/startMachine", method = RequestMethod.POST)
    public ResponseEntity<String> startMachine(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        ResultDto resultDto = machineServiceImpl.restartMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 设备开启 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/openDoor", method = RequestMethod.POST)
    public ResponseEntity<String> openDoor(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        ResultDto resultDto = machineServiceImpl.openDoor(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineLogs", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineLogs(@RequestParam int page,
                                                 @RequestParam int row,
                                                 @RequestParam String machineTypeCd,
                                                 @RequestParam(name = "logId", required = false) String logId,
                                                 @RequestParam(name = "machineCode", required = false) String machineCode,
                                                 @RequestParam(name = "machineName", required = false) String machineName) throws Exception {

        Assert.hasText(machineTypeCd, "请求报文中未包含设备类型");
        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setPage(page);
        operateLogDto.setRow(row);
        operateLogDto.setMachineTypeCd(machineTypeCd);
        operateLogDto.setLogId(logId);
        operateLogDto.setMachineCode(machineCode);
        operateLogDto.setMachineName(machineName);

        ResultDto resultDto = operateLogServiceImpl.getOperateLogs(operateLogDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineFaces", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineFaces(@RequestParam int page,
                                                  @RequestParam int row,
                                                  @RequestParam String machineTypeCd,
                                                  @RequestParam(name = "name", required = false) String logId,
                                                  @RequestParam(name = "machineCode", required = false) String machineCode,
                                                  @RequestParam(name = "machineId", required = false) String machineId,
                                                  @RequestParam(name = "machineName", required = false) String machineName) throws Exception {

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setPage(page);
        machineFaceDto.setRow(row);
        machineFaceDto.setMachineId(machineId);
        machineFaceDto.setMachineCode(machineCode);
        machineFaceDto.setMachineName(machineName);
        machineFaceDto.setMachineTypeCd(machineTypeCd);

        ResultDto resultDto = machineFaceService.getMachineFace(machineFaceDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineOpenDoors", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineOpenDoors(@RequestParam int page,
                                                      @RequestParam int row,
                                                      @RequestParam(name = "name", required = false) String logId,
                                                      @RequestParam(name = "machineCode", required = false) String machineCode,
                                                      @RequestParam(name = "machineId", required = false) String machineId,
                                                      @RequestParam(name = "machineName", required = false) String machineName,
                                                      @RequestParam(name = "userName", required = false) String userName) throws Exception {

        OpenDoorDto openDoorDto = new OpenDoorDto();
        openDoorDto.setPage(page);
        openDoorDto.setRow(row);
        openDoorDto.setMachineId(machineId);
        openDoorDto.setMachineCode(machineCode);
        openDoorDto.setMachineName(machineName);
        openDoorDto.setUserName(userName);

        ResultDto resultDto = openDoorService.getOpenDoor(openDoorDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 交互日志
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getTranLogs", method = RequestMethod.GET)
    public ResponseEntity<String> getTranLogs(@RequestParam int page,
                                              @RequestParam int row,
                                              @RequestParam(name = "tranId", required = false) String tranId,
                                              @RequestParam(name = "machineCode", required = false) String machineCode,
                                              @RequestParam(name = "machineName", required = false) String machineName) throws Exception {

        TransactionLogDto transactionLogDto = new TransactionLogDto();
        transactionLogDto.setPage(page);
        transactionLogDto.setRow(row);
        transactionLogDto.setTranId(tranId);
        transactionLogDto.setMachineCode(machineCode);
        transactionLogDto.setMachineName(machineName);

        ResultDto resultDto = transactionLogServiceImpl.getTransactionLogs(transactionLogDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备指令
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveMachineCmd", method = RequestMethod.POST)
    public ResponseEntity<String> saveMachineCmd(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "machineTypeCd", "请求报文中未包含设备类型");

        Assert.hasKeyAndValue(paramObj, "machineCode", "请求报文中未包含硬件编码");

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        Assert.hasKeyAndValue(paramObj, "cmdCode", "请求报文中未包含命令编码");

        Assert.hasKeyAndValue(paramObj, "cmdName", "请求报文中未包含命令编码名称");

        if (!paramObj.containsKey("machineTypeCd")) {
            paramObj.put("machineTypeCd", MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        }

        paramObj.put("cmdId", UUID.randomUUID().toString());
        paramObj.put("communityId", "99999");
        MachineCmdDto machineCmdDto = BeanConvertUtil.covertBean(paramObj, MachineCmdDto.class);
        machineCmdDto.setObjType(MachineConstant.MACHINE_CMD_OBJ_TYPE_SYSTEM);
        machineCmdDto.setObjTypeValue("-1");

        ResultDto resultDto = machineCmdService.saveMachineCmd(machineCmdDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 查询指令
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineCmds", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineCmds(@RequestParam int page,
                                              @RequestParam int row,
                                              @RequestParam(name = "machineCode", required = false) String machineCode,
                                              @RequestParam(name = "cmdName", required = false) String cmdName) throws Exception {

        MachineCmdDto machineCmdDto = new MachineCmdDto();
        machineCmdDto.setPage(page);
        machineCmdDto.setRow(row);
        machineCmdDto.setMachineCode(machineCode);
        machineCmdDto.setCmdName(cmdName);
        ResultDto resultDto = machineCmdService.getMachineCmd(machineCmdDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 删除指令
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMachineCmd", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMachineCmd(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "cmdId", "请求报文中未包含指令ID");

        ResultDto resultDto = machineCmdService.deleteMachineCmd(BeanConvertUtil.covertBean(paramObj, MachineCmdDto.class));
        return super.createResponseEntity(resultDto);
    }
}
