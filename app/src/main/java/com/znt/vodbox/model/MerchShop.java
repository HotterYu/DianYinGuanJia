package com.znt.vodbox.model;

import java.io.Serializable;

public class MerchShop implements Serializable{

    private String id = "";
    private String name = "";
    private String shopCode = "";
    private String userShopCode = "";
    private String address = "";
    private String tel = "";
    private String memberId = "";
    private String linkman = "";
    private String linkmanPhone = "";
    private String wifiName = "";
    private String wifiPassword = "";
    private String status = "";
    private String group = "";
    private String tmlRunStatus = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getUserShopCode() {
        return userShopCode;
    }

    public void setUserShopCode(String userShopCode) {
        this.userShopCode = userShopCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTmlRunStatus() {
        return tmlRunStatus;
    }

    public void setTmlRunStatus(String tmlRunStatus) {
        this.tmlRunStatus = tmlRunStatus;
    }
}
