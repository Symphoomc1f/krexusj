package com.java110.things.mqtt;


import com.java110.things.factory.AccessControlProcessFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            AccessControlProcessFactory.getAssessControlProcessImpl("").mqttMessageArrived(topic, new String(message.getPayload()));
        } catch (Exception e) {
            log.error("处理订阅消息失败", e);
        }
    }

}