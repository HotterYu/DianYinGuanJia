package com.znt.vodbox.bean;

import java.io.Serializable;
import java.net.URLDecoder;

public class MediaInfo implements Serializable {

    private String id;
    private String musicName;
    private String musicSing;
    private String musicAlbum;
    private String musicDuration;
    private String musicUrl;
    private String musicType;
    private String sourceType;
    private String status;
    private String fileSize;
    private int num;
    boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setNum(int num)
    {
        this.num = num;
    }
    public int getNum()
    {
        return num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicSing() {
        return musicSing;
    }

    public void setMusicSing(String musicSing) {
        this.musicSing = musicSing;
    }

    public String getMusicAlbum() {
        return musicAlbum;
    }

    public void setMusicAlbum(String musicAlbum) {
        this.musicAlbum = musicAlbum;
    }

    public String getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(String musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getMusicUrl() {
        return URLDecoder.decode(musicUrl);
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getMusicType() {
        return musicType;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
