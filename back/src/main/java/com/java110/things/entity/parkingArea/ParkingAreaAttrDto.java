package com.java110.things.entity.parkingArea;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

public class ParkingAreaAttrDto extends PageDto implements Serializable {

    public static final String SPEC_EXT_PA_ID = "6185-17861";

    private String paId;
    private String[] paIds;
    private String communityId;
    private String attrId;
    private String specCd;
    private String value;
    private String statusCd;



    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
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

    public String[] getPaIds() {
        return paIds;
    }

    public void setPaIds(String[] paIds) {
        this.paIds = paIds;
    }
}
