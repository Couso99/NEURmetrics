package com.imovil.NEURmetrics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imovil.NEURmetrics.models.Trial;

import java.io.Serializable;
import java.util.List;

public class Trials implements Serializable {
    @SerializedName("trials")
    @Expose
    private List<Trial> trials = null;

    public Trials(List<Trial> trials) {
        setUsers(trials);
    }

    public List<Trial> getTrials() {
        return trials;
    }

    public void setUsers(List<Trial> trials) {
        this.trials = trials;
    }
}

