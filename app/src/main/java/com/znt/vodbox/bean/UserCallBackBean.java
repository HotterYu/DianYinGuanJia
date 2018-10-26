package com.znt.vodbox.bean;

import com.znt.vodbox.model.UserInfo;

import java.io.Serializable;

public class UserCallBackBean implements Serializable {

    private String resultcode;
    private String message;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    private UserInfo data;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

}
