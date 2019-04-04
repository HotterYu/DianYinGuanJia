package com.znt.vodbox.bean;

import com.znt.vodbox.model.Shopinfo;

import java.io.Serializable;

public class ShopInfoCallBackBean implements Serializable {

    private String resultcode;
    private String message;

    public Shopinfo getData() {
        return data;
    }

    public void setData(Shopinfo data) {
        this.data = data;
    }

    private Shopinfo data;


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
