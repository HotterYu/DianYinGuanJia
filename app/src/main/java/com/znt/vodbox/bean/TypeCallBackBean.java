package com.znt.vodbox.bean;

import java.io.Serializable;
import java.util.List;

public class TypeCallBackBean implements Serializable {

    private String resultcode;
    private String message;

    public boolean isSuccess()
    {
        return resultcode.equals("1");
    }

    public List<TypeInfo> getData() {
        return data;
    }

    public void setData(List<TypeInfo> data) {
        this.data = data;
    }

    private List<TypeInfo> data;


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
