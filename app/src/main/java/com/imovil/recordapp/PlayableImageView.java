package com.imovil.recordapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class PlayableImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener {
    private static RecorderPlayer recorderPlayer;
    private String audio_fname;
    private Context context;

    public PlayableImageView(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PlayableImageView(Context context, @Nullable AttributeSet attrs, String image, String audio) {
        super(context, attrs);
        this.setImageURI(Uri.fromFile(new File(context.getExternalCacheDir() + File.separator + image)));
        this.audio_fname = context.getExternalCacheDir() + File.separator +audio;
        this.recorderPlayer = new RecorderPlayer();
        this.setOnClickListener(this);
    }

    public void setAudio(String audio) {
        this.audio_fname = context.getExternalCacheDir() + File.separator +audio;
        this.recorderPlayer = new RecorderPlayer();
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        recorderPlayer.startPlaying(audio_fname);
    }
}
