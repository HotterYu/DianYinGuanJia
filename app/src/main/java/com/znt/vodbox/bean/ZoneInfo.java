package com.znt.vodbox.bean;

import java.io.Serializable;

public class ZoneInfo implements Serializable
{

    private String id = "";
    private String adminId = "";
    private String groupName = "";

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
}
