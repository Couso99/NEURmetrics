package com.imovil.NEURmetrics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String getDateFromTimestamp(long startTime) {
        Date date = new Date(startTime);
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy  hh:mm");
        return f.format(date);
    }
}
