package com.znt.vodbox.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by prize on 2018/11/7.
 */
public class LoginRecordInfo implements Serializable
{

    public int id;

    private String nickName;

    private String account = "";

    private String pwd;

    public LoginRecordInfo() {
    }

    public LoginRecordInfo(Integer id, String nickName, String account, String pwd) {
        this.id = id;
        this.nickName = nickName;
        this.account = account;
        this.pwd = pwd;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LoginRecordInfo)) {
            return false;
        }
        LoginRecordInfo mLoginRecordInfo = (LoginRecordInfo) o;
        if (!TextUtils.isEmpty(mLoginRecordInfo.getAccount())
                && mLoginRecordInfo.getAccount().equals(this.account)) {
            return true;
        }

        return false;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
