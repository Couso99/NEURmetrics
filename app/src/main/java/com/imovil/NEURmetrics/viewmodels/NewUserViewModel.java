package com.imovil.NEURmetrics.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.imovil.NEURmetrics.models.User;

import java.util.Calendar;

public class NewUserViewModel extends AndroidViewModel {

    User user;
    MutableLiveData<Calendar> calendar = new MutableLiveData<>();
    MutableLiveData<Boolean> isUploadUser = new MutableLiveData<>();

    public NewUserViewModel(@NonNull Application application) {
        super(application);
    }

    public void initNewUser() {
        user = new User();
    }

    public User getUser() {
        return user;
    }

    public LiveData<Calendar> getBirthday() {
        return calendar;
    }

    public void setName(String name) {
        user.setName(name);
    }

    public void setSurname(String surname) {
        user.setSurname(surname);
    }

    public void setCentre(String centre) {
        user.setCentre(centre);
    }

    public void setSex(int position) {
        String sex = new String();

        switch (position) {
            case 0:
                sex = "M";
                break;
            case 1:
                sex = "F";
                break;
            case 2:
                sex = "X";
                break;
        }
        if (!sex.isEmpty())
            user.setSex(sex);
    }

    public void setBirthday(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);

        calendar.setValue(c);
        user.setBirthday(c.getTimeInMillis()/1000);
    }

    public LiveData<Boolean> getIsUploadUser() {
        return isUploadUser;
    }

    public void uploadUser() {
        isUploadUser.setValue(true);
    }
}
