package com.imovil.recordapp;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TappableTextView extends androidx.appcompat.widget.AppCompatTextView implements View.OnClickListener {
    public static final int CHECKED_SUCCESS = 0;
    public static final int UNCHECKED_ERROR = 1;
    public static final int CHECKED_ERROR = 2;

    private static final int colorChecked = Color.BLUE;
    private static final int colorUnchecked = Color.LTGRAY;
    private static final int colorSuccessChecked = Color.GREEN;
    private static final int colorErrorChecked = Color.RED;
    private static final int colorErrorUnchecked = Color.WHITE;
    private static final int colorErrorUnknown = Color.MAGENTA;

    private boolean isChecked = false;
    private String text;

    public TappableTextView(@NonNull Context context, @Nullable AttributeSet attrs, String text) {
        super(context, attrs);
        this.text = text;
        this.setText(text);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!isChecked) {
            this.setTextColor(colorChecked);
            isChecked = true;
        }
        else {
            this.setTextColor(colorUnchecked);
            isChecked = false;
        }
    }

    public void mark(int code) {
        switch (code) {
            case CHECKED_SUCCESS:
                this.setTextColor(colorSuccessChecked);
                break;
            case UNCHECKED_ERROR:
                this.setTextColor(colorErrorUnchecked);
                break;
            case CHECKED_ERROR:
                this.setTextColor(colorErrorChecked);
                break;
            default:
                this.setTextColor(colorErrorUnknown);
                break;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean strcmp(String str) {
        return (this.text.equals(str));
    }

}
