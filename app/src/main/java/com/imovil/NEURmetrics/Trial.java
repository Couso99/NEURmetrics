package com.imovil.NEURmetrics;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trial implements Serializable {

    @SerializedName("info")
    @Expose
    private TrialInfo trialInfo;
    @SerializedName("tests")
    @Expose
    private List<Test> tests = null;

    public TrialInfo getTrialInfo() {
        return trialInfo;
    }

    public void setTrialInfo(TrialInfo trialInfo) {
        this.trialInfo = trialInfo;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

}