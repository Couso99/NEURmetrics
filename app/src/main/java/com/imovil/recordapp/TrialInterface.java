package com.imovil.recordapp;

public interface TrialInterface {
    String getFilePath(String fname);

    void nextTest();
    void scoreTest();
    void testsResult();
    void endTrial();

    void startRecording(String fileName, int recording_time_ms);
    void stopRecording();
    void uploadFile(String fileName, String mediaType);
}
