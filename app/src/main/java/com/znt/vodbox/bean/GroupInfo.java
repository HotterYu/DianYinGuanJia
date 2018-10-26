package com.znt.vodbox.bean;

import java.io.Serializable;

public class GroupInfo implements Serializable
{

    String id = "";
    String adminId = "";
    String groupName = "";
    String storeNumber = "";
    String tmlNumber = "";
    String adminName = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getTmlNumber() {
        return tmlNumber;
    }

    public void setTmlNumber(String tmlNumber) {
        this.tmlNumber = tmlNumber;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
