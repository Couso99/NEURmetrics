package com.imovil.NEURmetrics.ui.selectionActivity.fragments;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.viewmodels.SharedSelectionViewModel;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedSelectionViewModel model;

    public static SelectUserFragment newInstance() {
        SelectUserFragment fragment = new SelectUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(SharedSelectionViewModel.class);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        model.onChangePreferences();
    }
}
