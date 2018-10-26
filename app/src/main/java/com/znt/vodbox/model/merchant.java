package com.znt.vodbox.model;

import java.io.Serializable;

public class merchant implements Serializable{

    private String id = "";
    private String activeMonth = "";
    private String address = "";
    private String createTime = "";
    private String flag = "";
    private String industry = "";
    private String introduction = "";
    private String latitude = "";
    private String linkman = "";
    private String linkmanPhone = "";
    private String linkmanTel = "";
    private String longitude = "";
    private String name = "";
    private String bindCode = "";
    private String shopcodeFlag = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActiveMonth() {
        return activeMonth;
    }

    public void setActiveMonth(String activeMonth) {
        this.activeMonth = activeMonth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getLinkmanPhone() {
        return linkmanPhone;
    }

    public void setLinkmanPhone(String linkmanPhone) {
        this.linkmanPhone = linkmanPhone;
    }

    public String getLinkmanTel() {
        return linkmanTel;
    }

    public void setLinkmanTel(String linkmanTel) {
        this.linkmanTel = linkmanTel;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBindCode() {
        return bindCode;
    }

    public void setBindCode(String bindCode) {
        this.bindCode = bindCode;
    }

    public String getShopcodeFlag() {
        return shopcodeFlag;
    }

    public void setShopcodeFlag(String shopcodeFlag) {
        this.shopcodeFlag = shopcodeFlag;
    }
}
