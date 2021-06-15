package com.imovil.recordapp;

public interface NavigationInterface {

    void launchSettings();
    void launchSelectionMode();
    void onModeSelected(boolean isUserTrial);
    void onUserSelected(String userID);

}
