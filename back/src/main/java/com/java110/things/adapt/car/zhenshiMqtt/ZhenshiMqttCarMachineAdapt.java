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
package com.java110.things.adapt.car.zhenshiMqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.BaseMachineAdapt;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.adapt.car.ICallCarService;
import com.java110.things.adapt.car.ICarMachineProcess;
import com.java110.things.adapt.car.zhenshi.ZhenshiByteToString;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 臻识 车辆道闸摄像头接口协议
 * <p>
 * 相关 网站地址 http://vzenith.com/case/ivs-fc-productpage/
 */
@Service("zhenshiMqttCarMachineAdapt")
public class ZhenshiMqttCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {
    Logger logger = LoggerFactory.getLogger(ZhenshiMqttCarMachineAdapt.class);

    private static final String CMD_GET_RTSP_URI = "get_rtsp_uri";//获取视频连接
    private static final String CMD_HEARTBEAT = "heartbeat";//获取视频连接
    private static final String CMD_RESULT = "ivs_result";// 车牌识别结果

    @Autowired
    private IMachineService machineService;

    @Autowired
    private ICallCarService callCarServiceImpl;

    @Override
    public void initCar() {

    }

    @Override
    public void initCar(MachineDto machineDto) {


    }

    @Override
    public void readByte(MachineDto machineDto, byte[] bytes) throws Exception {

    }

    /**
     * {"AlarmInfoPlate": {"channel": 0,"deviceName": "","ipaddr": "192.168.1.100","serialno": "5cdc5841-3d038816","heartbeat": 1}}
     *
     * @param topic
     * @param s
     */
    @Override
    public void mqttMessageArrived(String topic, String s) {
        JSONObject paramIn = JSONObject.parseObject(s);
        if (!paramIn.containsKey("AlarmInfoPlate")) {
            return;
        }

        JSONObject alarmInfoPlateObj = paramIn.getJSONObject("AlarmInfoPlate");

        String serialno = alarmInfoPlateObj.getString("serialno");
        if (StringUtil.isEmpty(serialno)) {
            return;
        }

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(serialno);
        machineDto.setMachineTypeCd(MachineDto.MACHINE_TYPE_CAR); // 标识门禁 以防万一和道闸之类冲突
        ResultDto resultDto = machineService.getMachine(machineDto);
        if (resultDto == null || resultDto.getCode() != ResponseConstant.SUCCESS) {
            return;
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        if (machineDtos.size() < 1) {
            return;
        }
        if (alarmInfoPlateObj.containsKey("heartbeat")) {
            alarmInfoPlateObj.put("cmd", CMD_HEARTBEAT);
        } else {
            alarmInfoPlateObj.put("cmd", CMD_RESULT);
        }
        dealCmd(alarmInfoPlateObj, machineDtos.get(0));
    }

    private void dealCmd(JSONObject reqData, MachineDto machineDto) {
        String cmd = reqData.getString("cmd");
        switch (cmd) {
            case CMD_HEARTBEAT:
                doHeartBeat(reqData, machineDto);
                break;
            case CMD_RESULT:
                doResult(reqData, machineDto);
                break;
        }
    }

    /**
     * 车辆识别结果
     * <p>
     * {
     * <p>
     * }
     *
     * @param reqData
     * @param machineDto
     */
    private void doResult(JSONObject reqData, MachineDto machineDto) {

        try {
            JSONObject plateResult = reqData.getJSONObject("result").getJSONObject("PlateResult");
            String type = plateResult.getString("type");
            String license = plateResult.getString("license");
            if(plateResult.containsKey("imagePath")) {
                String imagePath = ImageFactory.getBase64ByImgUrl(MappingCacheFactory.getValue("OSS_URL")+plateResult.getString("imagePath"));
                machineDto.setPhotoJpg(imagePath);
            }

            ResultParkingAreaTextDto resultParkingAreaTextDto = callCarServiceImpl.ivsResult(type, license, machineDto);
            JinjieScreenMqttFactory.pay(machineDto, resultParkingAreaTextDto.getVoice());
            JinjieScreenMqttFactory.downloadTempTexts(machineDto, 1, resultParkingAreaTextDto.getText1());
            JinjieScreenMqttFactory.downloadTempTexts(machineDto, 2, resultParkingAreaTextDto.getText2());
            JinjieScreenMqttFactory.downloadTempTexts(machineDto, 3, resultParkingAreaTextDto.getText3());
            JinjieScreenMqttFactory.downloadTempTexts(machineDto, 4, resultParkingAreaTextDto.getText4());

            if (ResultDto.SUCCESS != resultParkingAreaTextDto.getCode()) {
                return; //不开门
            }
            openDoor(machineDto, null);

        } catch (Exception e) {
            logger.error("开门异常", e);
        }

    }

    /**
     * 处理视频播放地址
     *
     * @param reqData
     * @param machineDto
     */
    private void doHeartBeat(JSONObject reqData, MachineDto machineDto) {
        try {
            String heartBeatTime = null;
            heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
            MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineDto.getMachineCode(), heartBeatTime);
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);

        } catch (Exception e) {
            logger.error("心跳失败", e);
        }

    }

    @Override
    public void restartMachine(MachineDto machineDto) {

    }

    @Override
    public void openDoor(MachineDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        // 发送开闸命令
        String triggerCmd = "{\n" +
                "    \"Response_AlarmInfoPlate\": {\n" +
                "        \"channelNum\": 0,\n" +
                "        \"delay\": 1000,\n" +
                "        \"info\": \"ok\",\n" +
                "        \"msgId\": \"" + SeqUtil.getId() + "\"\n" +
                "    }\n" +
                "}";
        ZhenshiMqttSend.sendCmd(machineDto, triggerCmd);

        if (parkingAreaTextDto == null) {
            JinjieScreenMqttFactory.pay(machineDto, "欢迎光临");
            JinjieScreenMqttFactory.downloadTempTexts(machineDto, 0, "欢迎光临");
            return;
        }
        JinjieScreenMqttFactory.pay(machineDto, parkingAreaTextDto.getVoice());
        JinjieScreenMqttFactory.downloadTempTexts(machineDto, 0, parkingAreaTextDto.getText1());
        JinjieScreenMqttFactory.downloadTempTexts(machineDto, 1, parkingAreaTextDto.getText2());
        JinjieScreenMqttFactory.downloadTempTexts(machineDto, 2, parkingAreaTextDto.getText3());
        JinjieScreenMqttFactory.downloadTempTexts(machineDto, 3, parkingAreaTextDto.getText4());
    }

    @Override
    public void sendKeepAlive(MachineDto machineDto) {
        ZhenshiByteToString.sendKeepAlive(machineDto);

    }


}
