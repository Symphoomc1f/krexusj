package com.java110.things.entity.parkingArea;

import java.io.Serializable;

/**
 * 结果集
 */
public class ResultParkingAreaTextDto extends ParkingAreaTextDto implements Serializable {
    public static final int CODE_CAR_IN_SUCCESS = 0; // carNum, "欢迎光临", "", "", carNum + ",欢迎光临"
    public static final int CODE_MONTH_CAR_SUCCESS = 1; // "月租车," + carNum + ",欢迎光临", "开门成功"
    public static final int CODE_TEMP_CAR_SUCCESS = 2; //"临时车," + carNum + ",欢迎光临", "开门成功"
    public static final int CODE_FREE_CAR_OUT_SUCCESS = 3; //"免费车辆", "", "", carNum + ",免费车辆"
    public static final int CODE_MONTH_CAR_OUT_SUCCESS = 4; // "月租车," + carNum + ",欢迎光临", "开门成功"
    public static final int CODE_TEMP_CAR_OUT_SUCCESS = 5; //"临时车," + carNum + ",欢迎光临", "开门成功"
    public static final int CODE_CAR_OUT_SUCCESS = 6; // "车未入场", "", "", carNum + ",车未入场"
    //carNum + ",停车" + result.getHours() + "时" + result.getMin() + "分,请交费" + result.getPayCharge() + "元"
    public static final int CODE_CAR_BLACK = 101; // 黑名单 "此车为黑名单车辆", carNum + ",禁止通行", "", "", "此车为黑名单车辆," + carNum + ",禁止通行"
    public static final int CODE_CAR_INED = 102; // 车辆已在场 "车已在场", "", "", carNum + ",车已在场"
    public static final int CODE_CAR_IN_ERROR = 103; // 车辆入场失败  "禁止入场", "", "", carNum + ",禁止入场"
    public static final int CODE_CAR_NO_IN = 104; // "车未入场", "", "", carNum + ",车未入场"
    public static final int CODE_CAR_NO_PRI = 105; // "车未入场", "", "", carNum + ",车未入场"
    public static final int CODE_CAR_OUT_ERROR = 106; // "停车" + result.getHours() + "时" + result.getMin() + "分", "请交费" + result.getPayCharge() + "元", "", "",

    private int code;

    private String carNum;

    private int day;

    private long hours;

    private long min;

    private double payCharge;

    public ResultParkingAreaTextDto() {
    }


    public ResultParkingAreaTextDto(int code, String text1, String text2, String text3, String text4, String voice, String carNum) {
        this.code = code;
        this.setText1(text1);
        this.setText2(text2);
        this.setText3(text3);
        this.setText4(text4);
        this.setVoice(voice);
        this.setCarNum(carNum);
    }

    public ResultParkingAreaTextDto(int code, String text1) {
        this.code = code;
        this.setText1(text1);
        this.setText2("");
        this.setText3("");
        this.setText4("");
        this.setVoice("");
    }

    public ResultParkingAreaTextDto(int code, ParkingAreaTextCacheDto parkingAreaTextCacheDto,String carNum) {
        this.code = code;
        this.setText1(parkingAreaTextCacheDto.getText1());
        this.setText2(parkingAreaTextCacheDto.getText2());
        this.setText3(parkingAreaTextCacheDto.getText3());
        this.setText4(parkingAreaTextCacheDto.getText4());
        this.setVoice(parkingAreaTextCacheDto.getVoice());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public double getPayCharge() {
        return payCharge;
    }

    public void setPayCharge(double payCharge) {
        this.payCharge = payCharge;
    }
}
