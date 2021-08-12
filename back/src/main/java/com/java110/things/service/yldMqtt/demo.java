package com.java110.things.service.yldMqtt;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.mqtt.MqttPushClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.util.Date;

/**
 * @ClassName demo
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 16:22
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class demo {

    public static void main(String[] args) {


        //createFace();
        //gpioControl();
        searchMachine();
        //reboot();

        //setUiTitle();
    }


    /**
     * 创建人脸
     */
    public static void createFace() {

        MqttPushClient.MQTT_HOST = "tcp://192.168.1.4:1883";
        MqttPushClient.MQTT_CLIENTID = "hc_mqttId_inbound";
        MqttPushClient.MQTT_USERNAME = "admin";
        MqttPushClient.MQTT_PASSWORD = "admin";
        MqttPushClient client = MqttPushClient.getInstance();
        client.subscribe("face.9f15b229-0422fd6b.response");
        JSONObject param = new JSONObject();
        param.put("client_id", "9f15b229-0422fd6b");
        param.put("cmd_id", "fffffffff");
        param.put("version", "0.2");
        param.put("cmd", "create_face");
        param.put("per_id", "123456");
        param.put("face_id", "12313");
        param.put("per_name", "傻逼");
        param.put("idcardNum", "123123");
        param.put("img_url", "http://39.98.253.100/20200520021223248.jpg");
        param.put("idcardper", "632126199109162011");
        param.put("s_time", new Date().getTime() - 1000 * 60 * 60);
        param.put("e_time", new Date().getTime() + 1000 * 60 * 60 * 24);
        param.put("per_type", 0);
        param.put("usr_type", 0);
        param.put("cmd", "create_face");

        client.publish("face.9f15b229-0422fd6b.request", param.toJSONString());

    }

    /**
     * 重启
     */
    public static void gpioControl() {

        MqttPushClient.MQTT_HOST = "tcp://192.168.1.4:1883";
        MqttPushClient.MQTT_CLIENTID = "hc_mqttId_inbound";
        MqttPushClient.MQTT_USERNAME = "admin";
        MqttPushClient.MQTT_PASSWORD = "admin";
        MqttPushClient client = MqttPushClient.getInstance();
        client.subscribe("face.response");
        JSONObject param = new JSONObject();
        param.put("client_id", "9f15b229-0422fd6b");
        param.put("cmd_id", "fffffffff");
        param.put("version", "0.2");
        param.put("cmd", "gpio control");
        param.put("ctrl_type", "on");
        client.publish("face.9f15b229-0422fd6b.request", param.toJSONString());

    }

    /**
     * 重启
     */
    public static void reboot() {

        MqttPushClient.MQTT_HOST = "tcp://192.168.1.4:1883";
        MqttPushClient.MQTT_CLIENTID = "hc_mqttId_inbound";
        MqttPushClient.MQTT_USERNAME = "admin";
        MqttPushClient.MQTT_PASSWORD = "admin";
        MqttPushClient client = MqttPushClient.getInstance();
        client.subscribe("face.response");
        client.subscribe("online.response");
        JSONObject param = new JSONObject();
        param.put("client_id", "9f15b229-0422fd6b");
        param.put("cmd_id", "fffffffff");
        param.put("version", "0.2");
        param.put("cmd", "reboot_cam");
        client.publish("face.9f15b229-0422fd6b.request", param.toJSONString());

    }

    /**
     * 搜索
     */
    public static void searchMachine() {

        MqttPushClient.MQTT_HOST = "tcp://192.168.1.4:1883";
        MqttPushClient.MQTT_CLIENTID = "hc_mqttId_inbound";
        MqttPushClient.MQTT_USERNAME = "admin";
        MqttPushClient.MQTT_PASSWORD = "admin";
        MqttPushClient client = MqttPushClient.getInstance();
        //client.subscribe("face.9f15b229-0422fd6b.response");

        JSONObject param = new JSONObject();
        param.put("client_id", "9f15b229-0422fd6b");
        param.put("cmd_id", "fffffffff");
        param.put("version", "0.2");
        param.put("cmd", "face_search");
        client.publish("face.request", param.toJSONString());

    }

    /**
     * 重启
     */
    public static void setUiTitle() {

        MqttPushClient.MQTT_HOST = "tcp://192.168.1.4:1883";
        MqttPushClient.MQTT_CLIENTID = "hc_mqttId_inbound";
        MqttPushClient.MQTT_USERNAME = "admin";
        MqttPushClient.MQTT_PASSWORD = "admin";
        MqttPushClient client = MqttPushClient.getInstance();
        client.subscribe("face.response");
        client.subscribe("online.response");
        JSONObject param = new JSONObject();
        param.put("client_id", "9f15b229-0422fd6b");
        param.put("cmd_id", "fffffffff");
        param.put("version", "0.2");
        param.put("cmd", "set_ui_title");
        JSONObject body = new JSONObject();
        body.put("title", "HC-" + "9f15b229-0422fd6b");
        param.put("body", body);
        client.publish("face.9f15b229-0422fd6b.request", param.toJSONString());

    }
}
