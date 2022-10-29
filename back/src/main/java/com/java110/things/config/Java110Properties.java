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

    private static String cloudFacePath;


    public static String getCloudFacePath() {
        return cloudFacePath;
    }

    public void setCloudFacePath(String cloudFacePath) {
        this.cloudFacePath = cloudFacePath;
    }
}
