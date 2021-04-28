package com.imovil.recordapp;

public interface RecorderObserver {

    // 0 for not recording, 1 for recording
    void onIsRecordingChanged(int isRecording);

    //public void onIsPlayingChanged(int isPlaying);
}
