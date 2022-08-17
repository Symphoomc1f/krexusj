package com.java110.things.entity.app;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * 应用属性对象
 *
 * @ClassName
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 23:33
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class AppAttrDto extends PageDto implements Serializable {

    //人脸上报 地址
    public static final String SPEC_CD_UPLOAD_FACE_URL = "3001001";
    //异步执行结果上报
    public static final String SPEC_CD_UPLOAD_CMD_URL = "3002001";
    public static final String SPEC_CD_APP_ID = "3003001";
    public static final String SPEC_CD_SECURITY_CODE = "3004001";

    // 设备心跳上报
    public static final String SPEC_CD_UPLOAD_HEARTBEAT = "3005001";
    private String attrId;
    private String appId;
    private String[] appIds;
    private String specCd;
    private String value;
    private String createTime;
    private String statusCd;

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String[] getAppIds() {
        return appIds;
    }

    public void setAppIds(String[] appIds) {
        this.appIds = appIds;
    }
}
