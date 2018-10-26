package com.znt.vodbox.model;

import com.znt.vodbox.bean.GroupInfo;

import java.io.Serializable;
import java.util.List;

public class Shopinfo implements Serializable
{

    private String id = "";
    private String addTime = "";
    private String address = "";
    private String allowNum = "";
    private String bindNumber = "";
    private String city = "";
    private String country = "";
    private String geohash = "";
    private String latitude = "";
    private String longitude = "";
    private String memberId = "";
    private String merchId = "";
    private String name = "";
    private String province = "";
    private String region = "";
    private String shopCode = "";
    private String userShopCode = "";
    private String tel = "";
    private String wifiName = "";
    private String wifiPassword = "";
    private String linkman = "";
    private String linkmanPhone = "";
    private String status = "";
    private GroupInfo group ;
    private List<tmlRunStatus> tmlRunStatus = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAllowNum() {
        return allowNum;
    }

    public void setAllowNum(String allowNum) {
        this.allowNum = allowNum;
    }

    public String getBindNumber() {
        return bindNumber;
    }

    public void setBindNumber(String bindNumber) {
        this.bindNumber = bindNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }

    public List<com.znt.vodbox.model.tmlRunStatus> getTmlRunStatus() {
        return tmlRunStatus;
    }

    public void setTmlRunStatus(List<com.znt.vodbox.model.tmlRunStatus> tmlRunStatus) {
        this.tmlRunStatus = tmlRunStatus;
    }
}
