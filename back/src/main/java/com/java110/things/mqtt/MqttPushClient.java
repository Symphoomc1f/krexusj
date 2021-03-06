package com.java110.things.mqtt;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName MqttPushClient
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 16:37
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class MqttPushClient {

    private static final Logger log = LoggerFactory.getLogger(MqttPushClient.class);
    public static String MQTT_HOST = "";
    public static String MQTT_CLIENTID = "";
    public static String MQTT_USERNAME = "";
    public static String MQTT_PASSWORD = "";
    public static int MQTT_TIMEOUT = 10;
    public static int MQTT_KEEPALIVE = 10;

    private MqttClient client;
    private static volatile MqttPushClient mqttClient = null;

    public static MqttPushClient getInstance() {
        if (mqttClient == null) {
            synchronized (MqttPushClient.class) {
                if (mqttClient == null) {
                    mqttClient = new MqttPushClient();
                }
            }
        }
        return mqttClient;
    }

    private MqttPushClient() {
        log.info("Connect MQTT: " + this);
        connect();
    }

    private void connect() {
        try {
            client = new MqttClient(MQTT_HOST, MQTT_CLIENTID, new MemoryPersistence());
            MqttConnectOptions option = new MqttConnectOptions();
            option.setCleanSession(true);
            option.setUserName(MQTT_USERNAME);
            option.setPassword(MQTT_PASSWORD.toCharArray());
            option.setConnectionTimeout(MQTT_TIMEOUT);
            option.setKeepAliveInterval(MQTT_KEEPALIVE);
            option.setAutomaticReconnect(true);
            try {
                client.setCallback(new MqttPushCallback(client,option));
                client.connect(option);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????<br>
     * ??????qos???1 ????????????
     *
     * @param topic
     * @param data
     */
    public void publish(String topic, String data) {
        if(!client.isConnected()){
            connect();
        }
        publish(topic, data, 1, false);
    }

    /**
     * ??????
     *
     * @param topic
     * @param data
     * @param qos
     * @param retained
     */
    public void publish(String topic, String data, int qos, boolean retained) {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(data.getBytes());
        MqttTopic mqttTopic = client.getTopic(topic);
        if (null == mqttTopic) {
            log.error("Topic Not Exist");
        }
        MqttDeliveryToken token;
        try {
            token = mqttTopic.publish(message);
            token.waitForCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????? qos?????????1
     *
     * @param topic
     */
    public void subscribe(String topic) {
        subscribe(topic, 1);
    }

    /**
     * ??????????????????
     *
     * @param topic
     * @param qos
     */
    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(topic, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}