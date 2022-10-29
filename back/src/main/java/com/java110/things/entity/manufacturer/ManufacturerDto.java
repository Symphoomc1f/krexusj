package com.java110.things.entity.manufacturer;

import com.java110.things.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName ManufacturerDto 硬件厂商
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/18 20:42
 * @Version 1.0
 * add by wuxw 2020/5/18
 **/
public class ManufacturerDto extends PageDto implements Serializable {

    private String hmId;
    private String hmName;
    private String version;
    private String protocolImpl;
    private String description;
    private String createTime;

    private String author;
    private String link;
    private String license;
    private String prodUrl;
    private String defaultProtocol;

    private String hmType;

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getHmName() {
        return hmName;
    }

    public void setHmName(String hmName) {
        this.hmName = hmName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProtocolImpl() {
        return protocolImpl;
    }

    public void setProtocolImpl(String protocolImpl) {
        this.protocolImpl = protocolImpl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHmType() {
        return hmType;
    }

    public void setHmType(String hmType) {
        this.hmType = hmType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public String getDefaultProtocol() {
        return defaultProtocol;
    }

    public void setDefaultProtocol(String defaultProtocol) {
        this.defaultProtocol = defaultProtocol;
    }
}
