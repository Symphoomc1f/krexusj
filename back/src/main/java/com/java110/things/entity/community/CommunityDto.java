package com.java110.things.entity.community;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName CommunityDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/18 8:43
 * @Version 1.0
 * add by wuxw 2020/5/18
 **/
public class CommunityDto extends PageDto implements Serializable {

    private String communityId;

    private String taskId;

    private String name;

    private String address;

    private String cityCode;

    private String createTime;

    private String areaName;

    private String cityName;

    private String provName;

    private String statusCd;
    private String extCommunityId;
    private String thirdCommunityId;


    private String appId;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public String getExtCommunityId() {
        return extCommunityId;
    }

    public void setExtCommunityId(String extCommunityId) {
        this.extCommunityId = extCommunityId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getThirdCommunityId() {
        return thirdCommunityId;
    }

    public void setThirdCommunityId(String thirdCommunityId) {
        this.thirdCommunityId = thirdCommunityId;
    }

}
