package com.imovil.NEURmetrics;

public interface NavigationInterface {

    void launchSettings();
    void launchTrial();
    void onModeSelected(boolean isUserTrial);
    void onUserSelected(String userID);
    void onTrialSelected();
    void onCreateUser();

}