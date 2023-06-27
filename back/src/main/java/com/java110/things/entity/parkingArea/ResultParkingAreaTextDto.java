package com.java110.things.entity.parkingArea;

import java.io.Serializable;

/**
 * 结果集
 */
public class ResultParkingAreaTextDto extends ParkingAreaTextDto implements Serializable {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -1;
    private int code;

    public ResultParkingAreaTextDto() {
    }


    public ResultParkingAreaTextDto(int code, String text1, String text2, String text3, String text4, String voice) {
        this.code = code;
        this.setText1(text1);
        this.setText2(text2);
        this.setText3(text3);
        this.setText4(text4);
        this.setVoice(voice);
    }

    public ResultParkingAreaTextDto(int code, String text1) {
        this.code = code;
        this.setText1(text1);
        this.setText2("");
        this.setText3("");
        this.setText4("");
        this.setVoice("");
    }

    public ResultParkingAreaTextDto(int code, ParkingAreaTextCacheDto parkingAreaTextCacheDto) {
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
}
