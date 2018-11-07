package com.znt.vodbox.bean;

import com.znt.vodbox.model.UserInfo;

import java.io.Serializable;
import java.util.List;

public class UserListCallBackBean implements Serializable {

    private String resultcode;
    private String message;

    public boolean isSuccess()
    {
        return resultcode.equals("1");
    }

    public List<UserInfo> getData() {
        return data;
    }

    public void setData(List<UserInfo> data) {
        this.data = data;
    }

    private List<UserInfo> data;


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
