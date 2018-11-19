package com.znt.vodbox.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanResultBean implements Serializable {

    private String resultcode;
    private String message;

    public boolean isSuccess()
    {
        return resultcode.equals("1");
    }

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

    private PlanInfo data;

    public PlanInfo getData() {

        return data;
    }


    private List<MediaInfo> mediaInfos = new ArrayList<>();
    public void setMediaList(List<MediaInfo> mediaInfos)
    {
        this.mediaInfos = mediaInfos;
    }
    public List<MediaInfo> getMediaInfos()
    {
        return mediaInfos;
    }


}
