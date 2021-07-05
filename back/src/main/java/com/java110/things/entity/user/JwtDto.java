package com.java110.things.entity.user;

/**
 * @ClassName JwtDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 21:43
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class JwtDto {

    private String jdi;

    private String userId;

    private long expireTime;


    public String getJdi() {
        return jdi;
    }

    public void setJdi(String jdi) {
        this.jdi = jdi;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
