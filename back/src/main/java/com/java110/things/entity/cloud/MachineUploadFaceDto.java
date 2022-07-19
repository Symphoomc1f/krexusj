package com.java110.things.entity.cloud;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName MachineUploadFaceDto
 * @Description TODO 设备人脸上报云端接口协议
 * @Author wuxw
 * @Date 2020/5/27 8:31
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
public class MachineUploadFaceDto implements Serializable {

    private String userId; //用户信息

    private String userName; // 用户名称

    private String machineCode; //设备编码

    private String openTypeCd;//开门方式 1000 人脸开门 2000 钥匙开门

    private String similar;//相似度

    private String photo;//抓拍照片

    private String dateTime;//抓拍时间

    private String communityId;// 小区信息

    private String recordTypeCd; //记录类型，8888 开门记录 6666 访客留影

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getOpenTypeCd() {
        return openTypeCd;
    }

    public void setOpenTypeCd(String openTypeCd) {
        this.openTypeCd = openTypeCd;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getRecordTypeCd() {
        return recordTypeCd;
    }

    public void setRecordTypeCd(String recordTypeCd) {
        this.recordTypeCd = recordTypeCd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
