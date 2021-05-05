package com.imovil.recordapp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RecorderPlayer implements MediaPlayer.OnCompletionListener {
    private final static String LOG_TAG="Recorder";

    private static boolean isRecording=false;
    private static boolean isPlaying = false, isTimeSpecified=false;
    private static int amplitude = 0;
    private static int time_counter = 0, time_ms = 0;
    private static final int SILENCE_TIME_MS = 3000;

    private static MediaPlayer player = null;
    private static MediaRecorder mediaRecorder = null;
    private static Timer timer = null;

    public RecorderPlayer() {}

    public boolean isRecording() {
        return isRecording;
    }

    public void startPlaying(String fileName) {
        if (isPlaying) return;
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        Log.d(LOG_TAG, "Inicio reproducción");
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
            isPlaying = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    public void stopPlaying() {
        if (!isPlaying) return;
        Log.d(LOG_TAG, "Fin reproducción");
        player.release();
        player = null;
        isPlaying = false;
    }

    public void startRecording(String fileName, int recording_time_ms) {
        if (isRecording) return;
        Log.d(LOG_TAG, "Inicio grabación");
        if (timer == null)
            timer = new Timer();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        int period_ms = 500;
        mediaRecorder.start();
        isRecording = true;

        if (recording_time_ms != 0) {
            time_ms = recording_time_ms;
            isTimeSpecified = true;
        }
        else {
            time_ms = SILENCE_TIME_MS;
            isTimeSpecified = false;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                amplitude = mediaRecorder.getMaxAmplitude();
                if (amplitude<2000 || isTimeSpecified) {
                    time_counter++;
                    if (!isRecording || time_counter >= time_ms/period_ms)
                    {
                        stopRecording();
                        time_counter = 0;
                    }
                }
                else {
                    time_counter = 0;
                }
            }
        }, period_ms, period_ms);
    }

    public void stopRecording() {
        if (isRecording) {
            Log.d(LOG_TAG, "Fin grabación");
            timer.cancel();
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            timer = null;
            isRecording = false;
            return;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        mp = null;
        isPlaying = false;
        Log.d(LOG_TAG, "Fin reproducción (Final archivo)");
    }
}
