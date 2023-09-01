package com.java110.things.entity.parkingArea;


import java.io.Serializable;
import java.util.Date;

/**
 * 缓存
 */
public class ParkingAreaTextCacheDto extends ParkingAreaTextDto implements Serializable {

    /**
     * 过期时间
     */
    public static long EXPIRES = 24 * 60 * 60 * 1000;

    private Date cacheTime;

    public Date getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Date cacheTime) {
        this.cacheTime = cacheTime;
    }
}
