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
package com.java110.things.adapt.car.zhenshi;

import com.java110.things.adapt.car.ICarMachineProcess;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 臻识 车辆道闸摄像头接口协议
 *
 * 相关 网站地址 http://vzenith.com/case/ivs-fc-productpage/
 */
@Service("zhenshiCarMachineAdapt")
public class ZhenshiCarMachineAdapt implements ICarMachineProcess {
    Logger logger = LoggerFactory.getLogger(ZhenshiCarMachineAdapt.class);

    @Override
    public void initCar() {

    }

    @Override
    public void initCar(MachineDto machineDto) {
        //初始化配置
        ZhenshiByteToString.configFormat(machineDto, 1, 1, 1);


    }

    @Override
    public void readByte(MachineDto machineDto, byte[] bytes) {

        String data = ZhenshiByteToString.getDataByByte(bytes);
        //如果读取到数据为空 啥事不干
        if (StringUtil.isEmpty(data)) {
            return;
        }

        logger.debug("臻识道闸传入数据：" + data);

    }

    @Override
    public void restartMachine(MachineDto machineDto) {

    }

    @Override
    public void openDoor(MachineDto machineDto) {
        // 发送开闸命令
        String triggerCmd = "{\"cmd\":\"ioctl\",\"io\" :0,\"value\":2,\"delay\":500}";
        ZhenshiByteToString.sendCmd(machineDto, triggerCmd);
    }

    @Override
    public void sendKeepAlive(MachineDto machineDto) {
        ZhenshiByteToString.sendKeepAlive(machineDto);
    }
}
