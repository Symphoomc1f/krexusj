package com.java110.things.mqtt;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.CarMachineProcessFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.StringUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * MQTT 推送回调
 *
 * @author wuxw
 * @date 2020-05-20
 */
public class MqttPushCallback implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MqttPushCallback.class);
    private MqttClient client;

    private MqttConnectOptions option;

    public MqttPushCallback() {

    }

    public MqttPushCallback(MqttClient client, MqttConnectOptions option) {
        this.client = client;
        this.option = option;
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.info("断开连接，建议重连" + this);
        log.error("连接断开",cause);
        while(true) {
            try {
                Thread.sleep(1000);
                // 重新连接
                //client.connect(option);
                if (!client.isConnected()) {
                    client.connect(option);
                    log.info("连接成功");
                }else {
                    //即使连接上也要先断开再重新连接
                    client.disconnect();  //不这样就重连会报错
                    client.connect(option);
                    log.info("连接成功");
                }

                break;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //log.info(token.isComplete() + "");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            log.info("Topic: " + topic);
            log.info("Message: " + new String(message.getPayload()));
            if("/device/push/result".equals(topic)){ //臻识的摄像头
                CarMachineProcessFactory.getCarImpl("17").mqttMessageArrived(topic, new String(message.getPayload()));
            }else {
                AccessControlProcessFactory.getAssessControlProcessImpl(getHmId(topic, message)).mqttMessageArrived(topic, new String(message.getPayload()));
            }
        } catch (Exception e) {
            log.error("处理订阅消息失败", e);
        }
    }

    /**
     * 获取协议
     *
     * @param topic
     * @param message
     * @return
     */
    private String getHmId(String topic, MqttMessage message) {

        //德安中获取
        String hmId = getHmIdByDean(topic);
        if (!StringUtil.isEmpty(hmId)) {
            return hmId;
        }

        //伊兰度中获取协议
        hmId = getHmIdByYld(topic, message);




        return hmId;

    }

    /**
     * 伊兰度中获取设备ID
     *
     * @param topic   face.{sn}.response
     * @param message
     * @return
     */
    private String getHmIdByYld(String topic, MqttMessage message) {
        String msg = new String(message.getPayload());

        if(!Assert.isJsonObject(msg)){
            return "";
        }

        String hmId = "";

        JSONObject msgObj = JSONObject.parseObject(msg);
        String machineCode = "";
        if(msgObj.containsKey("sn")){
            machineCode = msgObj.getString("sn");
        }

        if(msgObj.containsKey("body")){
            machineCode = msgObj.getJSONObject("body").getString("sn");
        }

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        try {
            List<MachineDto> machineDtos = machineService.queryMachines(machineDto);

            Assert.listOnlyOne(machineDtos, "未找到设备信息");

            hmId = machineDtos.get(0).getHmId();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return hmId;
    }

    /**
     * 德安门禁获取
     *
     * @param topic mqtt/face/ID/Rec mqtt/face/ID/Snap mqtt/face/ID/QRCode mqtt/face/ID/Ack
     * @return
     */
    private String getHmIdByDean(String topic) {
        String hmId = "";
        //判断是否为德安心跳
        if ("mqtt/face/heartbeat".equals(topic)) {
            return "6";//德安协议 非常抱歉 topic中不带设备编号 神也没办法用设备编码去查协议了，只能写死了 尴尬的一B
        }
        if (!topic.endsWith("Rec") && !topic.endsWith("Snap") && !topic.endsWith("QRCode") && !topic.endsWith("Ack")) {
            return hmId;
        }

        if (topic.length() < 10) {
            return hmId;
        }

        String machineCode = topic.substring(10, topic.lastIndexOf("/"));

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        try {
            List<MachineDto> machineDtos = machineService.queryMachines(machineDto);

            Assert.listOnlyOne(machineDtos, "未找到设备信息");

            hmId = machineDtos.get(0).getHmId();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return hmId;
    }

}