package com.znt.vodbox.bean;

import java.io.Serializable;
import java.util.List;

public class GourpListResultBean implements Serializable {

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

    private List<GroupInfo> data;

    public List<GroupInfo> getData() {

        return data;
    }


}
