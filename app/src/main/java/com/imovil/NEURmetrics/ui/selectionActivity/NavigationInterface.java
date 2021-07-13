package com.imovil.NEURmetrics.ui.selectionActivity;

public interface NavigationInterface {

    void launchSettings();
    void launchTrial();
    void onModeSelected(boolean isUserTrial);
    void onUserSelected(String userID);
    void onTrialSelected();
    void onCreateUser();

}
