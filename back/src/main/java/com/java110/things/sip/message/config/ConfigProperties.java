package com.java110.things.sip.message.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sip")
//@PropertySource("classpath:config.properties")
public class ConfigProperties {
    @Value("${sip.listenIp}")
    private String listenIp;
    @Value("${sip.listenPort}")
    private int listenPort;
    @Value("${sip.sipId}")
    private String sipId;
    @Value("${sip.sipRealm}")
    private String sipRealm;
    @Value("${sip.password}")
    private String password;
    @Value("${sip.streamMediaIp}")
    private String streamMediaIp;
    @Value("${sip.pullRtmpAddress}")
    private String pullRtmpAddress;
    @Value("${sip.pushRtmpAddress}")
    private String pushRtmpAddress;
    @Value("${sip.checkSsrc}")
    private boolean checkSsrc;

    public String getListenIp() {
        return listenIp;
    }

    public void setListenIp(String listenIp) {
        this.listenIp = listenIp;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getSipId() {
        return sipId;
    }

    public void setSipId(String sipId) {
        this.sipId = sipId;
    }

    public String getSipRealm() {
        return sipRealm;
    }

    public void setSipRealm(String sipRealm) {
        this.sipRealm = sipRealm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStreamMediaIp() {
        return streamMediaIp;
    }

    public void setStreamMediaIp(String streamMediaIp) {
        this.streamMediaIp = streamMediaIp;
    }

    public String getPullRtmpAddress() {
        return pullRtmpAddress;
    }

    public void setPullRtmpAddress(String pullRtmpAddress) {
        this.pullRtmpAddress = pullRtmpAddress;
    }

    public String getPushRtmpAddress() {
        return pushRtmpAddress;
    }

    public void setPushRtmpAddress(String pushRtmpAddress) {
        this.pushRtmpAddress = pushRtmpAddress;
    }

    public boolean isCheckSsrc() {
        return checkSsrc;
    }

    public void setCheckSsrc(boolean checkSsrc) {
        this.checkSsrc = checkSsrc;
    }

}
