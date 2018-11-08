package com.znt.vodbox.model;

import java.io.Serializable;

public class UserInfo implements Serializable
{

    private String id = "";
    private String username = "";
    private String phone = "";
    private String email = "";
    private String nickName = "";
    private String token = "";
    private String memberHead = "";
    private String isDefault = "";//是否是客户默认管理员1-是 0否
    private com.znt.vodbox.model.merchant merchant = null;//客户信息对象
    private String type = "";//0-系统管理管理员，1-客户总管理员，2-客户分区管理员  3-店长
    private String lastLoginIp = "";//
    private String lastLoginTime = "";//
    private String pwd = "";
    public String getPwd()
    {
        return pwd;
    }
    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMemberHead() {
        return memberHead;
    }

    public void setMemberHead(String memberHead) {
        this.memberHead = memberHead;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public merchant getMerchant() {
        if(merchant == null)
            merchant = new merchant();
        return merchant;
    }

    public void setMerchant(merchant mmerchant) {
        this.merchant = mmerchant;
    }

    public String getType() {
        return type;
    }
    public String getTypeName() {

        if(type.equals("0"))
        {
            return "系统管理员";
        }
        else if(type.equals("1"))
        {
            return "总管理员";
        }
        else if(type.equals("2"))
        {
            return "分区管理员";
        }
        else if(type.equals("3"))
        {
            return "店长";
        }

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
