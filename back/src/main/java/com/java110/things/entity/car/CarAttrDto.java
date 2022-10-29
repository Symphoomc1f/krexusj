package com.java110.things.entity.car;

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
public class CarAttrDto extends PageDto implements Serializable {

    public static final String SPEC_ZHEN_SHI_ID = "2029302";

    private String carId;
    private String communityId;
    private String attrId;
    private String specCd;
    private String value;
    private String statusCd;


    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
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
