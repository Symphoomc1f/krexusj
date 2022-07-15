package com.java110.things.entity.app;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.List;

/**
 * 应用对象
 *
 * @ClassName
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 23:33
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class AppDto extends PageDto implements Serializable {
    private String appId;
    private String appSecret;
    private String appName;
    private String accessToken;
    private String updateTime;
    private String createTime;

    List<AppAttrDto> appAttrs;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<AppAttrDto> getAppAttrs() {
        return appAttrs;
    }

    public void setAppAttrs(List<AppAttrDto> appAttrs) {
        this.appAttrs = appAttrs;
    }
}
