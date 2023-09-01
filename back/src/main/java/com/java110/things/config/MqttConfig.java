package com.java110.things.config;

import com.java110.things.mqtt.MqttPushCallback;
import com.java110.things.mqtt.MqttPushClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * @ClassName MqttReceiveConfig
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 16:10
 * @Version 1.0
 *
 @IntegrationComponentScan
 * add by wuxw 2020/5/20
 **/
@Configuration
public class MqttConfig {

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("${spring.mqtt.client.id}")
    private String clientId;

    @Value("${spring.mqtt.default.topic}")
    private String defaultTopic;

    @Value("${spring.mqtt.completionTimeout}")
    private int completionTimeout;   //连接超时

    @Value("${spring.mqtt.keepalive}")
    private int keepalive;


    @Bean
    public MqttClient mqttClient() {
        MqttClient client = null;
        try {
            client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
            MqttConnectOptions option = new MqttConnectOptions();
            option.setCleanSession(false);
            option.setUserName(username);
            option.setPassword(password.toCharArray());
            option.setConnectionTimeout(completionTimeout);
            option.setKeepAliveInterval(keepalive);
            option.setAutomaticReconnect(true);

            client.setCallback(new MqttPushCallback(client,option));
            client.connect(option);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }


}
