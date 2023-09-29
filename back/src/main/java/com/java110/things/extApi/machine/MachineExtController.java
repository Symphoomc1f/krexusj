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
package com.java110.things.extApi.machine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.adapt.car.zeroOne.ZeroOneCarSocketProcessAdapt;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.car.BarrierGateControlDto;
import com.java110.things.entity.car.CarInoutDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.sip.Device;
import com.java110.things.entity.sip.DeviceChannel;
import com.java110.things.entity.sip.PushStreamDevice;
import com.java110.things.factory.RedisCacheFactory;
import com.java110.things.service.car.ICarInoutService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.sip.Server;
import com.java110.things.sip.SipLayer;
import com.java110.things.sip.TCPServer;
import com.java110.things.sip.UDPServer;
import com.java110.things.sip.callback.OnProcessListener;
import com.java110.things.sip.message.config.ConfigProperties;
import com.java110.things.sip.message.session.MessageManager;
import com.java110.things.sip.message.session.SyncFuture;
import com.java110.things.sip.remux.Observer;
import com.java110.things.sip.remux.RtmpPusher;
import com.java110.things.sip.session.PushStreamDeviceManager;
import com.java110.things.util.*;
import com.java110.things.ws.BarrierGateControlWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sip.Dialog;
import javax.sip.SipException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 设备对外 控制类
 * <p>
 * 完成小区添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/machine")
public class MachineExtController extends BaseController implements OnProcessListener {
    private static Logger logger = LoggerFactory.getLogger(MachineExtController.class);

    @Autowired
    IMachineService machineServiceImpl;

    @Autowired
    ICommunityService communityServiceImpl;

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private SipLayer mSipLayer;

    @Autowired
    private ConfigProperties configProperties;

    private MessageManager mMessageManager = MessageManager.getInstance();

    private PushStreamDeviceManager mPushStreamDeviceManager = PushStreamDeviceManager.getInstance();

    /**
     * 添加设备信息
     * <p>
     *
     * @param reqParam {
     *                 "machineCode":""
     *                 machine_name
     *                 machine_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_machine_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addMachine", method = RequestMethod.POST)
    public ResponseEntity<String> addMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");
        Assert.hasKeyAndValue(reqJson, "machineName", "未包含设备名称");
        Assert.hasKeyAndValue(reqJson, "machineTypeCd", "未包含设备类型");
        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "locationType", "未包含位置类型");
        Assert.hasKeyAndValue(reqJson, "locationObjId", "未包含位置ID");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");
        Assert.hasKeyAndValue(reqJson, "direction", "未包含设备方向");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        machineDto.setMachineId(SeqUtil.getId());
        machineDto.setCommunityId(communityDtos.get(0).getCommunityId());
        if (!reqJson.containsKey("machineVersion")) {
            machineDto.setMachineVersion("v1.0");
        }
        if (!reqJson.containsKey("machineIp")) {
            machineDto.setMachineIp("192.168.1.1:80");
        }
        ResultDto result = machineServiceImpl.saveMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改设备信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC小区",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateMachine", method = RequestMethod.POST)
    public ResponseEntity<String> updateMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");


        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);


        ResultDto result = machineServiceImpl.updateMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除设备信息
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMachine", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.deleteMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 远程开门
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/openDoor", method = RequestMethod.POST)
    public ResponseEntity<String> openDoor(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.openDoor(machineDto, null);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 获取二维码
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getQRcode", method = RequestMethod.POST)
    public ResponseEntity<String> getQRcode(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "userId", "未包含人员ID");
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含人员设备");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        UserFaceDto userFaceDto = BeanConvertUtil.covertBean(reqJson, UserFaceDto.class);
        ResultDto result = machineServiceImpl.getQRcode(userFaceDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 重启设备
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/restartMachine", method = RequestMethod.POST)
    public ResponseEntity<String> restartMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.restartMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 重启设备
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/customCarInOut", method = RequestMethod.POST)
    public ResponseEntity<String> customCarInOut(@RequestBody String reqParam) throws Exception {

        JSONObject paramObj = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(paramObj, "extMachineId", "请求报文中未包含设备ID");
        Assert.hasKeyAndValue(paramObj, "carNum", "请求报文中未包含车辆编号");
        Assert.hasKeyAndValue(paramObj, "type", "请求报文中未包含类型");

        MachineDto machineDto = new MachineDto();
        machineDto.setExtMachineId(paramObj.getString("extMachineId"));
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "设备不存在");
        machineDto = machineDtos.get(0);
        ResultDto resultDto = null;
        ResultParkingAreaTextDto parkingAreaTextDto = null;
        if ("1101".equals(paramObj.getString("type"))) {
            uploadcarin(machineDto, paramObj);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, paramObj.getString("carNum"),
                    "欢迎光临", "", "", paramObj.getString("carNum") + ",欢迎光临", paramObj.getString("carNum"));
        } else {
            uploadcarout(machineDto, paramObj);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, paramObj.getString("carNum"),
                    "一路平安", "", "", paramObj.getString("carNum") + ",一路平安", paramObj.getString("carNum"));
        }
        resultDto = machineServiceImpl.openDoor(machineDto, parkingAreaTextDto);

        return ResultDto.createResponseEntity(resultDto);
    }

    /**
     * 出场上报
     *
     * @param machineDto
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarout(MachineDto machineDto, JSONObject acceptJson) throws Exception {
        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState(CarInoutDto.STATE_IN);
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutDto.setPayType(CarInoutDto.PAY_TYPE_CASH);
            carInoutDto.setPayCharge(acceptJson.getString("amount"));
            carInoutDto.setRealCharge(acceptJson.getString("amount"));
            carInoutDto.setPayTime(DateUtil.getCurrentDate());
            carInoutServiceImpl.updateCarInout(carInoutDto);
        }
        carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState("3");
        carInoutDto.setRemark("手工出场");
        if (acceptJson.containsKey("payCharge")) {
            carInoutDto.setPayCharge(acceptJson.getString("payCharge"));
        } else {
            carInoutDto.setPayCharge(acceptJson.getString("amount"));
        }
        carInoutDto.setRealCharge(acceptJson.getString("amount"));
        carInoutDto.setPayType("1");
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutServiceImpl.saveCarInout(carInoutDto);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工出场", "开门成功");
        BarrierGateControlWebSocketServer.sendInfo(barrierGateControlDto.toString(), machineDto.getLocationObjId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }

    /**
     * 车辆进场记录
     *
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarin(MachineDto machineDto, JSONObject acceptJson) throws Exception {
        //2.0 手工进场
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState("1");
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        carInoutDto.setRemark("手工进场");
        carInoutServiceImpl.saveCarInout(carInoutDto);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工进场", "开门成功");
        BarrierGateControlWebSocketServer.sendInfo(barrierGateControlDto.toString(), machineDto.getLocationObjId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }

    /**
     * 播放视频
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/playVideo", method = RequestMethod.POST)
    public ResponseEntity<String> playVideo(@RequestBody String reqParam) throws Exception {

        JSONObject paramIn = JSONObject.parseObject(reqParam);
        ///play/{deviceId}/{channelId}/{mediaProtocol}
        Assert.hasKeyAndValue(paramIn, "deviceId", "未包含deviceId");
        Assert.hasKeyAndValue(paramIn, "channelId", "未包含channelId");
        Assert.hasKeyAndValue(paramIn, "mediaProtocol", "未包含mediaProtocol");

        String deviceId = paramIn.getString("deviceId");
        String channelId = paramIn.getString("channelId");
        String mediaProtocol = paramIn.getString("mediaProtocol");

        JSONObject result = new JSONObject();
        try {
            //1.从redis查找设备，如果不存在，返回离线
            String deviceStr = RedisUtil.get(deviceId);
            if (StringUtils.isEmpty(deviceStr)) {
                //throw new IllegalArgumentException("设备离线");
                return ResultDto.error("设备离线");
            }
            //2.设备在线，先检查是否正在推流
            //如果正在推流，直接返回rtmp地址
            String streamName = StreamNameUtils.play(deviceId, channelId);
            PushStreamDevice pushStreamDevice = mPushStreamDeviceManager.get(streamName);
            if (pushStreamDevice != null) {
                result.put("address", configProperties.getPullRtmpAddress().concat(streamName));
                result.put("callId", pushStreamDevice.getCallId());
                prolongedSurvival(pushStreamDevice.getCallId());
                return ResultDto.createResponseEntity(result);
            }
            //检查通道是否存在
            Device device = JSONObject.parseObject(deviceStr, Device.class);
            Map<String, DeviceChannel> channelMap = device.getChannelMap();
            if (channelMap == null || !channelMap.containsKey(channelId)) {
                //throw new IllegalArgumentException("通道不存在");
                return ResultDto.error("通道不存在");
            }
            boolean isTcp = mediaProtocol.toUpperCase().equals(SipLayer.TCP);
            //3.下发指令
            String callId = IDUtils.id();
            //getPort可能耗时，在外面调用。
            int port = mSipLayer.getPort();
            String ssrc = mSipLayer.getSsrc(true);
            mSipLayer.sendInvite(device, SipLayer.SESSION_NAME_PLAY, callId, channelId, port, ssrc, isTcp);
            //4.等待指令响应
            SyncFuture<?> receive = mMessageManager.receive(callId);
            Dialog response = (Dialog) receive.get(3, TimeUnit.SECONDS);

            //4.1响应成功，创建推流session
            if (response != null) {
                String address = configProperties.getPushRtmpAddress().concat(streamName);
                Server server = isTcp ? new TCPServer() : new UDPServer();
                Observer observer = new RtmpPusher(address, callId);

                server.subscribe(observer);
                pushStreamDevice = new PushStreamDevice(deviceId, Integer.valueOf(ssrc), callId, streamName, port, isTcp, server,
                        observer, address);

                pushStreamDevice.setDialog(response);
                server.startServer(pushStreamDevice.getFrameDeque(), Integer.valueOf(ssrc), port, false);
                observer.startRemux();

                observer.setOnProcessListener(this);
                mPushStreamDeviceManager.put(streamName, callId, Integer.valueOf(ssrc), pushStreamDevice);
                //result = GBResult.ok(new MediaData(configProperties.getPullRtmpAddress().concat(streamName), pushStreamDevice.getCallId()));
                result.put("address", configProperties.getPullRtmpAddress().concat(streamName));
                result.put("callId", pushStreamDevice.getCallId());
                prolongedSurvival(pushStreamDevice.getCallId());
            } else {
                //3.2响应失败，删除推流session
                mMessageManager.remove(callId);
                //throw new IllegalArgumentException("摄像头 指令未响应");
                return ResultDto.error("摄像头 指令未响应");
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
            throw new IllegalArgumentException("系统异常");
        }
        return ResultDto.createResponseEntity(result);
    }

    /**
     * 存活检测
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/heartbeatVideo", method = RequestMethod.POST)
    public ResponseEntity<String> heartbeatVideo(@RequestBody String reqParam) throws Exception {

        JSONObject paramIn = JSONObject.parseObject(reqParam);
        ///play/{deviceId}/{channelId}/{mediaProtocol}
        Assert.hasKeyAndValue(paramIn, "callIds", "未包含callIds");
        String callIds = paramIn.getString("callIds");

        for (String callId : callIds.split(",")) {
            if (StringUtil.isEmpty(callId)) {
                continue;
            }
            prolongedSurvival(callId);
        }

        return ResultDto.success();
    }

    /**
     * 延长存活时间
     *
     * @param callId
     */
    private void prolongedSurvival(String callId) {
        RedisCacheFactory.setValue(callId + "_callId", callId, 60);

        String calls = RedisCacheFactory.getValue("VEDIO_CALLS");
        JSONArray callIds = null;
        if (StringUtil.isEmpty(calls)) {
            callIds = JSONArray.parseArray(calls);
        } else {
            callIds = new JSONArray();
            callIds.add(callId);
        }

        if (!hasCallIn(callIds, callId)) {
            callIds.add(callId);
        }

        RedisCacheFactory.setValue("VEDIO_CALLS", callIds.toJSONString());
    }

    private boolean hasCallIn(JSONArray callIds, String callId) {
        String tmpCallId = "";
        for (int callIndex = 0; callIndex < callIds.size(); callIndex++) {
            tmpCallId = callIds.getString(callIndex);
            if (tmpCallId.equals(callId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重启设备
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/byeVideo", method = RequestMethod.POST)
    public ResponseEntity<String> byeVideo(@RequestBody String reqParam) throws Exception {

        JSONObject paramIn = JSONObject.parseObject(reqParam);
        ///play/{deviceId}/{channelId}/{mediaProtocol}
        Assert.hasKeyAndValue(paramIn, "callId", "未包含callId");
        String callId = paramIn.getString("callId");
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
            return ResultDto.error(e.getLocalizedMessage());
        }
        return ResultDto.success();
    }

    @Override
    public void onError(String callId) {
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
        }
    }
}
