package com.java110.things.entity.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName MachineAttrDto
 * @Description TODO 设备属性表，存储设备相关信息
 * @Author wuxw
 * @Date 2020/5/10 23:33
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class MachineAttrDto extends PageDto implements Serializable {

    public static final String SPEC_VEDIO_URL = "19089001";

    private String machineId;
    private String communityId;
    private String attrId;
    private String specCd;
    private String value;
    private String statusCd;


    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
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

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
