package com.java110.things.entity.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName UserDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 20:56
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class UserDto extends PageDto implements Serializable {
    private String id;

    private String userId;

    private String username;

    private String password;

    private String tel;

    private String levelCd;

    private String statusCd;

    private String address;

    private String email;

    private int age;

    private int sex;

    private String createTime;

    private String appId;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getLevelCd() {
        return levelCd;
    }

    public void setLevelCd(String levelCd) {
        this.levelCd = levelCd;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
