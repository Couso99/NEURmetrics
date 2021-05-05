package com.imovil.recordapp;

public class TrialTimer {
    private static long startTime;

    public static void init_timer() {
        startTime = System.currentTimeMillis();
    }

    public static long getElapsedTime() {
        long elapsedTime = (System.currentTimeMillis() - startTime);
        return elapsedTime;
    }

    public static long getStartTime() {
        return startTime;
    }
}
