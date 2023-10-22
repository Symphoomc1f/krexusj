/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.api.car;

import com.java110.things.Controller.BaseController;
import com.java110.things.adapt.car.ICarMachineProcess;
import com.java110.things.adapt.car.ICarProcess;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.CarMachineProcessFactory;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.netty.client.CarNettyClient;
import com.java110.things.service.machine.IMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 道闸对接 接口类
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/car")
public class CarApi extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CarApi.class);

    @Autowired
    private IMachineService machineServiceImpl;


    /**
     * 设备心跳
     * <p>
     * 门禁配置地址为：/api/car/heartBeat/设备编码
     *
     * @param request request请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/heartBeat/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> heartBeat(HttpServletRequest request, @PathVariable(value = "machineCode") String machineCode) throws Exception {
        logger.debug(machineCode + "设备心跳");
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        machineDto.setMachineTypeCd("9996");
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            return ResultDto.error("设备不存在");
        }
        //设备重连
        CarNettyClient.twoGetChannel(machineDtos.get(0));

        ICarMachineProcess carMachineProcess = CarMachineProcessFactory.getCarImpl(machineDtos.get(0).getHmId());
        carMachineProcess.sendKeepAlive(machineDtos.get(0));
        return ResultDto.success();
    }

    /**
     * 第三方平台设备心跳
     * <p>
     * 门禁配置地址为：/api/car/heartBeatPlatform/设备编码
     *
     * @param request request请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/heartBeatPlatform/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> heartBeatPlatform(HttpServletRequest request, @PathVariable(value = "machineCode") String machineCode) throws Exception {
        logger.debug(machineCode + "设备心跳");
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        machineDto.setMachineTypeCd("9995");//第三方道闸设备心跳
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            return ResultDto.error("设备不存在");
        }
        //设备重连
        CarNettyClient.twoGetChannel(machineDtos.get(0));

        ICarProcess carProcess = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId());
        return carProcess.heartBeart(machineDtos.get(0));

    }

    /**
     * 设备心跳
     * <p>
     * 门禁配置地址为：/api/car/parkingUp/设备编码
     *
     * @param request request请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/parkingUp/{machineCode}", method = RequestMethod.POST)
    public ResponseEntity<String> parkingUp(HttpServletRequest request,
                                            @PathVariable(value = "machineCode") String machineCode,
                                            @RequestBody String reqJson) throws Exception {
        ResponseEntity<String> paramOut = null;
        try {
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(machineCode);
            machineDto.setMachineTypeCd("9995");
            List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

            if (machineDtos == null || machineDtos.size() < 1) {
                return ResultDto.error("设备不存在");
            }

            ICarProcess carProcess = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId());
            Java110CarProtocol java110CarProtocol = carProcess.accept(machineDtos.get(0), reqJson);

            paramOut = new ResponseEntity<String>(java110CarProtocol.getContent(), HttpStatus.OK);
        } finally {
            logger.debug("设备：" + machineCode + ",请求报文：" + reqJson + ",返回报文：" + paramOut);
        }
        return paramOut;
    }

    /**
     * 设备心跳
     * <p>
     * 门禁配置地址为：/api/car/uploadRecord/设备编码/1
     *
     * @param request request请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/uploadRecord/{machineCode}/{moreParam}", method = RequestMethod.POST)
    public ResponseEntity<String> uploadRecord(HttpServletRequest request,
                                               @PathVariable(value = "machineCode") String machineCode,
                                               @RequestBody String reqJson) throws Exception {
        ResponseEntity<String> paramOut = null;
        try {
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(machineCode);
            machineDto.setMachineTypeCd("9995");
            List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

            if (machineDtos == null || machineDtos.size() < 1) {
                return ResultDto.error("设备不存在");
            }

            ICarProcess carProcess = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId());
            Java110CarProtocol java110CarProtocol = carProcess.accept(machineDtos.get(0), reqJson);

            paramOut = new ResponseEntity<String>(java110CarProtocol.getContent(), HttpStatus.OK);
        } finally {
            logger.debug("设备：" + machineCode + ",请求报文：" + reqJson + ",返回报文：" + paramOut);
        }
        return paramOut;
    }

}
