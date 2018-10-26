package com.znt.vodbox.bean;

import com.google.gson.Gson;
import com.znt.vodbox.model.Shopinfo;

import java.io.Serializable;
import java.util.List;

public class AlbumListResultBean implements Serializable {

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

    private List<AlbumInfo> data;

    public List<AlbumInfo> getData() {

        return data;
    }


}
