package com.java110.things.entity.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.PageDto;
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

    private String id;

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
}
