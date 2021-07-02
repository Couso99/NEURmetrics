package com.imovil.NEURmetrics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("surname")
    @Expose
    private String surname;

    @SerializedName("birthday")
    @Expose
    private long birthday;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("centre")
    @Expose
    private String centre;

    @SerializedName("_id")
    @Expose
    private LinkedTreeMap userID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCentre() {
        return centre;
    }

    public void setCentre(String centre) {
        this.centre = centre;
    }

    public String getUserID() {
        return userID.get("$oid").toString();//["$oid"];
    }
}
