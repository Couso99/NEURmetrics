package com.imovil.recordapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Test implements Serializable {

    @SerializedName("testPieces")
    @Expose
    private List<Test> testPieces;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("testID")
    @Expose
    private String testID;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("textArray")
    @Expose
    private List<String> textArray;
    @SerializedName("score")
    @Expose
    private int score;
    @SerializedName("expandedScore")
    @Expose
    private List<Integer> expandedScore=null;
    @SerializedName("maxScore")
    @Expose
    private int maxScore;
    @SerializedName("scoreComments")
    @Expose
    private List<String> scoreComments=null;
    @SerializedName("outputFilename")
    @Expose
    private String outputFilename;

    public List<Test> getTestPieces() {
        return testPieces;
    }

    public void setTestPieces(List<Test> testPieces) {
        this.testPieces = testPieces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getTextArray() {
        return textArray;
    }

    public void setTextArray(List<String> textArray) {
        this.textArray = textArray;
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

    public List<String> getScoreComments() {
        return scoreComments;
    }

    public void setScoreComments(List<String> scoreComments) {
        this.scoreComments = scoreComments;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }
}