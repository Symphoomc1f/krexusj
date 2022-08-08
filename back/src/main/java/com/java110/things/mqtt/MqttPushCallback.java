package com.java110.things.mqtt;


import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.StringUtil;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
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

    @Override
    public void connectionLost(Throwable cause) {
        log.info("断开连接，建议重连" + this);
//        try {
//            //断开连接，建议重连
//            AccessControlProcessFactory.getAssessControlProcessImpl().initAssessControlProcess();
//        } catch (Exception e) {
//            log.error("初始化失败", e);
//        }
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
            AccessControlProcessFactory.getAssessControlProcessImpl(getHmId(topic, message)).mqttMessageArrived(topic, new String(message.getPayload()));
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
        String hmId = "";
        if (!topic.startsWith("face.") || !topic.endsWith(".response")) {
            return hmId;
        }
        if (topic.length() < 5) {
            return hmId;
        }

        String machineCode = topic.substring(5, topic.lastIndexOf("."));

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