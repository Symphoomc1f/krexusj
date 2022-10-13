package com.java110.things.entity.car;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * 临时车费标准实体类
 */
public class TempCarFeeConfigAttrDto extends PageDto implements Serializable {

    private String configId;
    private String[] configIds;
    private String communityId;
    private String attrId;
    private String specCd;
    private String value;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
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

    public String[] getConfigIds() {
        return configIds;
    }

    public void setConfigIds(String[] configIds) {
        this.configIds = configIds;
    }
}
