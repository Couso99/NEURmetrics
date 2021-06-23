package com.imovil.recordapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;

public class NewUserViewModel extends AndroidViewModel {

    User user;
    MutableLiveData<Calendar> calendar = new MutableLiveData<>();

    public NewUserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Calendar> getBirthday() {
        return calendar;
    }

    public void setBirthday(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);

        calendar.setValue(c);
    }
}
