package com.java110.things.Controller.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.service.machine.IMachineService;
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
}