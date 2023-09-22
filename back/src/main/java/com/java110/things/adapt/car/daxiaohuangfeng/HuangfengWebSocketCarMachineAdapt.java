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
package com.java110.things.adapt.car.daxiaohuangfeng;

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
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.DateUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大小黄蜂 车辆道闸摄像头接口协议
 * <p>
 * 相关 网站地址 http://vzenith.com/case/ivs-fc-productpage/
 */
@Service("huangfengWebSocketCarMachineAdapt")
public class HuangfengWebSocketCarMachineAdapt extends BaseMachineAdapt implements ICarMachineProcess {
    Logger logger = LoggerFactory.getLogger(HuangfengWebSocketCarMachineAdapt.class);

    private static final String CMD_GET_RTSP_URI = "get_rtsp_uri";//获取视频连接
    private static final String CMD_HEARTBEAT = "Dev.HB";//获取视频连接
    private static final String CMD_RESULT = "ivs_result";// 车牌识别结果

    private static Map<String, String> voiceMap = new HashMap<>();

    static {
        voiceMap.put("0", "0");
        voiceMap.put("1", "1");
        voiceMap.put("2", "2");
        voiceMap.put("3", "3");
        voiceMap.put("4", "4");
        voiceMap.put("5", "5");
        voiceMap.put("6", "6");
        voiceMap.put("7", "7");
        voiceMap.put("8", "8");
        voiceMap.put("9", "9");
        voiceMap.put("A", "46");
        voiceMap.put("B", "47");
        voiceMap.put("C", "48");
        voiceMap.put("D", "49");
        voiceMap.put("E", "50");
        voiceMap.put("F", "51");
        voiceMap.put("G", "52");
        voiceMap.put("H", "53");
        voiceMap.put("I", "54");
        voiceMap.put("J", "55");
        voiceMap.put("K", "56");
        voiceMap.put("L", "57");
        voiceMap.put("M", "58");
        voiceMap.put("N", "59");
        voiceMap.put("O", "60");
        voiceMap.put("P", "61");
        voiceMap.put("Q", "62");
        voiceMap.put("R", "63");
        voiceMap.put("S", "64");
        voiceMap.put("T", "65");
        voiceMap.put("U", "66");
        voiceMap.put("V", "67");
        voiceMap.put("W", "68");
        voiceMap.put("X", "69");
        voiceMap.put("Y", "70");
        voiceMap.put("Z", "71");
        voiceMap.put("澳", "72");
        voiceMap.put("藏", "73");
        voiceMap.put("川", "74");
        voiceMap.put("鄂", "75");
        voiceMap.put("甘", "76");
        voiceMap.put("赣", "77");
        voiceMap.put("港", "78");
        voiceMap.put("贵", "79");
        voiceMap.put("桂", "80");
        voiceMap.put("黑", "81");
        voiceMap.put("沪", "81");
        voiceMap.put("吉", "83");
        voiceMap.put("冀", "84");
        voiceMap.put("津", "85");
        voiceMap.put("晋", "86");
        voiceMap.put("京", "87");
        voiceMap.put("警", "88");
        voiceMap.put("军", "89");
        voiceMap.put("辽", "90");
        voiceMap.put("鲁", "91");
        voiceMap.put("蒙", "92");
        voiceMap.put("闽", "93");
        voiceMap.put("宁", "94");
        voiceMap.put("青", "95");
        voiceMap.put("琼", "96");
        voiceMap.put("陕", "97");
        voiceMap.put("苏", "98");
        voiceMap.put("台", "99");
        voiceMap.put("皖", "100");
        voiceMap.put("湘", "101");
        voiceMap.put("新", "102");
        voiceMap.put("渝", "103");
        voiceMap.put("豫", "104");
        voiceMap.put("粤", "105");
        voiceMap.put("云", "106");
        voiceMap.put("浙", "107");
    }

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
     * @param cmd
     * @param s
     */
    @Override
    public void mqttMessageArrived(String cmd, String s) {
        JSONObject paramIn = JSONObject.parseObject(s);

        String machineCode = paramIn.getString("devNO");

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        machineDto.setMachineTypeCd(MachineDto.MACHINE_TYPE_CAR); // 标识门禁 以防万一和道闸之类冲突
        ResultDto resultDto = machineService.getMachine(machineDto);
        if (resultDto == null || resultDto.getCode() != ResponseConstant.SUCCESS) {
            return;
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        if (machineDtos.size() < 1) {
            return;
        }

        dealCmd(paramIn, machineDtos.get(0));
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
            JSONObject data = HuangfengAuth.decryptData(reqData).getJSONObject("data");
            String type = data.getString("type");
            String license = data.getString("carNO");
            if (data.containsKey("picMax")) {
                //String imagePath = ImageFactory.getBase64ByImgUrl(MappingCacheFactory.getValue("OSS_URL") + data.getString("imagePath"));
                String imagePath = ImageFactory.getBase64ByImgUrl(data.getString("picMax"));
                machineDto.setPhotoJpg(imagePath);
            }

            ResultParkingAreaTextDto resultParkingAreaTextDto = callCarServiceImpl.ivsResult(type, license, machineDto);


            if (ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
                    || ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS == resultParkingAreaTextDto.getCode()
            ) {
                openDoor(machineDto, resultParkingAreaTextDto, false);
                return; //不开门
            }

            //不开门
            openDoor(machineDto, resultParkingAreaTextDto, false);

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

    /***
     * JinjieScreenMqttFactory.pay(machineDto, resultParkingAreaTextDto.getVoice());
     *             JinjieScreenMqttFactory.downloadTempTexts(machineDto, 1, resultParkingAreaTextDto.getText1());
     *             JinjieScreenMqttFactory.downloadTempTexts(machineDto, 2, resultParkingAreaTextDto.getText2());
     *             JinjieScreenMqttFactory.downloadTempTexts(machineDto, 3, resultParkingAreaTextDto.getText3());
     *             JinjieScreenMqttFactory.downloadTempTexts(machineDto, 4, resultParkingAreaTextDto.getText4());
     *
     *             if (ResultDto.SUCCESS != resultParkingAreaTextDto.getCode()) {
     *                 return; //不开门
     *             }
     * @param machineDto 硬件信息
     * @param parkingAreaTextDto
     */
    @Override
    public void openDoor(MachineDto machineDto, ParkingAreaTextDto parkingAreaTextDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", "Spa.Control");
        paramIn.put("packageID", DateUtil.getCurrentDate().getTime());
        paramIn.put("devNO", machineDto.getMachineCode());
        paramIn.put("devIP", machineDto.getMachineIp());
        paramIn.put("time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        JSONObject data = new JSONObject();
        data.put("cmdName", "SetLCDItems");
        data.put("template", 1);
        data.put("adID", 1);
        if (parkingAreaTextDto == null) {
            data.put("items", "1,欢迎光临, , , ");
            data.put("voice", "26:欢迎光临");
            data.put("action", 1);
            paramIn.put("data", data);
            HuangfengWebSocketServer.sendInfo(HuangfengAuth.encryptData(paramIn).toJSONString(), machineDto.getMachineCode());
            return;
        }
    }

    public void openDoor(MachineDto machineDto, ResultParkingAreaTextDto resultParkingAreaTextDto, boolean openDoor) {
        // 发送开闸命令

        JSONObject paramIn = new JSONObject();
        paramIn.put("cmd", "Spa.Control");
        paramIn.put("packageID", DateUtil.getCurrentDate().getTime());
        paramIn.put("devNO", machineDto.getMachineCode());
        paramIn.put("devIP", machineDto.getMachineIp());
        paramIn.put("time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        JSONObject data = new JSONObject();
        data.put("cmdName", "SetLCDItems");
        data.put("template", 1);
        data.put("adID", 1);
        String items = "1";
        if (StringUtil.isEmpty(resultParkingAreaTextDto.getText1())) {
            items += ", ";
        } else {
            items += ("," + resultParkingAreaTextDto.getText1());
        }

        if (StringUtil.isEmpty(resultParkingAreaTextDto.getText2())) {
            items += ", ";
        } else {
            items += ("," + resultParkingAreaTextDto.getText2());
        }

        if (StringUtil.isEmpty(resultParkingAreaTextDto.getText3())) {
            items += ", ";
        } else {
            items += ("," + resultParkingAreaTextDto.getText3());
        }

        if (StringUtil.isEmpty(resultParkingAreaTextDto.getText4())) {
            items += ", ";
        } else {
            items += ("," + resultParkingAreaTextDto.getText4());
        }
        data.put("items", getItem(resultParkingAreaTextDto, false));
        data.put("voice", getItem(resultParkingAreaTextDto, true));
        if (openDoor) {
            data.put("action", 1);
        } else {
            data.put("action", 0);
        }
        paramIn.put("data", data);
        HuangfengWebSocketServer.sendInfo(HuangfengAuth.encryptData(paramIn).toJSONString(), machineDto.getMachineCode());
    }

    /**
     * 获取文本
     * <p>
     * public static final int CODE_CAR_IN_SUCCESS = 0; // carNum, "欢迎光临", "", "", carNum + ",欢迎光临"
     * public static final int CODE_MONTH_CAR_SUCCESS = 1; // "月租车," + carNum + ",欢迎光临", "开门成功"
     * public static final int CODE_TEMP_CAR_SUCCESS = 2; //"临时车," + carNum + ",欢迎光临", "开门成功"
     * public static final int CODE_FREE_CAR_OUT_SUCCESS = 3; //"免费车辆", "", "", carNum + ",免费车辆"
     * public static final int CODE_MONTH_CAR_OUT_SUCCESS = 4; // "月租车剩余" + day + "天", "", "", carNum + ",月租车"
     * public static final int CODE_TEMP_CAR_OUT_SUCCESS = 5; //"临时车," + carNum + ",欢迎光临", "开门成功"
     * public static final int CODE_CAR_OUT_SUCCESS = 6; // "车未入场", "", "", carNum + ",车未入场"
     * //carNum + ",停车" + result.getHours() + "时" + result.getMin() + "分,请交费" + result.getPayCharge() + "元"
     * public static final int CODE_CAR_BLACK = 101; // 黑名单 "此车为黑名单车辆", carNum + ",禁止通行", "", "", "此车为黑名单车辆," + carNum + ",禁止通行"
     * public static final int CODE_CAR_INED = 102; // 车辆已在场 "车已在场", "", "", carNum + ",车已在场"
     * public static final int CODE_CAR_IN_ERROR = 103; // 车辆入场失败  "禁止入场", "", "", carNum + ",禁止入场"
     * public static final int CODE_CAR_NO_IN = 104; // "车未入场", "", "", carNum + ",车未入场"
     * public static final int CODE_CAR_NO_PRI = 105; // "车未入场", "", "", carNum + ",车未入场"
     * public static final int CODE_CAR_OUT_ERROR = 106; // "停车" + result.getHours() + "时" + result.getMin() + "分", "请交费" + result.getPayCharge() + "元", "", "",
     *
     * @param resultParkingAreaTextDto
     * @return
     */
    private String getItem(ResultParkingAreaTextDto resultParkingAreaTextDto, boolean isVoice) {

        String itemText = "1, ";
        String voice = "";
        switch (resultParkingAreaTextDto.getCode()) {
            case ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",欢迎光临, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "26:欢迎光临");
                break;
            case ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS:
                itemText += ("月租车," + resultParkingAreaTextDto.getCarNum() + ",欢迎光临, ");
                voice += ("125:月租车," + getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "26:欢迎光临");
                break;
            case ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS:
                itemText += ("临时车," + resultParkingAreaTextDto.getCarNum() + ",祝您平安, ");
                voice += ("123:临时车," + getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "202:祝您平安");
                break;
            case ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS:
                itemText += ("免费车," + resultParkingAreaTextDto.getCarNum() + ",祝您平安, ");
                voice += ("126:免费车," + getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "202:祝您平安");
                break;
            case ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",月租还有, " + getDayVoice(resultParkingAreaTextDto.getDay() + "") + ",天到期");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "137:月租还有, " + resultParkingAreaTextDto.getDay() + ",138:天到期");
                break;
            case ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",祝您平安, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "202:祝您平安");
            case ResultParkingAreaTextDto.CODE_CAR_BLACK:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",黑名单车牌, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "112:黑名单车牌");
            case ResultParkingAreaTextDto.CODE_CAR_INED:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",祝您平安, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "202:祝您平安");
            case ResultParkingAreaTextDto.CODE_CAR_IN_ERROR:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",此车已入场, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "115:此车已入场");
            case ResultParkingAreaTextDto.CODE_CAR_NO_IN:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",未找到进场记录, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "223:未找到进场记录");
                break;
            case ResultParkingAreaTextDto.CODE_CAR_NO_PRI:
                itemText += (resultParkingAreaTextDto.getCarNum() + ",禁止入场, , ");
                voice += (getCarNumVoice(resultParkingAreaTextDto.getCarNum()) + "155:禁止入场");
                break;
            case ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR:
                itemText += ("停车时长" + resultParkingAreaTextDto.getHours() + "小时" + resultParkingAreaTextDto.getMin() + "分,请缴费" + resultParkingAreaTextDto.getPayCharge() + "元, , ");
                voice += ("218:停车时长," + resultParkingAreaTextDto.getHours() + ",134:小时," + resultParkingAreaTextDto.getMin() + ",17:分,31:请缴费," + resultParkingAreaTextDto.getPayCharge() + ",15:元");
                break;
        }

        if (isVoice) {
            return voice;
        }

        return itemText;

    }

    private String getDayVoice(String day) {
        String str = "";
        double dayP = Double.parseDouble(day);
        //万
        double wan = Math.floor(dayP / 10000);
        if (wan != 0) {
            str += (wan + ":" + wan + ",14:万,");
        }
        //千
        double qian = Math.floor(dayP / 1000);
        if (qian != 0) {
            str += (qian + ":" + qian + ",13:千,");
        }
        //百
        double bai = Math.floor(dayP / 100);
        if (bai != 0) {
            str += (bai + ":" + bai + ",12:百,");
        }
        //十
        double shi = Math.floor(dayP / 10);
        if (shi != 0) {
            str += (shi + ":" + shi + ",11:十,");
        }
        //元
        double yuan = Math.floor(dayP / 10);
        if (yuan != 0) {
            str += (yuan + ":" + yuan + ",15:元,");
        }

        return str;
    }

    public static void main(String[] args) {
        double dayP = 10;
        double wan = Math.floor(dayP / 10000);
        System.out.println(wan);
    }

    private String getCarNumVoice(String carNum) {
        if (StringUtil.isEmpty(carNum)) {
            return "";
        }

        String voiceCarNum = "";
        for (int carNumIndex = 0; carNumIndex < carNum.length(); carNumIndex++) {
            voiceCarNum += (voiceMap.get(carNum.charAt(carNumIndex)) + ":" + carNum.charAt(carNumIndex) + ",");
        }

        return voiceCarNum;
    }


    @Override
    public void sendKeepAlive(MachineDto machineDto) {
        ZhenshiByteToString.sendKeepAlive(machineDto);

    }


}
