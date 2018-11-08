package com.znt.vodbox.bean;

import java.io.Serializable;

/**
 * Created by prize on 2018/11/8.
 */

public class TypeInfo implements Serializable
{

    private String id = "";
    private String name = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
