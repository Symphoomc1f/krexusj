package com.java110.things.entity.user;

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

    private String userName;

    private String password;

    private String tel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
