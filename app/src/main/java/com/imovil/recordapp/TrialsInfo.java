package com.imovil.recordapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TrialsInfo implements Serializable {

    @SerializedName("trialsInfo")
    @Expose
    private List<TrialInfo> trialsInfo;

    public List<TrialInfo> getTrialsInfo() {
        return trialsInfo;
    }

    public void setTrialsInfo(List<TrialInfo> trialsInfo) {
        this.trialsInfo = trialsInfo;
    }
}
