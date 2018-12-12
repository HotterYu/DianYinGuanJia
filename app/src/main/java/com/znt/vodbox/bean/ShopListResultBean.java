package com.znt.vodbox.bean;

import com.znt.vodbox.model.Shopinfo;

import java.io.Serializable;
import java.util.List;

public class ShopListResultBean implements Serializable {

    private String resultcode;
    private String message;

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

    private List<Shopinfo> data;

    private ExtendDataBean extendData;

    public ExtendDataBean getExtendData() {
        return extendData;
    }

    public void setExtendData(ExtendDataBean extendData) {
        this.extendData = extendData;
    }

    public List<Shopinfo> getData() {
        return data;
    }

    public void setData(List<Shopinfo> data) {
        this.data = data;
    }
}
