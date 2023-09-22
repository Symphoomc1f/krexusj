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

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.BaseMachineAdapt;
import com.java110.things.adapt.car.ICallCarService;
import com.java110.things.adapt.car.ICarMachineProcess;
import com.java110.things.entity.machine.MachineAttrDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.machine.IMachineService;
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
@Service("zhenshiCarMachineAdapt")
public class ZhenshiCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {
    Logger logger = LoggerFactory.getLogger(ZhenshiCarMachineAdapt.class);

    private static final String CMD_GET_RTSP_URI = "get_rtsp_uri";//获取视频连接
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
        //初始化配置
        ZhenshiByteToString.configFormat(machineDto, 1, 1, 1);

        //获取视频连接地址
        getRtspUri(machineDto);

    }

    public void getRtspUri(MachineDto machineDto) {
        String cmd;
        String logId = SeqUtil.getMachineSeq();
        cmd = String.format("{" +
                "\"cmd\" : \"get_rtsp_uri\"," +
                "\"id\" : \"" + logId + "\"}");
        ZhenshiByteToString.sendCmd(machineDto, cmd);
        saveLog(logId, machineDto.getMachineId(), CMD_GET_RTSP_URI, cmd, "");
    }

    @Override
    public void readByte(MachineDto machineDto, byte[] bytes) throws Exception {

        String data = ZhenshiByteToString.getDataByByte(bytes);
        //如果读取到数据为空 啥事不干
        if (StringUtil.isEmpty(data)) {
            return;
        }

        logger.debug("臻识道闸传入数据：" + data);

        JSONObject reqData = JSONObject.parseObject(data);

        if (reqData.containsKey("cmd")) { //指令模式
            dealCmd(reqData, machineDto);
        }

    }

    private void dealCmd(JSONObject reqData, MachineDto machineDto) throws Exception {
        String cmd = reqData.getString("cmd");
        switch (cmd) {
            case CMD_GET_RTSP_URI:
                doGetRtspUriResult(reqData, machineDto);
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
     * "PlateResult": {
     * "featureCode": true,
     * "timeUsed": 85,
     * "plate_true_width": 0,
     * "confidence": 100,
     * "bright": 0,
     * "carBright": 0,
     * "type": 1,
     * "enable_encrypt": 1,
     * "colorType": 1,
     * "timeStamp": {
     * "Timeval": {
     * "sec": 1610940292,
     * "usec": 391030
     * }
     * },
     * "fake_plate": 0,
     * "license": "ANX9Q3ftqPOVPJd1Tox8SQ==",
     * "plate_distance": 0,
     * "carColor": 0,
     * "car_brand": {
     * "year": 65535,
     * "type": 255,
     * "brand": 255
     * },
     * "colorValue": 0,
     * "location": {
     * "RECT": {
     * "top": 736,
     * "left": 905,
     * "bottom": 783,
     * "right": 1076
     * }
     * },
     * "triggerType": 8,
     * "direction": 4
     * },
     * "fullImgSize": 84866,
     * "active_id": 1,
     * "photoJpg": "gkKFhcYGRJiilopSdkB/9k=",
     * "timeString": "2021-01-18 11:24:52",
     * "cmd": "ivs_result",
     * "id": 65,
     * "imageformat": "jpg",
     * "clipImgSize": 0
     * }
     *
     * @param reqData
     * @param machineDto
     */
    private void doResult(JSONObject reqData, MachineDto machineDto) {

        try {

            JSONObject plateResult = reqData.getJSONObject("PlateResult");

            String type = plateResult.getString("type");

            String license = plateResult.getString("license");

            machineDto.setPhotoJpg(reqData.getString("photoJpg"));

            ResultParkingAreaTextDto resultParkingAreaTextDto = callCarServiceImpl.ivsResult(type, license, machineDto);
            JinjieScreenFactory.pay(machineDto, resultParkingAreaTextDto.getVoice());
            JinjieScreenFactory.downloadTempTexts(machineDto, 1, resultParkingAreaTextDto.getText1());
            JinjieScreenFactory.downloadTempTexts(machineDto, 2, resultParkingAreaTextDto.getText2());
            JinjieScreenFactory.downloadTempTexts(machineDto, 3, resultParkingAreaTextDto.getText3());
            JinjieScreenFactory.downloadTempTexts(machineDto, 4, resultParkingAreaTextDto.getText4());

            if (ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
            ) {
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
    private void doGetRtspUriResult(JSONObject reqData, MachineDto machineDto) throws Exception {

        MachineAttrDto machineAttrDto = new MachineAttrDto();
        machineAttrDto.setSpecCd(MachineAttrDto.SPEC_VEDIO_URL);
        machineAttrDto.setMachineId(machineDto.getMachineId());
        List<MachineAttrDto> machineAttrDtos = machineService.queryMachineAttrs(machineAttrDto);
        MachineAttrDto tmpMachineAttr = new MachineAttrDto();
        tmpMachineAttr.setMachineId(machineDto.getMachineId());
        tmpMachineAttr.setSpecCd(MachineAttrDto.SPEC_VEDIO_URL);
        tmpMachineAttr.setValue(reqData.getString("uri"));
        tmpMachineAttr.setCommunityId(machineDto.getCommunityId());
        if (machineAttrDtos == null || machineAttrDtos.size() < 1) {
            tmpMachineAttr.setAttrId(SeqUtil.getId());
            machineService.saveMachineAttr(tmpMachineAttr);
        } else {
            machineService.updateMachineAttr(tmpMachineAttr);
        }
        saveLog(reqData.getString("id"), machineDto.getMachineId(), CMD_GET_RTSP_URI, "", reqData.toJSONString());
    }

    @Override
    public void restartMachine(MachineDto machineDto) {

    }

    @Override
    public void openDoor(MachineDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        // 发送开闸命令
        String triggerCmd = "{\"cmd\":\"ioctl\",\"io\" :0,\"value\":2,\"delay\":500}";
        ZhenshiByteToString.sendCmd(machineDto, triggerCmd);

        if (parkingAreaTextDto == null) {
            JinjieScreenFactory.pay(machineDto, "欢迎光临");
            JinjieScreenFactory.downloadTempTexts(machineDto, 0, "欢迎光临");
            return;
        }
        JinjieScreenFactory.pay(machineDto, parkingAreaTextDto.getVoice());
        JinjieScreenFactory.downloadTempTexts(machineDto, 0, parkingAreaTextDto.getText1());
        JinjieScreenFactory.downloadTempTexts(machineDto, 1, parkingAreaTextDto.getText2());
        JinjieScreenFactory.downloadTempTexts(machineDto, 2, parkingAreaTextDto.getText3());
        JinjieScreenFactory.downloadTempTexts(machineDto, 3, parkingAreaTextDto.getText4());
    }

    @Override
    public void sendKeepAlive(MachineDto machineDto) {
        ZhenshiByteToString.sendKeepAlive(machineDto);

    }

    @Override
    public void mqttMessageArrived(String topic, String s) {

    }
}
