package com.java110.things.entity.user;

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
public class CommunityPersonDto extends PageDto implements Serializable {
    private String personId;
    private String communityId;
    private String name;
    private String tel;
    private String idNumber;
    private String personType;
    private String facePath;
    private String extPersonId;
    private String createTime;
    private String personTypeName;
    private String statusCd = "0";

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getExtPersonId() {
        return extPersonId;
    }

    public void setExtPersonId(String extPersonId) {
        this.extPersonId = extPersonId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getPersonTypeName() {
        return personTypeName;
    }

    public void setPersonTypeName(String personTypeName) {
        this.personTypeName = personTypeName;
    }
}
