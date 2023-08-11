package com.java110.things.sip;


import com.java110.things.sip.message.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class InitSipServer {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${config.listenIp}")
    private String listenIp;

    @Value("${config.listenPort}")
    private int listenPort;

    @Value("${config.sipId}")
    private String sipId;

    @Value("${config.sipRealm}")
    private String sipRealm;

    @Value("${config.password}")
    private String password;

    @Value("${config.streamMediaIp}")
    private String streamMediaIp;

    /**
     * 初始化 层
     * @return
     */
    @Bean
    public SipLayer sipLayer() {
        SipLayer sipLayer = new SipLayer(sipId, sipRealm, password, listenIp, listenPort, streamMediaIp);
        boolean startStatus = sipLayer.startServer();
        if (startStatus) {
            logger.info("Sip Server 启动成功 port {}", listenPort);
        } else {
            logger.info("Sip Server 启动失败");
        }
        return sipLayer;
    }
}
