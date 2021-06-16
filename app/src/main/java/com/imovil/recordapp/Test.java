package com.imovil.recordapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Test implements Serializable {

    @SerializedName("isContainsTests")
    @Expose
    private boolean isContainsTests;
    @SerializedName("tests")
    @Expose
    private List<Test> tests;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("testType")
    @Expose
    private int testType;
    @SerializedName("testID")
    @Expose
    private String testID;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("h1")
    @Expose
    private String h1;
    @SerializedName("h2")
    @Expose
    private String h2;
    @SerializedName("parametersNumber")
    @Expose
    private int parametersNumber;
    @SerializedName("parametersType")
    @Expose
    private List<String> parametersType=null;
    @SerializedName("parameters")
    @Expose
    private List<String> parameters=null;
    @SerializedName("score")
    @Expose
    private int score;
    @SerializedName("expandedScore")
    @Expose
    private List<Integer> expandedScore=null;
    @SerializedName("maxScore")
    @Expose
    private int maxScore;
    @SerializedName("scoreOptions")
    @Expose
    private List<String> scoreOptions=null;
    @SerializedName("scoreWeights")
    @Expose
    private List<Integer> scoreWeights=null;
    @SerializedName("outputFilename")
    @Expose
    private String outputFilename;
    @SerializedName("startTestTimeOffset")
    @Expose
    private long startTestTimeOffset;
    @SerializedName("stopTestTimeOffset")
    @Expose
    private long stopTestTimeOffset;

    public boolean isContainsTests() {
        return isContainsTests;
    }

    public void setContainsTests(boolean containsTests) {
        isContainsTests = containsTests;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTestType() {
        return testType;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getH2() {
        return h2;
    }

    public void setH2(String h2) {
        this.h2 = h2;
    }

    public int getParametersNumber() {
        return parametersNumber;
    }

    public void setParametersNumber(int parametersNumber) {
        this.parametersNumber = parametersNumber;
    }

    public List<String> getParametersType() {
        return parametersType;
    }

    public void setParametersType(List<String> parametersType) {
        this.parametersType = parametersType;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Integer> getExpandedScore() {
        return expandedScore;
    }

    public void setExpandedScore(List<Integer> expandedScore) {
        this.expandedScore = expandedScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public List<String> getScoreOptions() {
        return scoreOptions;
    }

    public void setScoreOptions(List<String> scoreOptions) {
        this.scoreOptions = scoreOptions;
    }

    public List<Integer> getScoreWeights() {
        return scoreWeights;
    }

    public void setScoreWeights(List<Integer> scoreWeights) {
        this.scoreWeights = scoreWeights;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public long getStartTestTimeOffset() {
        return startTestTimeOffset;
    }

    public void setStartTestTimeOffset(long startTestTimeOffset) {
        this.startTestTimeOffset = startTestTimeOffset;
    }

    public long getStopTestTimeOffset() {
        return stopTestTimeOffset;
    }

    public void setStopTestTimeOffset(long stopTestTimeOffset) {
        this.stopTestTimeOffset = stopTestTimeOffset;
    }
}