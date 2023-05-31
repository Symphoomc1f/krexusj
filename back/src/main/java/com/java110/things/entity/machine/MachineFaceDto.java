package com.java110.things.entity.machine;

import com.java110.things.entity.accessControl.UserFaceDto;

import java.io.Serializable;

/**
 * @ClassName MachineDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 23:33
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class MachineFaceDto extends UserFaceDto implements Serializable {

    private String statusCd;

    private String createTime;

    private String machineTypeCd;

    private String taskId;

    private String id;
    private String state;
    private String message;

    private String communityId;

    private String cardId;

    private String cardNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMachineTypeCd() {
        return machineTypeCd;
    }

    public void setMachineTypeCd(String machineTypeCd) {
        this.machineTypeCd = machineTypeCd;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
