package com.java110.things.entity.user;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName UserDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 20:56
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class UserDto implements Serializable {
    private String userId;

    private String username;

    private String password;

    private String tel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
