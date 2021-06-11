package com.imovil.recordapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public Users(List<User> users) {
        setUsers(users);
    }

    public Users() {
        users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
