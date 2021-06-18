package com.java110.things.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by wuxw on 2017/7/25.
 */
@Component
@ConfigurationProperties(prefix = "java110")
@PropertySource("classpath:java110.properties")
public class Java110Properties {

    private String communityId;

    private String communityName;

    private String cloudApiUrl;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCloudApiUrl() {
        return cloudApiUrl;
    }

    public void setCloudApiUrl(String cloudApiUrl) {
        this.cloudApiUrl = cloudApiUrl;
    }
}
