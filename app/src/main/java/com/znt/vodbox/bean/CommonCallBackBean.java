package com.znt.vodbox.bean;

import java.io.Serializable;

public class CommonCallBackBean implements Serializable {

    private String resultcode;
    private String message;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;


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

    public boolean isSuccess()
    {
        return resultcode.equals("1");
    }

}
