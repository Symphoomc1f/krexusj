package com.java110.things.entity.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.TempCarFeeFactory;
import com.java110.things.util.DateUtil;

import java.io.Serializable;

public class BarrierGateControlDto implements Serializable {

    /**
     * 进出
     */
    public static final String ACTION_INOUT = "IN_OUT";
    public static final String ACTION_FEE_INFO = "FEE_INFO";

    private String action;
    private String carNum;
    private String inOutTime;
    private String img;
    private String machineId;
    private String extMachineId;
    private Double payCharge;
    private long hours;
    private long min;
    private String remark;
    private String open;
    private String extCommunityId;
    private String extPaId;
    private String extBoxId;

    public BarrierGateControlDto() {
    }

    public BarrierGateControlDto(String action, String carNum, String inOutTime, String img) {
        this.action = action;
        this.carNum = carNum;
        this.inOutTime = inOutTime;
        this.img = img;
    }

    public BarrierGateControlDto(String action, String carNum, MachineDto machineDto) {
        this.action = action;
        this.carNum = carNum;
        this.inOutTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_L);
        this.img = machineDto.getPhotoJpg();
        this.machineId = machineDto.getMachineId();
    }

    public BarrierGateControlDto(String action, String carNum, MachineDto machineDto, String remark, String open) {
        this.action = action;
        this.carNum = carNum;
        this.inOutTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_L);
        this.img = machineDto.getPhotoJpg();
        this.machineId = machineDto.getMachineId();
        this.payCharge = 0.0;
        this.hours = 0;
        this.min = 0;
        this.remark = remark;
        this.open = open;
    }

    public BarrierGateControlDto(String action, String carNum, MachineDto machineDto, double payCharge, CarInoutDto carInoutDto, String remark, String open) {
        this.action = action;
        this.carNum = carNum;
        this.inOutTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_L);
        this.img = machineDto.getPhotoJpg();
        this.machineId = machineDto.getMachineId();
        this.payCharge = payCharge;
        this.open = open;
        if (carInoutDto == null) {
            this.hours = 0;
            this.min = 0;
        } else {
            //获取停车时间
            long min = TempCarFeeFactory.getTempCarMin(carInoutDto);
            long hours = min / 60; //因为两者都是整数，你得到一个int
            long minutes = min % 60;
            this.hours = hours;
            this.min = minutes;
        }

        this.remark = remark;
    }


    public BarrierGateControlDto(String action, String carNum, String img) {
        this.action = action;
        this.carNum = carNum;
        this.inOutTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_L);
        this.img = img;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getInOutTime() {
        return inOutTime;
    }

    public void setInOutTime(String inOutTime) {
        this.inOutTime = inOutTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public Double getPayCharge() {
        return payCharge;
    }

    public void setPayCharge(Double payCharge) {
        this.payCharge = payCharge;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getExtMachineId() {
        return extMachineId;
    }

    public void setExtMachineId(String extMachineId) {
        this.extMachineId = extMachineId;
    }

    public String getExtCommunityId() {
        return extCommunityId;
    }

    public void setExtCommunityId(String extCommunityId) {
        this.extCommunityId = extCommunityId;
    }

    public String getExtPaId() {
        return extPaId;
    }

    public void setExtPaId(String extPaId) {
        this.extPaId = extPaId;
    }

    public String getExtBoxId() {
        return extBoxId;
    }

    public void setExtBoxId(String extBoxId) {
        this.extBoxId = extBoxId;
    }
}
