package com.imovil.recordapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrialInfo implements Serializable {

    @SerializedName("trialID")
    @Expose
    private String trialID;
    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("totalScore")
    @Expose
    private int totalScore;
    @SerializedName("totalMaxScore")
    @Expose
    private int totalMaxScore;
    @SerializedName("isTrialScored")
    @Expose
    private boolean isTrialScored;
    @SerializedName("startTime")
    @Expose
    private long startTime;

    public String getTrialID() {
        return trialID;
    }

    public void setTrialID(String trialID) {
        this.trialID = trialID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalMaxScore() {
        return totalMaxScore;
    }

    public void setTotalMaxScore(int totalMaxScore) {
        this.totalMaxScore = totalMaxScore;
    }

    public boolean isTrialScored() {
        return isTrialScored;
    }

    public void setTrialScored(boolean trialScored) {
        isTrialScored = trialScored;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
