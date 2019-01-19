package com.znt.vodbox.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubAdPlanInfo implements Serializable {

    private String cycleType;
    private String startTime;
    private String endTime;
    private String musicNum;
    private String adinfoId;
    private String adinfoName;
    private String adinfoUrl;
    private String id = "";
    private String playModel = "1";//时段的playModel=1标识 间隔musicNum首歌插播一次；=2标识定时插播

    public String getPlayModel() {
        return playModel;
    }

    public void setPlayModel(String playModel) {
        this.playModel = playModel;
    }


    public boolean isTimePushModel()
    {
        return playModel.equals("2")  || (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) &&  startTime.equals(endTime) );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdinfoUrl() {
        return adinfoUrl;
    }

    public void setAdinfoUrl(String adinfoUrl) {
        this.adinfoUrl = adinfoUrl;
    }

    public String getAdinfoName() {
        return adinfoName;
    }

    public void setAdinfoName(String adinfoName) {
        this.adinfoName = adinfoName;
    }

    public String getPlanTime()
    {
        return getStartTime() + " ~ " + getEndTime();
    }

    public String getCycleType() {
        return cycleType;
    }

    public void setCycleType(String cycleType) {
        this.cycleType = cycleType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMusicNum() {
        return musicNum;
    }

    public void setMusicNum(String musicNum) {
        this.musicNum = musicNum;
    }

    public String getAdinfoId() {
        return adinfoId;
    }

    public void setAdinfoId(String adinfoId) {
        this.adinfoId = adinfoId;
    }

    private List<AdMediaInfo> adMediaInfoList = new ArrayList<>();
    public void setSelectedAdList(List<AdMediaInfo> adMediaInfoList)
    {
        this.adMediaInfoList = adMediaInfoList;
        getPlanAdNames();
        getPlanAdIds();
        getPlanAdUrls();
    }

    public List<AdMediaInfo> getAdMediaInfoList()
    {
        String[] planAlbumNameStr = adinfoName.split(";");
        String[] planAlbumIdStr = adinfoId.split(";");
        String[] adinfoUrlStr = adinfoUrl.split(";");
        List<String> names = Arrays.asList(planAlbumNameStr);
        List<String> ids = Arrays.asList(planAlbumIdStr);
        List<String> urls = Arrays.asList(adinfoUrlStr);
        adMediaInfoList.clear();

        for (int i=0;i<names.size();i++)
        {
            AdMediaInfo tempInfo = new AdMediaInfo();
            String name = names.get(i);
            String id = ids.get(i);
            String url = URLDecoder.decode(urls.get(i));
            tempInfo.setAdname(name);
            tempInfo.setId(id);
            tempInfo.setUrl(url);
            adMediaInfoList.add(tempInfo);
        }

        return adMediaInfoList;
    }
    public void addAdInfor(AdMediaInfo infor)
    {
        adMediaInfoList.add(infor);
    }

    public String getPlanAdNames()
    {
        int size = adMediaInfoList.size();
        String tag = ";";
        adinfoName = "";
        for(int i=0;i<size;i++)
        {
            AdMediaInfo tempInfor = adMediaInfoList.get(i);
            if(i < size - 1)
                adinfoName += tempInfor.getAdname() + tag;
            else
                adinfoName += tempInfor.getAdname();
        }
        return adinfoName;
    }
    public String getPlanAdIds()
    {
        int size = adMediaInfoList.size();
        String tag = ";";
        adinfoId = "";
        for(int i=0;i<size;i++)
        {
            AdMediaInfo tempInfor = adMediaInfoList.get(i);
            if(i < size - 1)
                adinfoId += tempInfor.getId() + tag;
            else
                adinfoId += tempInfor.getId();
        }
        return adinfoId;
    }
    public String getPlanAdUrls()
    {
        int size = adMediaInfoList.size();
        String tag = ";";
        adinfoUrl = "";
        for(int i=0;i<size;i++)
        {
            AdMediaInfo tempInfor = adMediaInfoList.get(i);
            if(i < size - 1)
                adinfoUrl += tempInfor.getUrl() + tag;
            else
                adinfoUrl += tempInfor.getUrl();
        }
        return adinfoUrl;
    }
}
